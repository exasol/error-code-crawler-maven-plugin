package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.errorcodecrawlermavenplugin.model.*;

class ErrorMessageDeclarationCrawlerTest {
    private static final Path PROJECT_DIRECTORY = Path.of(".").toAbsolutePath();
    private static final ErrorMessageDeclarationCrawler DECLARATION_CRAWLER = new ErrorMessageDeclarationCrawler(
            PROJECT_DIRECTORY, new String[] {}, 11, Collections.emptyList());

    @Test
    void testCrawlValidCode() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java").toAbsolutePath());
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

    @Test
    void testCrawlTwoMessages() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithTwoMessageCalls.java")
                        .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMessage(), equalTo("compound message"));
    }

    @Test
    void testCrawlConcatenatedMessage() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithConcatenatedMessage.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMessage(), equalTo("concatenated message"));
    }

    @Test
    void testCrawlMultiplePartConcatenatedMessage() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path.of(
                "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithMultiplePartConcatenatedMessage.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMessage(), equalTo("concatenated message 2"));
    }

    @Test
    void testCrawlMessageFromConstant() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithMessageFromConstant.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMessage(), equalTo("message from constant"));
    }

    @Test
    void testCrawlMitigations() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER
                .crawl(Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithMitigations.java")
                        .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMitigations(), contains("That's how to fix it.", "One more hint."));
    }

    @Test
    void testCrawlNamedParameter() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithNamedParameter.java")
                        .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(), contains(new NamedParameter("test", null, true)));
    }

    @Test
    void testCrawlNamedUnquotedParameter() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithNamedUnquotedParameter.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(), contains(new NamedParameter("test", null, false)));
    }

    @Test
    void testCrawlNamedQuotedParameterWithDescription() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path.of(
                "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithNamedParameterWithDescription.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(), contains(new NamedParameter("test", "just a parameter", true)));
    }

    @Test
    void testCrawlNamedUnquotedParameterWithDescription() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path.of(
                "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/TestWithNamedUnquotedParameterWithDescription.java")
                .toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(), contains(new NamedParameter("test", "just a parameter", false)));
    }

    @Test
    void testIllegalErrorCodeFromFunction() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-2: ExaError#messageBuilder(String)'s parameter must be a literal. (IllegalErrorCodeFromFunction.java:10)"));
    }

    @Test
    void testInvalidErrorCodeSyntax() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/InvalidErrorCodeSyntax.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-10: The error code 'E-TEST-X' has an invalid format. (InvalidErrorCodeSyntax.java:10)"));
    }

    @Test
    void testLanguageLevel() {
        final ErrorMessageDeclarationCrawler.Result result = DECLARATION_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Java10.java").toAbsolutePath());
        assertDoesNotThrow(result::getErrorMessageDeclarations);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java",
            "**/IllegalErrorCodeFromFunction.java", "src/test/java/com/exasol/errorcodecrawlermavenplugin/**" })
    void testIgnoredFiled(final String excludeGlob) {
        final ErrorMessageDeclarationCrawler crawler = new ErrorMessageDeclarationCrawler(PROJECT_DIRECTORY,
                new String[] {}, 11, List.of(excludeGlob));
        final ErrorMessageDeclarationCrawler.Result result = crawler.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java"));
        assertTrue(result.getFindings().isEmpty());
    }
}