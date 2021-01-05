package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ExaError;

/**
 * Invalid example that is crawled in the tests.
 */
public class DuplicateErrorCode {
    public void run1() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("Test message").toString());
    }

    public void run2() {
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("other message").toString());
    }
}
