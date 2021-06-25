package com.exasol.errorcodecrawlermavenplugin.model;

import java.util.*;

/**
 * This class represents declaration
 */
public class ErrorMessageDeclaration {
    private final String identifier;
    private final String message;
    private final String sourceFile;
    private final int line;
    private final String declaringPackage;
    private final List<String> mitigations;
    private final List<NamedParameter> namedParameters;

    private ErrorMessageDeclaration(final Builder builder) {
        this.identifier = builder.identifier;
        this.message = builder.messageBuilder.toString();
        this.sourceFile = builder.sourceFile;
        this.declaringPackage = builder.declaringPackage;
        this.line = builder.line;
        this.mitigations = builder.mitigations;
        this.namedParameters = builder.namedParameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public String getIdentifier() {
        return this.identifier;
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

    /**
     * Get the message (including placeholders).
     * 
     * @return message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Get a list of mitigations.
     * 
     * @return mitigations
     */
    public List<String> getMitigations() {
        return this.mitigations;
    }

    /**
     * Get the named parameters.
     * 
     * @return list of named parameters
     */
    public List<NamedParameter> getNamedParameters() {
        return this.namedParameters;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final ErrorMessageDeclaration that = (ErrorMessageDeclaration) other;
        return this.line == that.line && Objects.equals(this.identifier, that.identifier)
                && Objects.equals(this.message, that.message) && Objects.equals(this.sourceFile, that.sourceFile)
                && Objects.equals(this.declaringPackage, that.declaringPackage)
                && Objects.equals(this.mitigations, that.mitigations)
                && Objects.equals(this.namedParameters, that.namedParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier, this.message, this.sourceFile, this.line, this.declaringPackage,
                this.mitigations, this.namedParameters);
    }

    @Override
    public String toString() {
        return "ErrorMessageDeclaration{" + "identifier='" + this.identifier + '\'' + ", message='" + this.message
                + '\'' + ", sourceFile='" + this.sourceFile + '\'' + ", line=" + this.line + ", declaringPackage='"
                + this.declaringPackage + '\'' + ", mitigations=" + this.mitigations + ", namedParameters="
                + this.namedParameters + '}';
    }

    public static class Builder {
        private final StringBuilder messageBuilder = new StringBuilder();
        private String identifier;
        private String sourceFile;
        private int line = -1;
        private String declaringPackage;
        private final List<String> mitigations = new LinkedList<>();
        private final List<NamedParameter> namedParameters = new LinkedList<>();

        private Builder() {
        }

        /**
         * Set the error identifier.
         *
         * @param identifier error identifier
         * @return self for fluent programming
         */
        public Builder identifier(final String identifier) {
            this.identifier = identifier;
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

        /**
         * Prepend a mitigation.
         *
         * @param mitigation mitigation to prepend.
         * @return self for fluent programming
         */
        public Builder prependMitigation(final String mitigation) {
            this.mitigations.add(0, mitigation);
            return this;
        }

        /**
         * Append a mitigation.
         *
         * @param mitigation mitigation to prepend.
         * @return self for fluent programming
         */
        public Builder appendMitigation(final String mitigation) {
            this.mitigations.add(mitigation);
            return this;
        }

        /**
         * Add a parameter.
         *
         * @param name        parameter name
         * @param description description
         * @return self for fluent programming
         */
        public Builder addParameter(final String name, final String description) {
            this.namedParameters.add(new NamedParameter(name, description));
            return this;
        }

        public ErrorMessageDeclaration build() {
            return new ErrorMessageDeclaration(this);
        }
    }
}
