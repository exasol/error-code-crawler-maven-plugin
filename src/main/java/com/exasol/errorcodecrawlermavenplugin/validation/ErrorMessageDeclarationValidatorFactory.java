package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;

/**
 * Factory for {@link ErrorMessageDeclarationValidator}.
 */
//[impl->dsn~validator~1]
public class ErrorMessageDeclarationValidatorFactory {

    /**
     * Creates a new instance of {@code ErrorMessageDeclarationValidatorFactory}.
     * <p>
     * This factory provides a configured {@link ErrorMessageDeclarationValidator}
     * composed of individual validation strategies.
     * </p>
     */
    public ErrorMessageDeclarationValidatorFactory() {
        // Default constructor required for instantiation
    }

    /**
     * Get an {@link ErrorMessageDeclarationValidator}.
     * 
     * @return built validator
     * @param config configuration
     */
    public ErrorMessageDeclarationValidator getValidator(final ErrorCodeConfig config) {
        return new CompoundValidator(List.of(new ErrorIdentifierValidator(), new DuplicatesValidator(config),
                new ErrorCodesBelongToPackageValidator(config), new ParametersValidator(),
                new EmptyParameterNameValidator(), new HighestIndexValidator(config)));
    }
}
