package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.model.ExasolError;

class ErrorValidatorTest {

    @Test
    void testDuplicates() {
        final List<ExasolError> errors = List.of(
                ExasolError.builder().errorCode("E-TEST-1").setPosition("src/test/Test.java", 1).build(),
                ExasolError.builder().errorCode("E-TEST-1").setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'E-TEST-1' was declared multiple times: Test.java:1, Test.java:5.");
    }

    private void assertValidationHasFindingsWithMessage(final List<ExasolError> errors,
            final String... expectedMessages) {
        final List<Finding> findings = new ErrorValidator().validate(errors);
        assertThat(findings.stream().map(Finding::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(expectedMessages));
    }
}