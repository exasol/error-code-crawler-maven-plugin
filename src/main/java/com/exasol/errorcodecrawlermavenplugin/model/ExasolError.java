package com.exasol.errorcodecrawlermavenplugin.model;

public class ExasolError {
    private final String errorCode;
    private final String message;
    private final String sourceFile;
    private final int line;

    private ExasolError(final Builder builder) {
        this.errorCode = builder.errorCode;
        this.message = builder.messageBuilder.toString();
        this.sourceFile = builder.sourceFile;
        this.line = builder.line;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Get the file in which this error message is declared.
     * 
     * @return path of the file relative to the project's directory
     */
    public String getSourceFile() {
        return this.sourceFile;
    }

    /**
     * Line number of the ExaError.messageBuilder call.
     * 
     * @return line number
     */
    public int getLine() {
        return this.line;
    }

    public static class Builder {
        private final StringBuilder messageBuilder = new StringBuilder();
        private String errorCode;
        private String sourceFile;
        private int line = -1;

        private Builder() {
        }

        /**
         * Add an error code.
         *
         * @param errorCode error code to add
         * @return self for fluent programming
         */
        public Builder errorCode(final String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * Set the position where the error message is declared.
         * 
         * @param sourceFile name of the source file relative to the project's root directory
         * @param line       linux number
         * @return self for fluent programming
         */
        public Builder setPosition(final String sourceFile, final int line) {
            this.sourceFile = sourceFile;
            this.line = line;
            return this;
        }

        /**
         * Prepend a message part.
         *
         * @param message message to prepend.
         * @return self for fluent programming
         */
        public Builder prependMessage(final String message) {
            this.messageBuilder.insert(0, message);
            return this;
        }

        public ExasolError build() {
            return new ExasolError(this);
        }
    }
}
