package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Invalid example that is crawled in the tests.
 */
public class InvalidErrorCodeSyntax {
    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("Q-TEST-1").message("Test message").toString());
    }
}
