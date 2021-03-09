package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that error codes are only declared in packages they belong to
 * (according to the config file).
 */
class ErrorCodesBelongToPackageValidator implements ErrorMessageDeclarationValidator {
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
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return errorMessageDeclarations.stream()//
                .map(this::verifyErrorCodeBelongsToPackage)//
                .filter(Optional::isPresent)//
                .map(Optional::get)//
                .collect(Collectors.toList());
    }

    private Optional<Finding> verifyErrorCodeBelongsToPackage(final ErrorMessageDeclaration errorMessageDeclaration) {
        final String tag = errorMessageDeclaration.getErrorCode().getTag();
        final String declaringPackage = errorMessageDeclaration.getDeclaringPackage();
        if (!this.config.hasErrorTag(tag)) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-12")
                    .message("The error tag {{tag}} was not declared in the " + ErrorCodeConfigReader.CONFIG_NAME + ".")
                    .parameter("tag", tag)
                    .mitigation(
                            "Check if it is just a typo and if not add an entry for {{tag}} and package {{package}}.")
                    .parameter("package", declaringPackage).toString()));
        }
        final Optional<String> requiredTag = this.config.getErrorTagForPackage(declaringPackage);
        if (requiredTag.isEmpty() || !tag.equalsIgnoreCase(requiredTag.get())) {
            final String tagSuggestion = getTagSuggestion(errorMessageDeclaration);
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-13")
                    .message("According to this project's " + ErrorCodeConfigReader.CONFIG_NAME
                            + ", the error tag {{tag}} is not allowed for the package {{package}}.")
                    .mitigation(
                            "The config allows the tag {{tag}} for the following packages: {{tags packages}}. {{optional tag suggestion}}")
                    .parameter("tag", tag)//
                    .parameter("package", declaringPackage)//
                    .parameter("tags packages", this.config.getPackagesForErrorTag(tag))//
                    .unquotedParameter("optional tag suggestion", tagSuggestion)//
                    .toString()));
        }
        return Optional.empty();
    }

    private String getTagSuggestion(final ErrorMessageDeclaration errorMessageDeclaration) {
        return this.config.getErrorTagForPackage(errorMessageDeclaration.getDeclaringPackage())
                .map(suggestedTag -> " For this package it allows the tag '" + suggestedTag + "'.").orElse("");
    }
}
