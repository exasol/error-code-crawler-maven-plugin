package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Valid example that is crawled in the tests.
 */
public class TestWithMitigations {

    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("my message")
                .mitigation("That's how to fix it.").mitigation("One more hint.").toString());
    }
}
