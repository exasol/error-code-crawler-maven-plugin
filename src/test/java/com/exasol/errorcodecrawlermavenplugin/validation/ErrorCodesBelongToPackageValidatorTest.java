package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.SingleErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class ErrorCodesBelongToPackageValidatorTest {
    private static final ErrorCode E_TEST_1 = new ErrorCode(ErrorCode.Type.E, "TEST", 1);
    private static final String EXAMPLE_PACKAGE = "com.example";
    private static final String OTHER_PACKAGE = "com.other";
    private static final ErrorCodeConfig CONFIG = new ErrorCodeConfig(
            Map.of("TEST", new SingleErrorCodeConfig(List.of(EXAMPLE_PACKAGE), 1), "OTHER",
                    new SingleErrorCodeConfig(List.of(OTHER_PACKAGE), 1)));

    public static void assertValidationHasFindingsWithMessage(final List<ErrorMessageDeclaration> errors,
            final String... expectedMessages) {
        final List<Finding> findings = new ErrorCodesBelongToPackageValidator(CONFIG).validate(errors);
        assertThat(findings.stream().map(Finding::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(expectedMessages));
    }

    @Test
    void testValid() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().errorCode(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().errorCode(new ErrorCode(ErrorCode.Type.E, "TEST", 2))
                        .declaringPackage(EXAMPLE_PACKAGE).setPosition("src/test/Test.java", 5).build());
        assertThat(new ErrorCodesBelongToPackageValidator(CONFIG).validate(errors), empty());
    }

    @Test
    void testNestedPackage() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().errorCode(E_TEST_1).declaringPackage("com.example.my")
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().errorCode(new ErrorCode(ErrorCode.Type.E, "TEST", 2))
                        .declaringPackage(EXAMPLE_PACKAGE).setPosition("src/test/Test.java", 5).build());
        assertThat(new ErrorCodesBelongToPackageValidator(CONFIG).validate(errors), empty());
    }

    @Test
    void testUnknownTag() {
        final List<ErrorMessageDeclaration> errors = List
                .of(ErrorMessageDeclaration.builder().errorCode(new ErrorCode(ErrorCode.Type.E, "UNKNOWN", 1))
                        .declaringPackage(EXAMPLE_PACKAGE).setPosition("src/test/Test.java", 1).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-12: The error tag 'UNKNOWN' was not declared in the errorCodeConfig.yml. Check if it is just a typo and if not add an entry for 'UNKNOWN' and package 'com.example'.");
    }

    @Test
    void testPackageAndTagMismatch() {
        final List<ErrorMessageDeclaration> errors = List.of(ErrorMessageDeclaration.builder().errorCode(E_TEST_1)
                .declaringPackage(OTHER_PACKAGE).setPosition("src/test/Test.java", 1).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-13: According to this project's errorCodeConfig.yml, the error tag 'TEST' is not allowed for the package 'com.other'. The config allows the tag 'TEST' for the following packages: ['com.example'].  For this package it allows the tag 'OTHER'.");
    }
}