package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class ErrorMessageDeclarationCrawlerTest {
    private static final Path PROJECT_DIRECTORY = Path.of(".").toAbsolutePath();
    private static final ErrorMessageDeclarationCrawler ERROR_CRAWLER = new ErrorMessageDeclarationCrawler(
            PROJECT_DIRECTORY, new String[] {}, 11);

    @Test
    void testCrawlValidCode() {
        final ErrorMessageDeclarationCrawler.Result result = ERROR_CRAWLER.crawl(
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
                        equalTo("com.exasol.errorcodecrawlermavenplugin.examples"))//
        );
    }

    @Test
    void testIllegalErrorCodeFromFunction() {
        final ErrorMessageDeclarationCrawler.Result result = ERROR_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-2: ExaError#messageBuilder(String)'s parameter must be a literal. (IllegalErrorCodeFromFunction.java:10)"));
    }

    @Test
    void testInvalidErrorCodeSyntax() {
        final ErrorMessageDeclarationCrawler.Result result = ERROR_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/InvalidErrorCodeSyntax.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-10: The error code 'E-TEST-X' has an invalid format. (InvalidErrorCodeSyntax.java:10)"));
    }

    @Test
    void testLanguageLevel() {
        final ErrorMessageDeclarationCrawler.Result result = ERROR_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Java10.java").toAbsolutePath());
        assertDoesNotThrow(result::getErrorMessageDeclarations);
    }
}