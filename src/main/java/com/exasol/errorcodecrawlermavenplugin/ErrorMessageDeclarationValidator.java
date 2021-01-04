package com.exasol.errorcodecrawlermavenplugin;

import java.io.File;
import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * Validator for {@link ErrorMessageDeclaration}s.
 */
public class ErrorMessageDeclarationValidator {

    /**
     * Validate the crawled error codes.
     * 
     * @param errorMessageDeclarations error codes to validate
     * @return list of findings
     */
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final List<Finding> findings = new LinkedList<>();
        findings.addAll(findDuplicates(errorMessageDeclarations));
        return findings;
    }

    private List<Finding> findDuplicates(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<String>> positionsPerCode = groupDeclarationsByErrorCode(errorMessageDeclarations);
        return generateFindingsForDublicates(positionsPerCode);
    }

    private Map<String, List<String>> groupDeclarationsByErrorCode(
            final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<String>> positionsPerCode = new HashMap<>();
        for (final ErrorMessageDeclaration errorMessageDeclaration : errorMessageDeclarations) {
            final String errorCode = errorMessageDeclaration.getErrorCode();
            if (positionsPerCode.containsKey(errorCode)) {
                positionsPerCode.get(errorCode).add(getSourceReference(errorMessageDeclaration));
            } else {
                positionsPerCode.put(errorCode, new LinkedList<>(List.of(getSourceReference(errorMessageDeclaration))));
            }
        }
        return positionsPerCode;
    }

    private List<Finding> generateFindingsForDublicates(final Map<String, List<String>> positionsPerCode) {
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
}
