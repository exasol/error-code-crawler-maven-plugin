package com.exasol.errorcodecrawlermavenplugin.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the config for a single error tag in the errorCodeConfig.yml. It is created by
 * {@link ErrorCodeConfigReader}.
 */
public class SingleErrorCodeConfig {
    private final List<String> packages;
    private final int highestIndex;

    @JsonCreator
    public SingleErrorCodeConfig(@JsonProperty("packages") final List<String> packages,
            @JsonProperty("highest-index") final int highestIndex) {
        this.packages = packages;
        this.highestIndex = highestIndex;
    }

    List<String> getPackages() {
        return this.packages;
    }

    int getHighestIndex() {
        return this.highestIndex;
    }

    @Override
    public String toString() {
        return "SingleErrorCodeConfig{" + "packages=" + this.packages + ", highestIndex=" + this.highestIndex + '}';
    }
}
