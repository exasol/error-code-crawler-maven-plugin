package com.exasol.errorcodecrawlermavenplugin.config;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.util.*;

import com.exasol.errorreporting.ExaError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the contents of the error code config file. It is created by {@link ErrorCodeConfigReader}.
 */
public class ErrorCodeConfig {
    private final Map<String, SingleErrorCodeConfig> errorTags;
    private final Map<String, String> packageToErrorCodeMapping;

    /**
     * Create a new instance of {@link ErrorCodeConfig}.
     * 
     * @param errorTags error tag entries
     */
    @JsonCreator
    public ErrorCodeConfig(@JsonProperty("error-tags") final Map<String, SingleErrorCodeConfig> errorTags) {
        this.errorTags = readErrorTags(errorTags);
        this.packageToErrorCodeMapping = inverseMapping(errorTags);
    }

    private static Map<String, SingleErrorCodeConfig> readErrorTags(final Map<String, SingleErrorCodeConfig> errorTags) {
        if (errorTags == null) {
            throw new IllegalArgumentException(messageBuilder("E-ECM-52")
                    .message("Invalid " + ErrorCodeConfigReader.CONFIG_NAME + ". Missing error tags.")
                    .mitigation("Add error tags to project configuration.").toString());
        } else {
            return errorTags;
        }
    }

    private Map<String, String> inverseMapping(final Map<String, SingleErrorCodeConfig> errorTags) {
        final Map<String, String> inverseMapping = new HashMap<>();
        for (final var entry : errorTags.entrySet()) {
            if (entry.getValue().getPackages() == null) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-ECM-55")
                        .message("No packages defined for error code {{error code}}.")
                        .parameter("error code", entry.getKey()).toString());
            }
            for (final String packageName : entry.getValue().getPackages()) {
                verifyThatCodeIsNotUsedTwice(inverseMapping, entry, packageName);
                inverseMapping.put(packageName, entry.getKey());
            }
        }
        return inverseMapping;
    }

    private void verifyThatCodeIsNotUsedTwice(final Map<String, String> inverseMapping,
            final Map.Entry<String, SingleErrorCodeConfig> entry, final String packageName) {
        if (inverseMapping.containsKey(packageName)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ECM-8").message(
                    "Two error codes cover the same package: {{package}} was declared for {{first}} and {{second}}.")
                    .parameter("package", packageName).parameter("first", inverseMapping.get(packageName))
                    .parameter("second", entry.getKey()).toString());
        }
    }

    /**
     * Get all configured error tags.
     *
     * @return all error tags.
     */
    Set<String> getErrorTags() {
        return this.errorTags.keySet();
    }

    /**
     * Get the error tag configured for a specific java package.
     * 
     * @param packageName name of the package
     * @return corresponding error tag
     */
    public Optional<String> getErrorTagForPackage(final String packageName) {
        final Optional<String> longestFittingPackageName = this.packageToErrorCodeMapping.keySet().stream()
                .filter(packageName::startsWith).max(Comparator.comparing(String::length));
        return longestFittingPackageName.map(this.packageToErrorCodeMapping::get);
    }

    /**
     * @param errorTag error tag e.g: {@code EXA-TEST}
     * @return list of packages that can declare this error code
     */
    public List<String> getPackagesForErrorTag(final String errorTag) {
        return this.errorTags.get(errorTag).getPackages();
    }

    /**
     * Check if a specific error tag is declared in the configuration file.
     * 
     * @param errorTag error tag to check
     * @return {@code true} if the tag was declared.
     */
    public boolean hasErrorTag(final String errorTag) {
        return this.errorTags.containsKey(errorTag);
    }

    /**
     * Get the highest index of an error code.
     * 
     * @param errorTag error code
     * @return highest index (according to the config file)
     */
    public int getHighestIndexForErrorTag(final String errorTag) {
        return this.errorTags.get(errorTag).getHighestIndex();
    }

    @Override
    public String toString() {
        return "ErrorCodeConfig{" + "errortags=" + this.errorTags + '}';
    }
}
