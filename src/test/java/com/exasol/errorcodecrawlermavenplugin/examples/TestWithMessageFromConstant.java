package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Valid example that is crawled in the tests.
 */
public class TestWithMessageFromConstant {
    private static final String MESSAGE_FROM_CONSTANT = "message from constant";

    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message(MESSAGE_FROM_CONSTANT).toString());
    }
}
