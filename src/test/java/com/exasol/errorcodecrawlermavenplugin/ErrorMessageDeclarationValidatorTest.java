package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class ErrorMessageDeclarationValidatorTest {

    @Test
    void testDuplicates() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().errorCode("E-TEST-1").setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().errorCode("E-TEST-1").setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'E-TEST-1' was declared multiple times: Test.java:1, Test.java:5.");
    }

    private void assertValidationHasFindingsWithMessage(final List<ErrorMessageDeclaration> errors,
            final String... expectedMessages) {
        final List<Finding> findings = new ExasolErrorValidator().validate(errors);
        assertThat(findings.stream().map(Finding::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(expectedMessages));
    }
}