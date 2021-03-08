package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;

/**
 * Factory for {@link ErrorMessageDeclarationValidator}.
 */
public class ErrorMessageDeclarationValidatorFactory {

    /**
     * Get an {@link ErrorMessageDeclarationValidator}.
     * 
     * @return built validator
     * @param config configuration
     */
    public ErrorMessageDeclarationValidator getValidator(final ErrorCodeConfig config) {
        return new CompoundValidator(List.of(new DuplicatesValidator(), new ErrorCodesBelongToPackageValidator(config),
                new ParametersAreDeclaredValidator()));
    }
}
