package com.exasol.errorcodecrawlermavenplugin.validation;

import java.io.File;
import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that no error code is declared twice.
 */
class DuplicatesValidator implements ErrorMessageDeclarationValidator {
    @Override
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
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
                        "Found duplicate error code: {{errorCode}} was declared multiple times: {{declarations|uq}}.")
                        .parameter("errorCode", errorCode.getKey())
                        .parameter("declarations", String.join(", ", errorCode.getValue())).toString()));
            }
        }
        return findings;
    }

    private String getSourceReference(final ErrorMessageDeclaration errorMessageDeclaration) {
        return new File(errorMessageDeclaration.getSourceFile()).getName() + ":" + errorMessageDeclaration.getLine();
    }
}
