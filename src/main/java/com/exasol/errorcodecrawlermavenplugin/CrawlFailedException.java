package com.exasol.errorcodecrawlermavenplugin;

/**
 * Exception that is thrown if the error declaration crawling found an syntax error.
 */
class CrawlFailedException extends Exception {
    private final transient Finding finding;

    /**
     * Create a new instance of {@link CrawlFailedException}.
     * 
     * @param message message for the {@link Finding}.
     */
    public CrawlFailedException(final String message) {
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