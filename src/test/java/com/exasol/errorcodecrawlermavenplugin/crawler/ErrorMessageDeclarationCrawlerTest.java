package com.exasol.errorcodecrawlermavenplugin.crawler;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;
import com.exsol.errorcodemodel.NamedParameter;

// [utest->dsn~error-declaration-crawler~1]
class ErrorMessageDeclarationCrawlerTest {
    private static final Path PROJECT_DIRECTORY = Path.of(".").toAbsolutePath();

    private static final ErrorMessageDeclarationCrawler DECLARATION_CRAWLER = new ErrorMessageDeclarationCrawler(
            PROJECT_DIRECTORY, PROJECT_DIRECTORY, emptyList(), 11, Collections.emptyList());

    private static final String TEST_DIR = "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/";

    @TempDir
    Path projectDir;

    @Test
    void testCrawlValidCode() {
        final Path path = Path.of(TEST_DIR, "Test1.java");
        final ErrorMessageDeclarationCrawler.Result result = crawl(path);
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(first.getSourceFile(), equalTo(path.toString())),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getDeclaringPackage(),
                        equalTo("com.exasol.errorcodecrawlermavenplugin.examples")), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }

    /*@Test
    void testSubProjectCrawlValidCode() throws IOException {
        Path subProjectDir = projectDir.resolve("sub-project");
        Path subProjectTestSrcJava = subProjectDir.resolve(Path.of("src", "test", "java"));
        Path subProjectTestSrcPackage = subProjectTestSrcJava
                .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        subProjectTestSrcPackage.toFile().mkdirs();
        ErrorMessageDeclarationCrawler subProjectDeclarationCrawler = new ErrorMessageDeclarationCrawler(
                subProjectDir.getParent(), subProjectDir, emptyList(), 11, Collections.emptyList());
        final Path path = subProjectTestSrcPackage.resolve("Test1.java").toAbsolutePath();
        Files.copy(Path.of(TEST_DIR).resolve("Test1.java"), path, StandardCopyOption.REPLACE_EXISTING);
        final ErrorMessageDeclarationCrawler.Result result = subProjectDeclarationCrawler.crawl(List.of(path));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(projectDir.relativize(Path.of(first.getSourceFile())).toString(), equalTo(projectDir.relativize(path).toString())),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getDeclaringPackage(),
                        equalTo("com.exasol.errorcodecrawlermavenplugin.examples")), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }*/

    @ParameterizedTest
    @CsvSource({ //
            "TestWithTwoMessageCalls.java, compound message", //
            "TestWithConcatenatedMessage.java, concatenated message", //
            "TestWithMultiplePartConcatenatedMessage.java, concatenated message 2", //
            "TestWithMessageFromConstant.java, message from constant" })
    void testCrawlMessage(final String testFile, final String expectedMessage) {
        final ErrorMessageDeclarationCrawler.Result result = crawl(Path.of(TEST_DIR, testFile));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(first.getMessage(), equalTo(expectedMessage)),
                () -> assertThat(first.getNamedParameters(), empty())//
        );
    }

    @Test
    void testCrawlMessageWithDirectParameters() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(
                Path.of(TEST_DIR, "TestWithMessageAndDirectParameter.java"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(first.getMessage(),
                        equalTo("message with parameters {{param1}} {{param2|uq}} {{param3}}")),
                () -> assertThat(first.getNamedParameters(),
                        containsInAnyOrder(new NamedParameter("param1", null), new NamedParameter("param2", null)))//
        );
    }

    @Test
    void testCrawlMitigations() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(Path.of(TEST_DIR, "TestWithMitigations.java"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getMitigations(), contains("That's how to fix it.", "One more hint."));
    }

    @Test
    void testCrawlMitigationWithDirectParameters() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(
                Path.of(TEST_DIR, "TestWithMitigationAndDirectParameter.java"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(first.getMitigations(), contains("That's how to fix it: {{hint}}")),
                () -> assertThat(first.getNamedParameters(), contains(new NamedParameter("hint", null)))//
        );
    }

    @ParameterizedTest
    @CsvSource({ //
            "TestWithNamedParameter.java,", //
            "TestWithNamedParameterWithDescription.java,just a parameter", //
    })
    void testCrawlNamedParameter(final String testFile, final String expectedDescription) {
        final ErrorMessageDeclarationCrawler.Result result = crawl(Path.of(TEST_DIR, testFile));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertThat(first.getNamedParameters(), contains(new NamedParameter("test", expectedDescription)));
    }

    @Test
    void testIllegalErrorCodeFromFunction() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(
                Path.of(TEST_DIR, "IllegalErrorCodeFromFunction.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-16: Invalid parameter for messageBuilder(java.lang.String) call. (IllegalErrorCodeFromFunction.java:10) Only literals, string-constants and concatenation of these two are supported."));
    }

    @Test
    void testLanguageLevel() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(Path.of(TEST_DIR, "Java10.java"));
        assertDoesNotThrow(result::getErrorMessageDeclarations);
    }

    @Test
    @EnabledOnJre({ JRE.JAVA_17 })
    void testLanguageLevelJava17() {
        final Path path = Path.of("src/test/resources/java17/").toAbsolutePath();
        final ErrorMessageDeclarationCrawler crawler = new ErrorMessageDeclarationCrawler(path, path, emptyList(), 17,
                emptyList());
        final ErrorMessageDeclarationCrawler.Result result = crawler.crawl(List.of(path));
        assertDoesNotThrow(result::getErrorMessageDeclarations);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java",
            "**/IllegalErrorCodeFromFunction.java", "src/test/java/com/exasol/errorcodecrawlermavenplugin/**" })
    void testIgnoredFiled(final String excludeGlob) {
        final ErrorMessageDeclarationCrawler crawler = new ErrorMessageDeclarationCrawler(PROJECT_DIRECTORY, PROJECT_DIRECTORY,
                emptyList(), 11, List.of(excludeGlob));
        final ErrorMessageDeclarationCrawler.Result result = crawler
                .crawl(List.of(Path.of(TEST_DIR, "IllegalErrorCodeFromFunction.java")));
        assertTrue(result.getFindings().isEmpty());
    }

    @Test
    void testIllegalAssigningOfBuilderToVariable() {
        final ErrorMessageDeclarationCrawler.Result result = crawl(
                Path.of(TEST_DIR, "TestWithBuilderAssignedToVariable.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                startsWith("E-ECM-31: Invalid incomplete builder call at TestWithBuilderAssignedToVariable.java:")));
    }

    private ErrorMessageDeclarationCrawler.Result crawl(final Path path) {
        return DECLARATION_CRAWLER.crawl(List.of(path.toAbsolutePath()));
    }
}
