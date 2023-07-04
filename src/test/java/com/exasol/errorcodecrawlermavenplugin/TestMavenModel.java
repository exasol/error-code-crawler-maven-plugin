package com.exasol.errorcodecrawlermavenplugin;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class TestMavenModel extends Model {
    private static final long serialVersionUID = 422440090869638523L;

    public TestMavenModel(final ErrorCodeCrawlerPluginDefinition errorCodeCrawlerPluginDefinition) {
        this.setBuild(new Build());
        this.setVersion("1.0.0");
        this.setArtifactId("project-to-test");
        this.setGroupId("com.example");
        this.setModelVersion("4.0.0");
        this.addDependency("error-reporting-java", "com.exasol", "", "1.0.1");
        addCompilerPlugin();
        addErrorCodeCrawlerPlugin(errorCodeCrawlerPluginDefinition);
    }

    public void writeAsPomToProject(final Path projectDir) throws IOException {
        try (final FileWriter fileWriter = new FileWriter(projectDir.resolve("pom.xml").toFile())) {
            new MavenXpp3Writer().write(fileWriter, this);
        }
    }

    public void addDependency(final String artifactId, final String groupId, final String scope, final String version) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setScope(scope);
        this.addDependency(dependency);
    }

    private void addErrorCodeCrawlerPlugin(final ErrorCodeCrawlerPluginDefinition declaration) {
        final Plugin pluginXml = new Plugin();
        pluginXml.setGroupId("com.exasol");
        pluginXml.setArtifactId("error-code-crawler-maven-plugin");
        pluginXml.setVersion(declaration.getVersion());
        final Xpp3Dom configuration = buildConfiguration(declaration);
        pluginXml.setConfiguration(configuration);
        final PluginExecution execution = new PluginExecution();
        execution.setGoals(List.of("verify"));
        pluginXml.setExecutions(List.of(execution));
        this.getBuild().addPlugin(pluginXml);
    }

    private Xpp3Dom buildConfiguration(final ErrorCodeCrawlerPluginDefinition declaration) {
        final Xpp3Dom configuration = new Xpp3Dom("configuration");
        addSourcePath(declaration, configuration);
        addSkip(declaration, configuration);
        return configuration;
    }

    private void addSourcePath(final ErrorCodeCrawlerPluginDefinition declaration, final Xpp3Dom configuration) {
        if (declaration.getSourcePaths() != null) {
            final Xpp3Dom sourcePathsXml = buildXmlList("sourcePaths", "sourcePath", declaration.getSourcePaths());
            configuration.addChild(sourcePathsXml);
        }
    }

    private void addSkip(final ErrorCodeCrawlerPluginDefinition declaration, final Xpp3Dom configuration) {
        if (declaration.getSkip() != null) {
            final Xpp3Dom skipXmlElement = new Xpp3Dom("skip");
            skipXmlElement.setValue(declaration.getSkip());
            configuration.addChild(skipXmlElement);
        }
    }

    private Xpp3Dom buildXmlList(final String containerName, final String itemName, final Collection<String> items) {
        final Xpp3Dom modules = new Xpp3Dom(containerName);
        for (final String item : items) {
            final Xpp3Dom xmlItem = new Xpp3Dom(itemName);
            xmlItem.setValue(item);
            modules.addChild(xmlItem);
        }
        return modules;
    }

    private void addCompilerPlugin() {
        final Plugin pluginXml = new Plugin();
        pluginXml.setGroupId("org.apache.maven.plugins");
        pluginXml.setArtifactId("maven-compiler-plugin");
        pluginXml.setVersion("3.8.1");
        final Xpp3Dom configuration = new Xpp3Dom("configuration");
        // add more
        final Xpp3Dom sourceItem = new Xpp3Dom("source");
        sourceItem.setValue("11");
        configuration.addChild(sourceItem);
        final Xpp3Dom targetItem = new Xpp3Dom("target");
        targetItem.setValue("11");
        configuration.addChild(targetItem);
        pluginXml.setConfiguration(configuration);
        this.getBuild().addPlugin(pluginXml);
    }
}
