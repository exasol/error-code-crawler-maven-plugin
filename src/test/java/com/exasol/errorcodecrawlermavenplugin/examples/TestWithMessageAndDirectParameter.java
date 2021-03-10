package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Valid example that is crawled in the tests.
 */
public class TestWithMessageAndDirectParameter {

    public void run() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1")
                .message("message with parameters {{param1}} {{param2|uq}} {{param3}}", 1, 2).toString());
    }
}
