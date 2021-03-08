package com.exasol.errorcodecrawlermavenplugin.validation;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorcodecrawlermavenplugin.model.NamedParameter;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that all parameters used in message and mitigation are
 * declared.
 */
class ParametersValidator implements ErrorMessageDeclarationValidator {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^\\}]*)\\}\\}");

    @Override
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return errorMessageDeclarations.stream().flatMap(errorMessageDeclaration -> {
            final String declarationsText = errorMessageDeclaration.getMessage()
                    + String.join(" ", errorMessageDeclaration.getMitigations());
            return validateParametersAreDeclared(errorMessageDeclaration, declarationsText);
        }).collect(Collectors.toList());
    }

    private Stream<Finding> validateParametersAreDeclared(final ErrorMessageDeclaration errorMessageDeclaration,
            final String textWithPlaceholders) {
        final Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(textWithPlaceholders);
        final Stream.Builder<Finding> findings = Stream.builder();
        while (placeholderMatcher.find()) {
            validatePlaceholder(errorMessageDeclaration, placeholderMatcher.group(1)).ifPresent(findings::add);
        }
        return findings.build();
    }

    private Optional<Finding> validatePlaceholder(final ErrorMessageDeclaration errorMessageDeclaration,
            final String placeholder) {
        final List<NamedParameter> matchingParameters = errorMessageDeclaration.getNamedParameters().stream()
                .filter(parameter -> parameter.getName().equals(placeholder)).collect(Collectors.toList());
        if (matchingParameters.isEmpty()) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-17")
                    .message("The parameter '{{parameter name}}' was used but not declared.").message(" ({{position}})")
                    .mitigation(
                            "Declare the parameter using parameter(\"{{parameter name}}\", value) or unquotedParameter(\"{{parameter name}}\", value).")
                    .unquotedParameter("parameter name", placeholder)
                    .unquotedParameter("position", getFormattedPosition(errorMessageDeclaration)).toString()));
        } else if (matchingParameters.size() > 1) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-18")
                    .message("The parameter '{{parameter name}}' was declared multiple times.")
                    .message(" ({{position}})")
                    .mitigation(
                            "Remove one of the parameter(\"{{parameter name}}\", value) or unquotedParameter(\"{{parameter name}}\", value) calls.")
                    .unquotedParameter("parameter name", placeholder)
                    .unquotedParameter("position", getFormattedPosition(errorMessageDeclaration)).toString()));
        } else {
            return Optional.empty();
        }
    }

    private String getFormattedPosition(final ErrorMessageDeclaration errorMessageDeclaration) {
        return new File(errorMessageDeclaration.getSourceFile()).getName() + ":" + errorMessageDeclaration.getLine();
    }
}
