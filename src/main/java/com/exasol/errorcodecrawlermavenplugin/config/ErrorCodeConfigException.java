package com.exasol.errorcodecrawlermavenplugin.config;

/**
 * Exception that is thrown on an error code config.
 */
public class ErrorCodeConfigException extends Exception {
    /**
     * Create a new instance of {@link ErrorCodeConfigException}.
     * 
     * @param message exception message
     */
    public ErrorCodeConfigException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@link ErrorCodeConfigException}.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ErrorCodeConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
