package com.exasol.errorcodecrawlermavenplugin;

/**
 * This class represents issues with the error codes.
 */
public class Finding {
    private final String message;

    /**
     * Create a new instance of {@link Finding}.
     *
     * @param message message of the finding.
     */
    public Finding(final String message) {
        this.message = message;
    }

    /**
     * Get the message of this finding.
     * 
     * @return message
     */
    public String getMessage() {
        return this.message;
    }
}
