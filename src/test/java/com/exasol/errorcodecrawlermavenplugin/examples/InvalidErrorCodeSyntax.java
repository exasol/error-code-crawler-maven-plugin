package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Invalid example that is crawled in the tests.
 */
public class InvalidErrorCodeSyntax {
    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-X").message("Test message").toString());
    }
}
