package com.exasol.errorcodecrawlermavenplugin;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.exasol.errorreporting.ExaError;

/**
 * This class is the entry point of the plugin.
 */
@Mojo(name = "verify", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.VERIFY)
public class ErrorCodeCrawlerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<Finding> findings = new LinkedList<>();
        final ErrorMessageDeclarationCrawler crawler = new ErrorMessageDeclarationCrawler(
                this.project.getBasedir().toPath(), getClasspath(), getJavaSourceVersion());
        final Path srcMainPath = this.project.getBasedir().toPath().resolve(Path.of("src", "main"));
        final Path srcTestPath = this.project.getBasedir().toPath().resolve(Path.of("src", "test"));
        final ErrorMessageDeclarationCrawler.Result crawlResult = crawler.crawl(srcMainPath, srcTestPath);
        findings.addAll(crawlResult.getFindings());
        findings.addAll(new ErrorMessageDeclarationValidator().validate(crawlResult.getErrorMessageDeclarations()));
        final Log log = getLog();
        if (!findings.isEmpty()) {
            findings.forEach(finding -> log.error(finding.getMessage()));
            throw new MojoFailureException(ExaError.messageBuilder("E-ECM-3")
                    .message("Error code validation had errors (see previous errors).").toString());
        }
        log.info("Found " + crawlResult.getErrorMessageDeclarations().size() + " valid error message declarations.");
    }

    private int getJavaSourceVersion() {
        try {
            final Plugin compilerPlugin = this.project.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            final Xpp3Dom configuration = (Xpp3Dom) compilerPlugin.getConfiguration();
            final String value = configuration.getChild("source").getValue();
            return Integer.parseInt(value);
        } catch (final Exception exception) {
            final int sourceVersion = 5;
            getLog().warn(ExaError.messageBuilder("W-ECM-5")
                    .message("Failed to read java source version from POM file. Falling back to {{version}}.")
                    .mitigation(
                            "This plugin reads the java source version from the configuration of the maven-compiler-plugin. Check that the version is defined there correctly.")
                    .parameter("version", sourceVersion).toString());
            return sourceVersion;
        }
    }

    /**
     * Get the class path of project under test.
     * 
     * @implNote We skip the first entry of the compile classpath and the first two of the test class path since these
     *           are the built classes and test-classes.
     * 
     * @return the class path
     */
    private String[] getClasspath() {
        try {
            final List<String> compileClasspath = this.project.getCompileClasspathElements();
            final List<String> testClasspath = this.project.getTestClasspathElements();
            return Stream.concat(compileClasspath.stream().skip(1), testClasspath.stream().skip(2))
                    .toArray(String[]::new);
        } catch (final DependencyResolutionRequiredException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-ECM-6").message("Failed to extract project's class path.").toString(),
                    exception);
        }
    }
}
