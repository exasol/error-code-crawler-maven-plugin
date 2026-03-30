package com.exasol.errorcodecrawlermavenplugin.examples;

public class Java25 {

    String describe(final long input) {
        if (input instanceof final int narrowed) {
            return "fits into int: " + narrowed;
        }
        return "does not fit into int: " + input;
    }

    String classify(final double value) {
        return switch (value) {
            case final double d when Double.isNaN(d) -> "not a number";
            case final double d when d == 0.0d -> "zero";   // catches +0.0 and -0.0
            case final double d when d > 0 -> "positive";
            default -> "negative";
        };
    }
}
