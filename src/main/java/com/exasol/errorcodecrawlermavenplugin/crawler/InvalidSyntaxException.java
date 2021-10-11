package com.exasol.errorcodecrawlermavenplugin.crawler;

import com.exasol.errorcodecrawlermavenplugin.Finding;

/**
 * Exception that is thrown if the error declaration crawling found an syntax error.
 */
class InvalidSyntaxException extends Exception {
    private static final long serialVersionUID = -5224230640960774874L;
    private final transient Finding finding;

    /**
     * Create a new instance of {@link InvalidSyntaxException}.
     * 
     * @param message message for the {@link Finding}.
     */
    public InvalidSyntaxException(final String message) {
        this.finding = new Finding(message);
    }

    /**
     * Get a {@link Finding} with the error message.
     * 
     * @return {@link Finding} with the error message
     */
    public Finding getFinding() {
        return this.finding;
    }
}
