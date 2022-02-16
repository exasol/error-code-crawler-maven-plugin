package com.exasol.errorcodecrawlermavenplugin.validation;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.SingleErrorCodeConfig;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

class HighestIndexValidatorTest {
    private static final String SOURCE_FILE = "src/file.java";
    private static final int SOURCE_LINE = 42;

    @Test
    void codeHasHigherIndex() {
        final List<Finding> findings = validate(config("EXA", 5), declaration("E-EXA-6"));
        assertFindings(findings,
                "E-ECM-54: Highest index for tag 'EXA' configured in error_code_config.yml is 5 but code E-EXA-6 in file.java:42 is higher. Update highest index in error_code_config.yml.");
    }

    @Test
    void codeHasLowerIndexThanHighestIndex() {
        assertNoFindings(validate(config("EXA", 5), declaration("E-EXA-4")));
    }

    @Test
    void codeHasEqualIndex() {
        assertNoFindings(validate(config("EXA", 5), declaration("E-EXA-5")));
    }

    @Test
    void noHighestIndexConfigured() {
        assertNoFindings(validate(config("EXA", 0), declaration("E-EXA-5")));
    }

    @Test
    void errorTagNotConfigured() {
        assertNoFindings(validate(config("EXA", 4), declaration("E-UNKNOWN-5")));
    }

    @Test
    void ignoresInvalidCodeFormat() {
        assertNoFindings(validate(config("EXA", 4), declaration("I-EXA-5")));
    }

    private void assertNoFindings(final List<Finding> findings) {
        assertFindings(findings);
    }

    private void assertFindings(final List<Finding> findings, final String... expectedFindingMessages) {
        final List<String> messages = findings.stream().map(Finding::getMessage).collect(toList());
        assertAll(() -> assertThat(messages, hasSize(expectedFindingMessages.length)),
                () -> assertThat(messages, hasItems(expectedFindingMessages)));
    }

    private ErrorMessageDeclaration declaration(final String identifier) {
        return ErrorMessageDeclaration.builder().identifier(identifier).setPosition(SOURCE_FILE, SOURCE_LINE).build();
    }

    private List<Finding> validate(final ErrorCodeConfig config, final ErrorMessageDeclaration declaration) {
        return new HighestIndexValidator(config).validate(List.of(declaration));
    }

    private ErrorCodeConfig config(final String tag, final int highestIndex) {
        return new ErrorCodeConfig(Map.of(tag, new SingleErrorCodeConfig(emptyList(), highestIndex)));
    }
}
