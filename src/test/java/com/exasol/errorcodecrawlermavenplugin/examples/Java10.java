package com.exasol.errorcodecrawlermavenplugin.examples;

import java.io.IOException;
import java.io.StringWriter;

import com.exasol.errorreporting.ExaError;

public class Java10 {

    void methodWithJava10Features() throws IOException {
        final var myNumber = 10;
        try (final StringWriter stringWriter = new StringWriter()) {
            stringWriter.append(String.valueOf(myNumber));
        }
        throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("Test message").toString());
    }
}
