package com.exasol.errorcodecrawlermavenplugin;

import java.util.List;

class ErrorCodeCrawlerPluginDefinition {
    private final String version;
    private final List<String> sourcePaths;

    public ErrorCodeCrawlerPluginDefinition(final String version, final List<String> sourcePaths) {
        this.version = version;
        this.sourcePaths = sourcePaths;
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getSourcePaths() {
        return this.sourcePaths;
    }
}
