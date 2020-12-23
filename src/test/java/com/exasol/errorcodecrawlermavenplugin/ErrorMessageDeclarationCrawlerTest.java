package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class ErrorMessageDeclarationCrawlerTest {
    private static final Path PROJECT_DIRECTORY = Path.of(".").toAbsolutePath();
    private static final ErrorCrawler ERROR_CRAWLER = new ErrorCrawler(PROJECT_DIRECTORY, new String[] {});

    @Test
    void test() {
        final ErrorCrawler.Result result = ERROR_CRAWLER.crawl(
                Path.of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java").toAbsolutePath());
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorCodes();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getErrorCode(), equalTo("E-TEST-1")),
                () -> assertThat(first.getSourceFile(),
                        equalTo("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java")),
                () -> assertThat(first.getLine(), equalTo(10))//
        );
    }

    @Test
    void testIllegalErrorCodeFromFunction() {
        final ErrorCrawler.Result result = ERROR_CRAWLER.crawl(Path
                .of("src/test/java/com/exasol/errorcodecrawlermavenplugin/examples/IllegalErrorCodeFromFunction.java"));
        final List<String> messages = result.getFindings().stream().map(Finding::getMessage)
                .collect(Collectors.toList());
        assertThat(messages, containsInAnyOrder(
                "E-ECM-2: ExaError#messageBuilder(String)'s parameter must be a literal. (IllegalErrorCodeFromFunction.java:10)"));
    }
}