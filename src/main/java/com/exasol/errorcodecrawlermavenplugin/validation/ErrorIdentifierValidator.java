package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorIdentifier;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that the error identifier has a valid format.
 */
//[impl->dsn~identifier-validator~1]
public class ErrorIdentifierValidator extends AbstractIndependentErrorMessageDeclarationValidator {
    @Override
    protected Stream<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration) {
        try {
            ErrorIdentifier.parse(errorMessageDeclaration.getIdentifier());
            return Stream.empty();
        } catch (final ErrorIdentifier.SyntaxException exception) {
            return Stream.of(new Finding(exception.getMessage() + " ("
                    + PositionFormatter.getFormattedPosition(errorMessageDeclaration) + ")"));
        }
    }
}
