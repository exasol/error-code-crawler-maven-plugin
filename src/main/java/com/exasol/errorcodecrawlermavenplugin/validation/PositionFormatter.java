package com.exasol.errorcodecrawlermavenplugin.validation;

import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * Format an error code positions for error messages.
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
        final String filePath = formatFilePath(errorMessageDeclaration);
        return filePath + ":" + errorMessageDeclaration.getLine();
    }

    private static String formatFilePath(final ErrorMessageDeclaration errorMessageDeclaration) {
        if (errorMessageDeclaration.getSourceFile() == null || errorMessageDeclaration.getSourceFile().isBlank()) {
            return "UNKNOWN-FILE";
        } else {
            return errorMessageDeclaration.getSourceFile();
        }
    }
}
