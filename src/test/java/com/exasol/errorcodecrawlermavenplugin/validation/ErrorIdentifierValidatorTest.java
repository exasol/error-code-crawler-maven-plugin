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

//[utest->dsn~identifier-validator~2]
class ErrorIdentifierValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = { "E-TEST-1", "E-ZERO-0", "E-TEST1-2", "E-TEST-MOD-1", "W-WARN-3", "F-FAIL-4", "EXA-5" })
    void testValidErrorIdentifiers(final String identifier) {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier(identifier).build();
        assertThat(new ErrorIdentifierValidator().validate(List.of(declaration)), empty());
    }

    @ParameterizedTest
    @CsvSource(quoteCharacter = '`', value = {
            "Q-TEST-1, `E-ECMOJ-2: Illegal error code 'Q-TEST-1'. The codes must start with 'W-', 'E-' or 'F-'.`",
            "E-lower-case-2, `E-ECMOJ-1: The error code 'E-lower-case-2' has an invalid format. Use a code like 'EXA-1', 'E-EXA-1' or 'W-EXA-MOD-2', tags can have max. 10 chars.`",
            "E-TOO-MANY-MODULES-6, `E-ECMOJ-1: The error code 'E-TOO-MANY-MODULES-6' has an invalid format. Use a code like 'EXA-1', 'E-EXA-1' or 'W-EXA-MOD-2', tags can have max. 10 chars.`",
            "E-VERYVERYLONGTAG-5, `E-ECMOJ-1: The error code 'E-VERYVERYLONGTAG-5' has an invalid format. Use a code like 'EXA-1', 'E-EXA-1' or 'W-EXA-MOD-2', tags can have max. 10 chars.`",
            "123, `E-ECMOJ-1: The error code '123' has an invalid format. Use a code like 'EXA-1', 'E-EXA-1' or 'W-EXA-MOD-2', tags can have max. 10 chars.`" //
    })
    void testInvalidErrorIdentifier(final String identifier, final String expectedErrorMessage) {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier(identifier)
                .setPosition("Test.java", 1).build();
        final List<Finding> findings = new ErrorIdentifierValidator().validate(List.of(declaration));
        final List<String> findingMessages = findings.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(expectedErrorMessage + " (Test.java:1)"));
    }
}
