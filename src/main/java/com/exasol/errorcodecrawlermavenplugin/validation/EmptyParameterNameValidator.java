package com.exasol.errorcodecrawlermavenplugin.validation;

import static com.exasol.errorcodecrawlermavenplugin.validation.PositionFormatter.getFormattedPosition;

import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * test-db-builder-java 3.2.0 This {@link ErrorMessageDeclarationValidator} validates, that there are no parameters with
 * no name.
 */
//[impl->dsn~empty-parameter-name-validator~1]
class EmptyParameterNameValidator extends AbstractIndependentErrorMessageDeclarationValidator {
    @Override
    protected Stream<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration) {
        if (hasParameterWithEmptyName(errorMessageDeclaration)) {
            return Stream.of(new Finding(ExaError.messageBuilder("E-ECM-19").message(
                    "Found an error message declaration with unnamed parameters. This is not allowed since it makes the error-catalog unreadable. ({{position|uq}})",
                    getFormattedPosition(errorMessageDeclaration))
                    .mitigation("Replace the empty placeholder by a placeholder with a name.").toString()));
        } else {
            return Stream.empty();
        }
    }

    private boolean hasParameterWithEmptyName(final ErrorMessageDeclaration errorMessageDeclaration) {
        return errorMessageDeclaration.getNamedParameters().stream()
                .anyMatch(parameter -> parameter.getName().isBlank());
    }
}
