package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorIdentifier;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that the error identifier has a valid format.
 */
//[impl->dsn~identifier-validator~2]
public class ErrorIdentifierValidator extends AbstractIndependentErrorMessageDeclarationValidator {

    /**
     * Creates a new instance of {@code ErrorIdentifierValidator}.
     * <p>
     * This validator checks whether the error identifier in a given {@link ErrorMessageDeclaration}
     * conforms to the expected syntax. It is typically used as part of the error code validation pipeline.
     * </p>
     */
    public ErrorIdentifierValidator() {
        // Default constructor required for instantiation via reflection or service loading
    }

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
