package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

/**
 * Abstract basis for {@link ErrorMessageDeclarationValidator}s that validate each {@link ErrorMessageDeclaration}
 * independent of the others.
 */
public abstract class AbstractIndependentErrorMessageDeclarationValidator implements ErrorMessageDeclarationValidator {
    @Override
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return errorMessageDeclarations.stream()//
                .map(this::validateSingleErrorMessageDeclaration)//
                .filter(Optional::isPresent)//
                .map(Optional::get)//
                .collect(Collectors.toList());
    }

    /**
     * Validate a single {@link ErrorMessageDeclaration}.
     * 
     * @param errorMessageDeclaration {@link ErrorMessageDeclaration} to validate
     * @return optional validation finding
     */
    protected abstract Optional<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration);
}
