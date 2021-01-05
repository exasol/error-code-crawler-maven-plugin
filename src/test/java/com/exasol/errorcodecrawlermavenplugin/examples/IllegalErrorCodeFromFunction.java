package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Invalid example that is crawled in the tests.
 */
public class IllegalErrorCodeFromFunction {
    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder(getErrorCode()).message("Test message").toString());
    }

    private String getErrorCode() {
        return "E-TEST-3";
    }
}
