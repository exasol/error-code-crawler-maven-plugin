package com.exasol.errorcodecrawlermavenplugin.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ErrorCodeConfigTest {
    private static final ErrorCodeConfig VALID_ERROR_CODE_CONFIG = new ErrorCodeConfig(
            Map.of("EX", new SingleErrorCodeConfig(List.of("com.exasol.example"), 1), //
                    "EX-S1", new SingleErrorCodeConfig(List.of("com.exasol.example.example1"), 2)));

    @Test
    void testGetPackages() {
        assertThat(VALID_ERROR_CODE_CONFIG.getPackagesForErrorTag("EX"), containsInAnyOrder("com.exasol.example"));
    }

    @ParameterizedTest
    @CsvSource({ //
            "com.exasol.example.example1, EX-S1", //
            "com.exasol.example, EX", //
            "com.exasol.example.other, EX"//
    })
    void testGetErrorCode(final String packageName, final String expectedErrorCode) {
        assertThat(VALID_ERROR_CODE_CONFIG.getErrorTagForPackage(packageName).orElseThrow(),
                equalTo(expectedErrorCode));
    }

    @Test
    void testGetErrorCodeWithUnknownPackageNames() {
        assertThat(VALID_ERROR_CODE_CONFIG.getErrorTagForPackage("com.other").isEmpty(), equalTo(true));
    }

    @Test
    void testGetHighestIndex() {
        assertThat(VALID_ERROR_CODE_CONFIG.getHighestIndexForErrorTag("EX"), equalTo(1));
    }

    @ParameterizedTest
    @CsvSource({ //
            "EX, true", //
            "OTHER, false",//
    })
    void testHasErrorTag(final String tag, final boolean expectedResult) {
        assertThat(VALID_ERROR_CODE_CONFIG.hasErrorTag(tag), equalTo(expectedResult));
    }

    @Test
    void testSamePackageDeclaredForTwoCodes() {
        final Map<String, SingleErrorCodeConfig> errorTags = Map.of("EX",
                new SingleErrorCodeConfig(List.of("com.exasol.example"), 1), //
                "EX-S1", new SingleErrorCodeConfig(List.of("com.exasol.example"), 2));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ErrorCodeConfig(errorTags));
        assertThat(exception.getMessage(),
                startsWith("E-ECM-8: Two error codes cover the same package: 'com.exasol.example' was declared for"));
    }
}
