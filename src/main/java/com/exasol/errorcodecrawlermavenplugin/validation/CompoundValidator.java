package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * This {@link ErrorMessageDeclarationValidator} combine multiple {@link ErrorMessageDeclarationValidator}s into one.
 */
class CompoundValidator implements ErrorMessageDeclarationValidator {
    private final List<ErrorMessageDeclarationValidator> validators;

    /**
     * Create a new instance of {@link CompoundValidator}.
     * 
     * @param validators validators to combine
     */
    CompoundValidator(final List<ErrorMessageDeclarationValidator> validators) {
        this.validators = validators;
    }

    @Override
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return this.validators.stream().flatMap(validator -> validator.validate(errorMessageDeclarations).stream())
                .collect(Collectors.toList());
    }
}
