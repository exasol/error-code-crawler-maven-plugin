package com.exasol.errorcodecrawlermavenplugin.examples;

import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exasol.errorreporting.ExaError;

/**
 * Invalid example. Assigning the builder to variables is not allowed.
 */
public class TestWithBuilderAssignedToVariable {
    public void run() {
        final ErrorMessageBuilder builder = ExaError.messageBuilder("E-TEST-1");
        throw new IllegalStateException(builder.message("concatenated " + "message").toString());
    }
}
