package com.exasol.errorcodecrawlermavenplugin.validation;

import static java.util.Collections.emptyList;
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
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

// [utest->dsn~duplication-validator~1]
class DuplicatesValidatorTest {
    private static final String E_TEST_1 = "E-TEST-1";
    private static final String EXAMPLE_PACKAGE = "com.example";
    private static final int HIGHEST_INDEX = 41;

    public static void assertValidationHasFindingsWithMessage(final List<ErrorMessageDeclaration> errors,
            final String... expectedMessages) {
        final List<Finding> findings = validate(errors);
        assertThat(findings.stream().map(Finding::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(expectedMessages));
    }

    private static List<Finding> validate(final List<ErrorMessageDeclaration> errors) {
        final ErrorCodeConfig config = new ErrorCodeConfig(
                Map.of("TEST", new SingleErrorCodeConfig(emptyList(), HIGHEST_INDEX)));
        return new DuplicatesValidator(config).validate(errors);
    }

    @Test
    void testValid() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier("E-TEST-2").declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertThat(validate(errors), empty());
    }

    @Test
    void testDuplicates() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: src/test/Test.java:1, src/test/Test.java:5."
                        + " Next available index for error tag 'TEST' is 42.");
    }

    @Test
    void testDuplicatesWithDifferentType() {
        final List<ErrorMessageDeclaration> errors = List.of(
                ErrorMessageDeclaration.builder().identifier(E_TEST_1).declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 1).build(),
                ErrorMessageDeclaration.builder().identifier("F-TEST-1").declaringPackage(EXAMPLE_PACKAGE)
                        .setPosition("src/test/Test.java", 5).build());
        assertValidationHasFindingsWithMessage(errors,
                "E-ECM-4: Found duplicate error code: 'TEST-1' was declared multiple times: src/test/Test.java:1, src/test/Test.java:5."
                        + " Next available index for error tag 'TEST' is 42.");
    }
}
