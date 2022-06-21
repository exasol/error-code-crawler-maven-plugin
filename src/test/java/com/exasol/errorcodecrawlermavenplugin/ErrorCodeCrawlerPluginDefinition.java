package com.exasol.errorcodecrawlermavenplugin;

import java.util.List;

class ErrorCodeCrawlerPluginDefinition {
    private final String version;
    private final List<String> sourcePaths;
    private final String skip;
    
    public ErrorCodeCrawlerPluginDefinition(final String version, final List<String> sourcePaths) {
        this(version, sourcePaths, null);
    }

    public ErrorCodeCrawlerPluginDefinition(final String version, final List<String> sourcePaths, final String skip) {
        this.version = version;
        this.sourcePaths = sourcePaths;
        this.skip = skip;
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
}
