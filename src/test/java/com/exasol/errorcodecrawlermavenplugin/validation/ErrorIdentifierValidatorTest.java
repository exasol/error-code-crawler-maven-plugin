package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

//[utest->dsn~identifier-validator~1]
class ErrorIdentifierValidatorTest {
    @Test
    void testValid() {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier("E-TEST-1").build();
        assertThat(new ErrorIdentifierValidator().validate(List.of(declaration)), empty());
    }

    @Test
    void testInvalidType() {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier("Q-TEST-1")
                .setPosition("Test.java", 1).build();
        final List<Finding> findings = new ErrorIdentifierValidator().validate(List.of(declaration));
        final List<String> findingMessages = findings.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(
                "E-ECMOJ-2: Illegal error code 'Q-TEST-1'. The codes must start with 'W-', 'E-' or 'F-'. (Test.java:1)"));
    }

    @Test
    void testInvalidFormat() {
        final ErrorMessageDeclaration declaration = ErrorMessageDeclaration.builder().identifier("123")
                .setPosition("Test.java", 1).build();
        final List<Finding> findings = new ErrorIdentifierValidator().validate(List.of(declaration));
        final List<String> findingMessages = findings.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains("E-ECMOJ-1: The error code '123' has an invalid format. (Test.java:1)"));
    }
}
