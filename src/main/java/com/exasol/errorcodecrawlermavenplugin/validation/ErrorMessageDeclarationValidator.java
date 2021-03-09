package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.Collection;
import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

/**
 * Interface for classes that validate {@link ErrorMessageDeclaration}s.
 */
public interface ErrorMessageDeclarationValidator {
    /**
     * Validate the passed error message declarations.
     *
     * @param errorMessageDeclarations error codes to validate
     * @return list of findings
     */
    List<Finding> validate(Collection<ErrorMessageDeclaration> errorMessageDeclarations);
}
