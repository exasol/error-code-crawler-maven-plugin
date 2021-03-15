package com.exasol.errorcodecrawlermavenplugin.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

class EmptyParameterNameValidatorTest {
    @Test
    void testValid() {
        final ErrorMessageDeclaration messageDeclaration = ErrorMessageDeclaration.builder()
                .addParameter("test", "", false).setPosition("myFile.java", 1).build();
        final List<Finding> findings = new EmptyParameterNameValidator().validate(List.of(messageDeclaration));
        assertThat(findings, empty());
    }

    @Test
    void testInvalid() {
        final ErrorMessageDeclaration messageDeclaration = ErrorMessageDeclaration.builder().addParameter("", "", false)
                .setPosition("myFile.java", 1).build();
        final List<Finding> findings = new EmptyParameterNameValidator().validate(List.of(messageDeclaration));
        final List<String> findingMessages = findings.stream().map(Finding::getMessage).collect(Collectors.toList());
        assertThat(findingMessages, contains(
                "E-ECM-19: Found an error message declaration with unnamed parameters. This is not allowed since it makes the error-catalog unreadable. (myFile.java:1) Replace the empty placeholder by a placeholder with a name."));
    }
}
