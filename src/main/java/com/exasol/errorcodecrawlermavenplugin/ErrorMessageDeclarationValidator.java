package com.exasol.errorcodecrawlermavenplugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * Validator for {@link ErrorMessageDeclaration}s.
 */
public class ErrorMessageDeclarationValidator {
    private final ErrorCodeConfig config;

    public ErrorMessageDeclarationValidator(final ErrorCodeConfig config) {
        this.config = config;
    }

    /**
     * Validate the crawled error codes.
     * 
     * @param errorMessageDeclarations error codes to validate
     * @return list of findings
     */
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final List<Finding> findings = new LinkedList<>();
        findings.addAll(findDuplicates(errorMessageDeclarations));
        findings.addAll(verifyErrorCodesBelongToPackage(errorMessageDeclarations));
        return findings;
    }

    private List<Finding> findDuplicates(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<String>> positionsPerCode = groupDeclarationsByErrorCode(errorMessageDeclarations);
        return generateFindingsForDuplicates(positionsPerCode);
    }

    private Map<String, List<String>> groupDeclarationsByErrorCode(
            final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<String>> positionsPerCode = new HashMap<>();
        for (final ErrorMessageDeclaration errorMessageDeclaration : errorMessageDeclarations) {
            final ErrorCode errorCode = errorMessageDeclaration.getErrorCode();
            final String errorId = errorCode.getTag() + "-" + errorCode.getIndex();
            if (positionsPerCode.containsKey(errorId)) {
                positionsPerCode.get(errorId).add(getSourceReference(errorMessageDeclaration));
            } else {
                positionsPerCode.put(errorId, new LinkedList<>(List.of(getSourceReference(errorMessageDeclaration))));
            }
        }
        return positionsPerCode;
    }

    private List<Finding> generateFindingsForDuplicates(final Map<String, List<String>> positionsPerCode) {
        final List<Finding> findings = new ArrayList<>();
        for (final Map.Entry<String, List<String>> errorCode : positionsPerCode.entrySet()) {
            if (errorCode.getValue().size() > 1) {
                findings.add(new Finding(ExaError.messageBuilder("E-ECM-4").message(
                        "Found duplicate error code: {{errorCode}} was declared multiple times: {{declarations}}.")
                        .parameter("errorCode", errorCode.getKey())
                        .unquotedParameter("declarations", String.join(", ", errorCode.getValue())).toString()));
            }
        }
        return findings;
    }

    private String getSourceReference(final ErrorMessageDeclaration errorMessageDeclaration) {
        return new File(errorMessageDeclaration.getSourceFile()).getName() + ":" + errorMessageDeclaration.getLine();
    }

    private List<Finding> verifyErrorCodesBelongToPackage(
            final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        return errorMessageDeclarations.stream()//
                .map(this::verifyErrorCodeBelongsToPackage)//
                .filter(Optional::isPresent)//
                .map(Optional::get)//
                .collect(Collectors.toList());
    }

    private Optional<Finding> verifyErrorCodeBelongsToPackage(final ErrorMessageDeclaration errorMessageDeclaration) {
        final String tag = errorMessageDeclaration.getErrorCode().getTag();
        if (!this.config.hasErrorTag(tag)) {
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-12")
                    .message("The error tag {{tag}} was not declared in the " + ErrorCodeConfigReader.CONFIG_NAME + ".")
                    .parameter("tag", tag)
                    .mitigation(
                            "Check if it is just a typo and if not add an entry for {{tag}} and package {{package}}.")
                    .parameter("package", errorMessageDeclaration.getDeclaringPackage()).toString()));
        }
        final List<String> tagsPackages = this.config.getPackagesForErrorTag(tag);
        if (!tagsPackages.contains(errorMessageDeclaration.getDeclaringPackage())) {
            final String tagSuggestion = getTagSuggestion(errorMessageDeclaration);
            return Optional.of(new Finding(ExaError.messageBuilder("E-ECM-13")
                    .message("According to this project's " + ErrorCodeConfigReader.CONFIG_NAME
                            + ", the error tag {{tag}} is not allowed for the package {{package}}.")
                    .mitigation(
                            "The config allows the tag {{tag}} for the following packages: {{tags packages}}. {{optional tag suggestion}}")
                    .parameter("tag", tag)//
                    .parameter("package", errorMessageDeclaration.getDeclaringPackage())//
                    .parameter("tags packages", tagsPackages)//
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
