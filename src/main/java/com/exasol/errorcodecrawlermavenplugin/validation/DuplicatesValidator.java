package com.exasol.errorcodecrawlermavenplugin.validation;

import java.io.File;
import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorIdentifier;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that no error code is declared twice.
 */
// [impl->dsn~duplication-validator~1]
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
            try {
                final var errorCode = ErrorIdentifier.parse(errorMessageDeclaration.getIdentifier());
                final String errorId = errorCode.getTag() + "-" + errorCode.getIndex();
                if (positionsPerCode.containsKey(errorId)) {
                    positionsPerCode.get(errorId).add(getSourceReference(errorMessageDeclaration));
                } else {
                    positionsPerCode.put(errorId,
                            new LinkedList<>(List.of(getSourceReference(errorMessageDeclaration))));
                }
            } catch (final ErrorIdentifier.SyntaxException exception) {
                // ignore. Will be reported by another validator
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
