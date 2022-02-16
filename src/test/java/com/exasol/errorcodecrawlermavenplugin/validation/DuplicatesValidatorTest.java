package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

// [utest->dsn~duplication-validator~1]
class DuplicatesValidatorTest {
    private static final String E_TEST_1 = "E-TEST-1";
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
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier("E-TEST-2").declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertThat(new DuplicatesValidator().validate(errors), empty());
    }

    @Test
    void testDuplicates() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: src/test/Test.java:1, src/test/Test.java:5.");
    }

    @Test
    void testDuplicatesWithDifferentType() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier("F-TEST-1").declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: src/test/Test.java:1, src/test/Test.java:5.");
    }
}
