package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

// [utest->dsn~parameters-validator~1]
class ParametersValidatorTest {
    @Test
    void testValid() {
        final ErrorMessageDeclaration validErrorDeclaration = ErrorMessageDeclaration.builder()
                .prependMessage("test {{my param}}").addParameter("my param", null, true).build();
        final List<Finding> result = new ParametersValidator().validate(List.of(validErrorDeclaration));
        assertThat(result, empty());
    }

    @Test
    void testNotDeclared() {
        final ErrorMessageDeclaration validErrorDeclaration = ErrorMessageDeclaration.builder()
                .setPosition("myFile.java", 1).prependMessage("test {{my param}}").build();
        final List<Finding> result = new ParametersValidator().validate(List.of(validErrorDeclaration));
        final List<String> findingMessages = result.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(
                "E-ECM-17: The parameter 'my param' was used but not declared. (myFile.java:1) Declare the parameter using parameter(\"my param\", value) or unquotedParameter(\"my param\", value)."));
    }

    @Test
    void testDeclaredTwice() {
        final ErrorMessageDeclaration validErrorDeclaration = ErrorMessageDeclaration.builder()
                .prependMessage("test {{my param}}").addParameter("my param", null, true)
                .addParameter("my param", null, true).setPosition("myFile.java", 1).build();
        final List<Finding> result = new ParametersValidator().validate(List.of(validErrorDeclaration));
        final List<String> findingMessages = result.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(
                "E-ECM-18: The parameter 'my param' was declared multiple times. (myFile.java:1) Remove one of the parameter(\"my param\", value) or unquotedParameter(\"my param\", value) calls."));
    }

    @Test
    void testUndeclaredInMitigation() {
        final ErrorMessageDeclaration validErrorDeclaration = ErrorMessageDeclaration.builder().prependMessage("test")
                .prependMitigation("{{my param}}").setPosition("myFile.java", 1).build();
        final List<Finding> result = new ParametersValidator().validate(List.of(validErrorDeclaration));
        final List<String> findingMessages = result.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(startsWith("E-ECM-17")));
    }
}
