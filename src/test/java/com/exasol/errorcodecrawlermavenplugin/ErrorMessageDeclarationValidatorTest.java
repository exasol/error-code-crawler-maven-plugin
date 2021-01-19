package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.SingleErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class ErrorMessageDeclarationValidatorTest {

    private static final ErrorCode E_TEST_1 = new ErrorCode(ErrorCode.Type.E, "TEST", 1);
    private static final String EXAMPLE_PACKAGE = "com.example";
    private static final String OTHER_PACKAGE = "com.other";
    private static final ErrorCodeConfig CONFIG = new ErrorCodeConfig(
            Map.of("TEST", new SingleErrorCodeConfig(List.of(EXAMPLE_PACKAGE), 1), "OTHER",
                    new SingleErrorCodeConfig(List.of(OTHER_PACKAGE), 1)));

    @Test
    void testDuplicates() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().errorCode(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().errorCode(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: Test.java:1, Test.java:5.");
    }

    @Test
    void testDuplicatesWithDifferentType() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().errorCode(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().errorCode(new ErrorCode(ErrorCode.Type.F, "TEST", 1))
                        .declaringPackage(EXAMPLE_PACKAGE).setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: Test.java:1, Test.java:5.");
    }

    private void assertValidationHasFindingsWithMessage(final List<ErrorMessageDeclaration> errors,
            final String... expectedMessages) {
        final List<Finding> findings = new ErrorMessageDeclarationValidator(CONFIG).validate(errors);
        assertThat(findings.stream().map(Finding::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(expectedMessages));
    }

    @Test
    void testUnknownTag() {
        final List<ErrorMessageDeclaration> errors = List
                .of(ErrorMessageDeclaration.builder().errorCode(new ErrorCode(ErrorCode.Type.E, "UNKNOWN", 1))
                        .declaringPackage(EXAMPLE_PACKAGE).setPosition("src/test/Test.java", 1).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-12: The error tag 'UNKNOWN' was not declared in the errorCodeConfig.yml. Check if it is just a type and if not add an entry for 'UNKNOWN' and package 'com.example'.");
    }

    @Test
    void testPackageAndTagMismatch() {
        final List<ErrorMessageDeclaration> errors = List.of(ErrorMessageDeclaration.builder().errorCode(E_TEST_1)
                .declaringPackage(OTHER_PACKAGE).setPosition("src/test/Test.java", 1).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-13: According to this project's errorCodeConfig.yml, the error tag 'TEST' is not allowed for the package 'com.other'. The config allows the tag 'TEST' for the following packages: ['com.example'].  For this package it allows the tag 'OTHER'.");
    }
}