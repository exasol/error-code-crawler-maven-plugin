package com.exasol.errorcodecrawlermavenplugin.crawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.*;

class ErrorMessageDeclarationCrawlerTest {
    private static final Path PROJECT_DIRECTORY = Path.of(".").toAbsolutePath();
    private static final ErrorMessageDeclarationCrawler DECLARATION_CRAWLER = new ErrorMessageDeclarationCrawler(
            PROJECT_DIRECTORY, new String[] {}, 11, Collections.emptyList());
    private static final String TEST_DIR = "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/";

    @Test
    void testCrawlValidCode() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, "Test1.java").toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getErrorCode(), equalTo(new ErrorCode(ErrorCode.Type.E, "TEST", 1))),
                () -> assertThat(first.getSourceFile(),
                        equalTo("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java")),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getDeclaringPackage(),
                        equalTo("com.exasol.errorcodecrawlermavenplugin.examples")), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }

    @ParameterizedTest
    @CsvSource({ //
            "TestWithTwoMessageCalls.java, compound message", //
            "TestWithConcatenatedMessage.java, concatenated message", //
            "TestWithMultiplePartConcatenatedMessage.java, concatenated message 2", //
            "TestWithMessageFromConstant.java, message from constant" })
    void testCrawlTwoMessages(final String testFile, final String expectedMessage) {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, testFile).toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void testCrawlMitigations() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, "TestWithMitigations.java").toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMitigations(), contains("That's how to fix it.", "One more hint."));
    }

    @ParameterizedTest
    @CsvSource({ //
            "TestWithNamedParameter.java,,true", //
            "TestWithNamedUnquotedParameter.java,,false", //
            "TestWithNamedParameterWithDescription.java,just a parameter,true", //
            "TestWithNamedUnquotedParameterWithDescription.java,just a parameter,false",//
    })
    void testCrawlNamedParameter(final String testFile, final String expectedDescription,
            final boolean expectedQuoted) {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, testFile).toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(),
                contains(new NamedParameter("test", expectedDescription, expectedQuoted)));
    }

    @Test
    void testIllegalErrorCodeFromFunction() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, "IllegalErrorCodeFromFunction.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-16: Invalid parameter for messageBuilder(java.lang.String) call. (IllegalErrorCodeFromFunction.java:10) Only literals, string-constants and concatenation of these two are supported."));
    }

    @Test
    void testInvalidErrorCodeSyntax() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, "InvalidErrorCodeSyntax.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-10: The error code 'E-TEST-X' has an invalid format. (InvalidErrorCodeSyntax.java:10)"));
    }

    @Test
    void testLanguageLevel() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of(TEST_DIR, "Java10.java").toAbsolutePath());
        assertDoesNotThrow(result::getErrorMessageDeclarations);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java",
            "**/IllegalErrorCodeFromFunction.java", "src/test/java/com/exasol/errorcodecrawlermavenplugin/**" })
    void testIgnoredFiled(final String excludeGlob) {
        final ErrorMessageDeclarationCrawler crawler = new ErrorMessageDeclarationCrawler(PROJECT_DIRECTORY,
                new String[] {}, 11, List.of(excludeGlob));
        final ErrorMessageDeclarationCrawler.Result result = crawler
                .crawl(Path.of(TEST_DIR, "IllegalErrorCodeFromFunction.java"));
        assertTrue(result.getFindings().isEmpty());
    }
}