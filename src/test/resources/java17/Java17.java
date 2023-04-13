package com.exasol.errorcodecrawlermavenplugin.examples;

public class Java17 {

    void methodWithJava17Features() {
        final var obj = new Object();
        if (obj instanceof final String s) {
            System.out.println(s.length());
        }
    }

    public record Point(int x, int y) {
        public Point(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
    }

    static abstract sealed class Shape permits Circle, Rectangle, Square {
    }

    static final class Circle extends Shape {
    }

    static final class Rectangle extends Shape {
    }

    static final class Square extends Shape {
    }
}
