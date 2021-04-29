package com.exasol.errorcodecrawlermavenplugin.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the config for a single error tag in the error code config file. It is created by
 * {@link ErrorCodeConfigReader}.
 */
public class SingleErrorCodeConfig {
    private final List<String> packages;
    private final int highestIndex;

    /**
     * Create a new instance of {@link SingleErrorCodeConfig}.
     * 
     * @param packages     packages belonging to this error tag
     * @param highestIndex highest index of this error tag
     */
    @JsonCreator
    public SingleErrorCodeConfig(@JsonProperty("packages") final List<String> packages,
            @JsonProperty("highest-index") final int highestIndex) {
        this.packages = packages;
        this.highestIndex = highestIndex;
    }

    /**
     * Get the packages that belong to this error tag.
     * 
     * @return packages that belong to this error tag
     */
    List<String> getPackages() {
        return this.packages;
    }

    /**
     * Get the highest index of this error tag.
     * 
     * @return highest index
     */
    int getHighestIndex() {
        return this.highestIndex;
    }

    @Override
    public String toString() {
        return "SingleErrorCodeConfig{" + "packages=" + this.packages + ", highestIndex=" + this.highestIndex + '}';
    }
}
