package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Invalid example that is crawled in the tests.
 */
public class IllegalUnnamedParameter {
    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("Test message {{}}", 1).toString());
    }
}
