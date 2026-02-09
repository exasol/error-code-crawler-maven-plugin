package com.exasol.errorcodecrawlermavenplugin.examples;

import java.io.IOException;
import java.io.StringWriter;

public class Java21SealedSwitch {

    private static String description(final Operator op) {
        return switch (op) {
            case BinaryOp.AND -> "conjunction operator";
            case BinaryOp.OR -> "disjunction operator";
            case UnaryOp.NOT -> "negation operator";
        };
    }
}

sealed interface Operator {}

enum BinaryOp implements Operator {
    AND, OR
}

enum UnaryOp implements Operator {
    NOT
}

