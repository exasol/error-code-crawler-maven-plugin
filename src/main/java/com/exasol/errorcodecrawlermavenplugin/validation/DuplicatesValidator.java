package com.exasol.errorcodecrawlermavenplugin.validation;

import static java.util.stream.Collectors.joining;

import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorIdentifier;
import com.exsol.errorcodemodel.ErrorIdentifier.SyntaxException;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * This {@link ErrorMessageDeclarationValidator} validates that no error code is declared twice.
 */
// [impl->dsn~duplication-validator~1]
class DuplicatesValidator implements ErrorMessageDeclarationValidator {

    private final ErrorCodeConfig config;

    DuplicatesValidator(final ErrorCodeConfig config) {
        this.config = config;
    }

    @Override
    public List<Finding> validate(final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<ErrorMessageDeclaration>> declarationsPerCode = groupDeclarationsByErrorCode(
                errorMessageDeclarations);
        return generateFindingsForDuplicates(declarationsPerCode);
    }

    private Map<String, List<ErrorMessageDeclaration>> groupDeclarationsByErrorCode(
            final Collection<ErrorMessageDeclaration> errorMessageDeclarations) {
        final Map<String, List<ErrorMessageDeclaration>> declarationsPerCode = new HashMap<>();
        for (final ErrorMessageDeclaration errorMessageDeclaration : errorMessageDeclarations) {
            try {
                final var errorCode = ErrorIdentifier.parse(errorMessageDeclaration.getIdentifier());
                final String errorId = errorCode.getTag() + "-" + errorCode.getIndex();
                if (declarationsPerCode.containsKey(errorId)) {
                    declarationsPerCode.get(errorId).add(errorMessageDeclaration);
                } else {
                    declarationsPerCode.put(errorId, new LinkedList<>(List.of(errorMessageDeclaration)));
                }
            } catch (final ErrorIdentifier.SyntaxException exception) {
                // ignore. Will be reported by another validator
            }
        }
        return declarationsPerCode;
    }

    private List<Finding> generateFindingsForDuplicates(
            final Map<String, List<ErrorMessageDeclaration>> positionsPerCode) {
        final List<Finding> findings = new ArrayList<>();
        for (final Map.Entry<String, List<ErrorMessageDeclaration>> declaration : positionsPerCode.entrySet()) {
            if (declaration.getValue().size() > 1) {
                final String locations = declaration.getValue().stream() //
                        .map(PositionFormatter::getFormattedPosition) //
                        .collect(joining(", "));
                final Optional<ErrorIdentifier> identifier = parseIdentifier(declaration.getValue().get(0));
                if (identifier.isEmpty()) {
                    // Ignore. Will be reported by another validator
                    continue;
                }
                final String errorTag = identifier.get().getTag();
                final int nextAvailableIndex = this.config.getHighestIndexForErrorTag(errorTag) + 1;
                findings.add(new Finding(ExaError.messageBuilder("E-ECM-4").message(
                        "Found duplicate error code: {{errorCode}} was declared multiple times: {{declarations|uq}}.")
                        .parameter("errorCode", declaration.getKey()) //
                        .parameter("declarations", locations) //
                        .mitigation("Next available index for error tag {{error tag}} is {{next available index}}.")
                        .parameter("error tag", errorTag) //
                        .parameter("next available index", nextAvailableIndex) //
                        .toString()));
            }
        }
        return findings;
    }

    private Optional<ErrorIdentifier> parseIdentifier(final ErrorMessageDeclaration errorMessageDeclaration) {
        try {
            return Optional.of(ErrorIdentifier.parse(errorMessageDeclaration.getIdentifier()));
        } catch (final SyntaxException exception) {
            return Optional.empty();
        }
    }
}
