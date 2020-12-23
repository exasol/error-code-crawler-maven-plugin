package com.exasol.errorcodecrawlermavenplugin;

import java.io.File;
import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.model.ExasolError;
import com.exasol.errorreporting.ExaError;

/**
 * Validator for {@link ExasolError}s.
 */
public class ErrorValidator {

    /**
     * Validate the crawled error codes.
     * 
     * @param exasolErrors error codes to validate
     * @return list of findings
     */
    public List<Finding> validate(final Collection<ExasolError> exasolErrors) {
        final List<Finding> findings = new LinkedList<>();
        findings.addAll(findDuplicates(exasolErrors));
        return findings;
    }

    private List<Finding> findDuplicates(final Collection<ExasolError> exasolErrors) {
        final Map<String, List<String>> positionsPerCode = new HashMap<>();
        for (final ExasolError exasolError : exasolErrors) {
            final String errorCode = exasolError.getErrorCode();
            if (positionsPerCode.containsKey(errorCode)) {
                positionsPerCode.get(errorCode).add(getSourceReference(exasolError));
            } else {
                positionsPerCode.put(errorCode, new LinkedList<>(List.of(getSourceReference(exasolError))));
            }
        }
        final List<Finding> findings = new ArrayList<>();
        for (final Map.Entry<String, List<String>> errorCode : positionsPerCode.entrySet()) {
            if (errorCode.getValue().size() > 1) {
                findings.add(new Finding(ExaError.messageBuilder("E-ECM-4").message(
                        "Found duplicate error code: {{exasolError}} was declared multiple times: {{declarations}}.")
                        .parameter("exasolError", errorCode.getKey())
                        .unquotedParameter("declarations", String.join(", ", errorCode.getValue())).toString()));
            }
        }
        return findings;
    }

    private String getSourceReference(final ExasolError exasolError) {
        return new File(exasolError.getSourceFile()).getName() + ":" + exasolError.getLine();
    }
}
