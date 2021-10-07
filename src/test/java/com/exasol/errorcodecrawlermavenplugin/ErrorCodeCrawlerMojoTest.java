package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ErrorCodeCrawlerMojoTest {

    // [utest->dsn~skip-execution~1]
    @ParameterizedTest
    @CsvSource({ //
            "true, false", //
            "false, true",//
    })
    void testIsEnabled(final String propertyValue, final boolean expectedResult) {
        final ErrorCodeCrawlerMojo errorCodeCrawler = new ErrorCodeCrawlerMojo();
        errorCodeCrawler.skip = propertyValue;
        assertThat(errorCodeCrawler.isEnabled(), equalTo(expectedResult));
    }

    // [utest->dsn~skip-execution~1]
    @Test
    void testIsEnabledWithInvalidValue() {
        final ErrorCodeCrawlerMojo errorCodeCrawler = new ErrorCodeCrawlerMojo();
        errorCodeCrawler.skip = "illegal-value";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                errorCodeCrawler::isEnabled);
        assertThat(exception.getMessage(), equalTo(
                "E-ECM-51: Invalid value 'illegal-value' for property 'error-code-crawler.skip'. Please set the property to 'true' or 'false'."));
    }
}