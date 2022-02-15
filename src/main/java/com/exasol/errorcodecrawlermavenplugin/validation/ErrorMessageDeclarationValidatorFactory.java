package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;

/**
 * Factory for {@link ErrorMessageDeclarationValidator}.
 */
//[impl->dsn~validator~1]
public class ErrorMessageDeclarationValidatorFactory {

    /**
     * Get an {@link ErrorMessageDeclarationValidator}.
     * 
     * @return built validator
     * @param config configuration
     */
    public ErrorMessageDeclarationValidator getValidator(final ErrorCodeConfig config) {
        return new CompoundValidator(List.of(new ErrorIdentifierValidator(), new DuplicatesValidator(),
                new ErrorCodesBelongToPackageValidator(config), new ParametersValidator(),
                new EmptyParameterNameValidator(), new HighestIndexValidator(config)));
    }
}
