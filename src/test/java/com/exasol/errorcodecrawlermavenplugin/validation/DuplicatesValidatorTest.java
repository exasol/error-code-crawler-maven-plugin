package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

// [utest->dsn~duplication-validator~1]
class DuplicatesValidatorTest {
    private static final ErrorCode E_TEST_1 = new ErrorCode(ErrorCode.Type.E, "TEST", 1);
    private static final String EXAMPLE_PACKAGE = "com.example";

    public static void assertValidationHasFindingsWithMessage(final List<ErrorMessageDeclaration> errors,
            final String... expectedMessages) {
        final List<Finding> findings = new DuplicatesValidator().validate(errors);
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
        assertThat(new DuplicatesValidator().validate(errors), empty());
    }

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
}