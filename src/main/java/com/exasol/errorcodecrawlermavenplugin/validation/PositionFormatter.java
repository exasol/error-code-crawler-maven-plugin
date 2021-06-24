package com.exasol.errorcodecrawlermavenplugin.validation;

import java.io.File;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

/**
 * Format a error code positions for error messages.
 */
class PositionFormatter {

    private PositionFormatter() {
        // empty on purpose
    }

    /**
     * Format error code position.
     * 
     * @param errorMessageDeclaration error message declaration
     * @return formatted string
     */
    static String getFormattedPosition(final ErrorMessageDeclaration errorMessageDeclaration) {
        final String fileName = formatFileName(errorMessageDeclaration);
        return fileName + ":" + errorMessageDeclaration.getLine();
    }

    private static String formatFileName(final ErrorMessageDeclaration errorMessageDeclaration) {
        if (errorMessageDeclaration.getSourceFile() == null || errorMessageDeclaration.getSourceFile().isBlank()) {
            return "UNKNOWN-FILE";
        } else {
            return new File(errorMessageDeclaration.getSourceFile()).getName();
        }
    }
}
