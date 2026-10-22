package com.exasol.errorcodecrawlermavenplugin.examples;

public class Java25 {

    record Point(int x, int y) {
    }

    boolean isNumberWithUnnamedCatchParameter(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (final NumberFormatException _) {
            return false;
        }
    }

    int xCoordinateWithUnnamedPatternInInstanceof(final Object object) {
        if (object instanceof Point(final int x, _)) {
            return x;
        }
        return 0;
    }

    static class Base {
        private final String name;

        Base(final String name) {
            this.name = name;
        }
    }

    static class ValidatingArgumentBeforeSuperConstructorCall extends Base {
        ValidatingArgumentBeforeSuperConstructorCall(final String name) {
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            super(name);
        }
    }
}
