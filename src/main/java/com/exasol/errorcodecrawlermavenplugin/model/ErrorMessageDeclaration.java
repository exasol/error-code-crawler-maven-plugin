package com.exasol.errorcodecrawlermavenplugin.model;

/**
 * This class represents declaration
 */
public class ErrorMessageDeclaration {
    private final ErrorCode errorCode;
    private final String message;
    private final String sourceFile;
    private final int line;
    private final String declaringPackage;

    private ErrorMessageDeclaration(final Builder builder) {
        this.errorCode = builder.errorCode;
        this.message = builder.messageBuilder.toString();
        this.sourceFile = builder.sourceFile;
        this.declaringPackage = builder.declaringPackage;
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
    public ErrorCode getErrorCode() {
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

    /**
     * Get the declaring java package.
     * 
     * @return declaring java package
     */
    public String getDeclaringPackage() {
        return this.declaringPackage;
    }

    public static class Builder {
        private final StringBuilder messageBuilder = new StringBuilder();
        private ErrorCode errorCode;
        private String sourceFile;
        private int line = -1;
        private String declaringPackage;

        private Builder() {
        }

        /**
         * Add an error code.
         *
         * @param errorCode error code to add
         * @return self for fluent programming
         */
        public Builder errorCode(final ErrorCode errorCode) {
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
         * Set the declaring java-package.
         * 
         * @param declaringPackage declaring java package
         * @return self for fluent programming
         */
        public Builder declaringPackage(final String declaringPackage) {
            this.declaringPackage = declaringPackage;
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

        public ErrorMessageDeclaration build() {
            return new ErrorMessageDeclaration(this);
        }
    }
}
