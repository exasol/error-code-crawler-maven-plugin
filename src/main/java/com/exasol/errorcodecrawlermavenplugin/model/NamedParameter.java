package com.exasol.errorcodecrawlermavenplugin.model;

import java.util.Objects;

import com.exasol.errorreporting.ExaError;

/**
 * This class represents a parameter of an {@link ExaError} invocation.
 */
public class NamedParameter {
    private final String name;
    private final String description;

    /**
     * Create a new instance of {@link NamedParameter}.
     * 
     * @param name        parameter name
     * @param description parameter description
     */
    public NamedParameter(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Get the name of the parameter
     * 
     * @return parameter name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the description for this parameter (3rd parameter of parameterCall).
     * 
     * @return description of the parameter
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final NamedParameter that = (NamedParameter) other;
        return Objects.equals(this.name, that.name) && Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.description);
    }

    @Override
    public String toString() {
        return "NamedParameter{" + "name='" + this.name + '\'' + ", description='" + this.description + '\'' + '}';
    }
}
