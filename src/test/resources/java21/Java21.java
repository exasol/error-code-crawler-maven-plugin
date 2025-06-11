package com.exasol.errorcodecrawlermavenplugin.examples;

import java.io.IOException;
import java.io.StringWriter;

public class Java21 {

    public sealed interface Java21SyntaxInterface permits Java21SyntaxRecord {
    }

    public record Java21SyntaxRecord(int targetLength) implements Java21SyntaxInterface {
    }

    void methodWithJava21Features() {
        Java21SyntaxRecord java21Syntax = new Java21SyntaxRecord(10);
        convertable(java21Syntax, 10);
    }

    private boolean convertable(final Java21SyntaxRecord target, int length) {
        return target instanceof Java21SyntaxRecord(int targetLength) && targetLength >= length;
    }
}
