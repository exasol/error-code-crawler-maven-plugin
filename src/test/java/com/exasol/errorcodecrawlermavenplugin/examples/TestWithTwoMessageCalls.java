package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Valid example that is crawled in the tests.
 */
public class TestWithTwoMessageCalls {
    public void run() {
        throw new IllegalStateException(
                ExaError.messageBuilder("E-TEST-1").message("compound ").message("message").toString());
    }
}
