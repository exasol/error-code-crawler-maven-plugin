package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

//[utest->dsn~identifier-validator~1]
class ErrorIdentifierValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = { "E-TEST-1", "E-ZERO-0", "E-TEST1-2", "E-TEST-MOD-1", "E-lower-case-2", "W-WARN-3",
            "F-FAIL-4", "E-VERYVERYLONGTAG-5", "E-TOO-MANY-MODULES-6" })
    void testValidErrorIdentifiers(final String identifier) {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier(identifier).build();
        assertThat(new ErrorIdentifierValidator().validate(List.of(declaration)), empty());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '`', value = {
            "Q-TEST-1, `E-ECMOJ-2: Illegal error code 'Q-TEST-1'. The codes must start with 'W-', 'E-' or 'F-'.`",
            "123, `E-ECMOJ-1: The error code '123' has an invalid format.`" //
    })
    void testInvalidErrorIdentifier(final String identifier, final String expectedErrorMessage) {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier(identifier)
                .setPosition("Test.java", 1).build();
        final List<Finding> findings = new ErrorIdentifierValidator().validate(List.of(declaration));
        final List<String> findingMessages = findings.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(expectedErrorMessage + " (Test.java:1)"));
    }
}
