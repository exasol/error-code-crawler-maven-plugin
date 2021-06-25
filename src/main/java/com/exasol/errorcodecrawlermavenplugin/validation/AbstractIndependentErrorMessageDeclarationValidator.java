package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * Abstract basis for {@link ErrorMessageDeclarationValidator}s that validate each {@link ErrorMessageDeclaration}
 * independent of the others.
 */
abstract class AbstractIndependentErrorMessageDeclarationValidator implements ErrorMessageDeclarationValidator {
    @Override
    public final List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return errorMessageDeclarations.stream()//
                .flatMap(this::validateSingleErrorMessageDeclaration)//
                .collect(Collectors.toList());
    }

    /**
     * Validate a single {@link ErrorMessageDeclaration}.
     * 
     * @param errorMessageDeclaration {@link ErrorMessageDeclaration} to validate
     * @return validation findings
     */
    protected abstract Stream<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration);
}
