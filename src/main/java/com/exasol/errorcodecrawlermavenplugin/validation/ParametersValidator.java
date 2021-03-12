package com.exasol.errorcodecrawlermavenplugin.validation;

import static com.exasol.errorcodecrawlermavenplugin.validation.PositionFormatter.getFormattedPosition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorcodecrawlermavenplugin.model.NamedParameter;
import com.exasol.errorreporting.*;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that all parameters used in message and mitigation are
 * declared.
 */
class ParametersValidator extends AbstractIndependentErrorMessageDeclarationValidator {

    @Override
    protected Stream<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration) {
        final String textWithPlaceholders = errorMessageDeclaration.getMessage()
                + String.join(" ", errorMessageDeclaration.getMitigations());
        final Stream.Builder<Finding> findings = Stream.builder();
        final Iterable<Placeholder> placeholders = PlaceholderMatcher.findPlaceholders(textWithPlaceholders);
        placeholders.forEach(placeholder -> validatePlaceholder(errorMessageDeclaration, placeholder.getName())
                .ifPresent(findings::add));
        return findings.build();
    }

    private Optional<Finding> validatePlaceholder(final ErrorMessageDeclaration errorMessageDeclaration,
            final String placeholder) {
        final List<NamedParameter> matchingParameters = errorMessageDeclaration.getNamedParameters().stream()
                .filter(parameter -> parameter.getName().equals(placeholder)).collect(Collectors.toList());
        if (matchingParameters.isEmpty()) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-17")
                    .message("The parameter {{parameter name}} was used but not declared.")
                    .message(" ({{position|uq}})")
                    .mitigation(
                            "Declare the parameter using parameter(\"{{parameter name|uq}}\", value) or unquotedParameter(\"{{parameter name|uq}}\", value).")
                    .parameter("parameter name", placeholder)
                    .parameter("position", getFormattedPosition(errorMessageDeclaration)).toString()));
        } else if (matchingParameters.size() > 1) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-18")
                    .message("The parameter {{parameter name}} was declared multiple times.")
                    .message(" ({{position|uq}})")
                    .mitigation(
                            "Remove one of the parameter(\"{{parameter name|uq}}\", value) or unquotedParameter(\"{{parameter name|uq}}\", value) calls.")
                    .parameter("parameter name", placeholder)
                    .parameter("position", getFormattedPosition(errorMessageDeclaration)).toString()));
        } else {
            return Optional.empty();
        }
    }
}
