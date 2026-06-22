package com.exasol.errorcodecrawlermavenplugin;

import java.util.List;

class ErrorCodeCrawlerPluginDefinition {
    private final String version;
    private final List<String> sourcePaths;
    private final String skip;
    private final Integer compilerSource;
    private final Integer compilerRelease;

    private ErrorCodeCrawlerPluginDefinition(final Builder builder) {
        this.version = builder.version;
        this.sourcePaths = builder.sourcePaths;
        this.skip = builder.skip;
        this.compilerSource = builder.compilerSource;
        this.compilerRelease = builder.compilerRelease;
    }

    public static Builder builder(final String version) {
        return new Builder(version);
    }

    public static final class Builder {
        private final String version;
        private List<String> sourcePaths;
        private String skip;
        private Integer compilerSource = 11;
        private Integer compilerRelease;

        private Builder(final String version) {
            this.version = version;
        }

        public Builder sourcePaths(final List<String> sourcePaths) {
            this.sourcePaths = sourcePaths;
            return this;
        }

        public Builder skip(final String skip) {
            this.skip = skip;
            return this;
        }

        public Builder compilerSource(final Integer compilerSource) {
            this.compilerSource = compilerSource;
            return this;
        }

        public Builder compilerRelease(final Integer compilerRelease) {
            this.compilerRelease = compilerRelease;
            return this;
        }

        public ErrorCodeCrawlerPluginDefinition build() {
            return new ErrorCodeCrawlerPluginDefinition(this);
        }
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getSourcePaths() {
        return this.sourcePaths;
    }

    public String getSkip() {
        return skip;
    }

    public Integer getCompilerSource() {
        return this.compilerSource;
    }

    public Integer getCompilerRelease() {
        return this.compilerRelease;
    }
}
