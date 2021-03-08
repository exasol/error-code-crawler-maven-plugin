package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Valid example that is crawled in the tests.
 */
public class TestWithNamedParameter {
    public void run() {
        throw new IllegalStateException(
                ExaError.messageBuilder("E-TEST-1").message("my message {{test}}").parameter("test", 1).toString());
    }
}
