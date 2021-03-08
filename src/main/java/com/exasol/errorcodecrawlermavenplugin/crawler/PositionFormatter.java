package com.exasol.errorcodecrawlermavenplugin.crawler;

import spoon.reflect.cu.SourcePosition;

/**
 * String formatter for source code positions.
 */
class PositionFormatter {
    private PositionFormatter() {
        // empty to hide public default
    }

    /**
     * Format a source code position for the use in exception messages.
     * 
     * @param position source code position
     * @return formatted string
     */
    public static String formatPosition(final SourcePosition position) {
        return position.getFile().getName() + ":" + position.getLine();
    }
}
