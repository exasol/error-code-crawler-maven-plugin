package com.exasol.errorcodecrawlermavenplugin.validation;

import static com.exasol.errorcodecrawlermavenplugin.validation.PositionFormatter.getFormattedPosition;

import java.util.Optional;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ErrorMessageDeclarationValidator} validates, that there are no parameters with no name.
 */
class EmptyParameterNameValidator extends AbstractIndependentErrorMessageDeclarationValidator {

    @Override
    protected Optional<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration) {
        if (hasParameterWithEmptyName(errorMessageDeclaration)) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-19").message(
                    "Found an error message declaration with unnamed parameters. This is not allowed since it makes the error-catalog unreadable. ({{position|uq}})",
                    getFormattedPosition(errorMessageDeclaration))
                    .mitigation("Replace the empty placeholder by a placeholder with a name.").toString()));
        } else {
            return Optional.empty();
        }
    }

    private boolean hasParameterWithEmptyName(final ErrorMessageDeclaration errorMessageDeclaration) {
        return errorMessageDeclaration.getNamedParameters().stream()
                .anyMatch(parameter -> parameter.getName().isBlank());
    }
}
