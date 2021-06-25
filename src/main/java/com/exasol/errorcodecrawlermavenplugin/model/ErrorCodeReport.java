package com.exasol.errorcodecrawlermavenplugin.model;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a error-code-report in Java.
 */
public class ErrorCodeReport {
    final List<ErrorMessageDeclaration> errorMessageDeclarations;
    private final String projectName;
    private final String projectVersion;

    /**
     * Create a new instance of {@link ErrorCodeReport}.
     * 
     * @param projectName              name of the project
     * @param projectVersion           version of the project
     * @param errorMessageDeclarations list of error message declarations
     */
    public ErrorCodeReport(final String projectName, final String projectVersion,
            final List<ErrorMessageDeclaration> errorMessageDeclarations) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.errorMessageDeclarations = errorMessageDeclarations;
    }

    /**
     * Get the project name.
     * 
     * @return name of the project
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Get the project version.
     * 
     * @return version of the project
     */
    public String getProjectVersion() {
        return this.projectVersion;
    }

    /**
     * Get the error message declarations.
     * 
     * @return list of error message declarations
     */
    public List<ErrorMessageDeclaration> getErrorMessageDeclarations() {
        return this.errorMessageDeclarations;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final ErrorCodeReport that = (ErrorCodeReport) other;
        return Objects.equals(this.projectName, that.projectName)
                && Objects.equals(this.projectVersion, that.projectVersion)
                && Objects.equals(this.errorMessageDeclarations, that.errorMessageDeclarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.projectName, this.projectVersion, this.errorMessageDeclarations);
    }

    @Override
    public String toString() {
        return "ErrorCodeReport{" + "projectName='" + this.projectName + '\'' + ", projectVersion='"
                + this.projectVersion + '\'' + ", errorMessageDeclarations=" + this.errorMessageDeclarations + '}';
    }
}
