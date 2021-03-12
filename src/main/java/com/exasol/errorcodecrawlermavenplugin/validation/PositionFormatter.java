package com.exasol.errorcodecrawlermavenplugin.validation;

import java.io.File;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

/**
 * Format a error code positions for error messages.
 */
class PositionFormatter {
    /**
     * Format error code position.
     * 
     * @param errorMessageDeclaration error message declaration
     * @return formatted string
     */
    static String getFormattedPosition(final ErrorMessageDeclaration errorMessageDeclaration) {
        return new File(errorMessageDeclaration.getSourceFile()).getName() + ":" + errorMessageDeclaration.getLine();
    }
}
