package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.Optional;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that error codes are only declared in packages they belong to
 * (according to the config file).
 */
//[impl->dsn~error-identifier-belongs-to-package-validator~1]
class ErrorCodesBelongToPackageValidator extends AbstractIndependentErrorMessageDeclarationValidator {
    private final ErrorCodeConfig config;

    /**
     * Create a new instance of {@link ErrorCodesBelongToPackageValidator}.
     * 
     * @param config configuration
     */
    public ErrorCodesBelongToPackageValidator(final ErrorCodeConfig config) {
        this.config = config;
    }

    @Override
    protected Stream<Finding> validateSingleErrorMessageDeclaration(
            final ErrorMessageDeclaration errorMessageDeclaration) {
        final String tag = errorMessageDeclaration.getErrorCode().getTag();
        final String declaringPackage = errorMessageDeclaration.getDeclaringPackage();
        if (!this.config.hasErrorTag(tag)) {
            return getUndeclaredTagFinding(tag, declaringPackage);
        }
        final Optional<String> requiredTag = this.config.getErrorTagForPackage(declaringPackage);
        if (requiredTag.isEmpty() || !tag.equalsIgnoreCase(requiredTag.get())) {
            final String tagSuggestion = getTagSuggestion(errorMessageDeclaration);
            return getWrongTagFinding(tag, declaringPackage, tagSuggestion);
        }
        return Stream.empty();
    }

    private Stream<Finding> getWrongTagFinding(final String tag, final String declaringPackage,
            final String tagSuggestion) {
        return Stream.of(new Finding(ExaError.messageBuilder("E-ECM-13")
                .message("According to this project's " + ErrorCodeConfigReader.CONFIG_NAME
                        + ", the error tag {{tag}} is not allowed for the package {{package}}.")
                .mitigation(
                        "The config allows the tag {{tag}} for the following packages: {{tags packages}}. {{optional tag suggestion|uq}}")
                .parameter("tag", tag)//
                .parameter("package", declaringPackage)//
                .parameter("tags packages", this.config.getPackagesForErrorTag(tag))//
                .parameter("optional tag suggestion", tagSuggestion)//
                .toString()));
    }

    private Stream<Finding> getUndeclaredTagFinding(final String tag, final String declaringPackage) {
        return Stream.of(new Finding(ExaError.messageBuilder("E-ECM-12")
                .message("The error tag {{tag}} was not declared in the " + ErrorCodeConfigReader.CONFIG_NAME + ".")
                .parameter("tag", tag)
                .mitigation("Check if it is just a typo and if not add an entry for {{tag}} and package {{package}}.")
                .parameter("package", declaringPackage).toString()));
    }

    private String getTagSuggestion(final ErrorMessageDeclaration errorMessageDeclaration) {
        return this.config.getErrorTagForPackage(errorMessageDeclaration.getDeclaringPackage())
                .map(suggestedTag -> " For this package it allows the tag '" + suggestedTag + "'.").orElse("");
    }
}
