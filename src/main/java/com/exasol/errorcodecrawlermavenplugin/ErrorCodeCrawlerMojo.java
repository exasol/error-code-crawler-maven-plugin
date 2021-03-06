package com.exasol.errorcodecrawlermavenplugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.exasol.errorcodecrawlermavenplugin.config.*;
import com.exasol.errorcodecrawlermavenplugin.crawler.ErrorMessageDeclarationCrawler;
import com.exasol.errorcodecrawlermavenplugin.validation.ErrorMessageDeclarationValidator;
import com.exasol.errorcodecrawlermavenplugin.validation.ErrorMessageDeclarationValidatorFactory;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorCodeReportWriter;

/**
 * This class is the entry point of the plugin.
 */
//[impl->dsn~mvn-verify-goal~1]
@Mojo(name = "verify", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.VERIFY)
public class ErrorCodeCrawlerMojo extends AbstractMojo {
    private static final Path REPORT_PATH = Path.of("target", "error_code_report.json");

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Glob patterns for files that should be excluded from validation.
     */
    @Parameter(name = "excludes")
    private List<String> excludes;// this variable must have the same name as the parameter

    @Override
    public void execute() throws MojoFailureException {
        final var projectDir = this.project.getBasedir().toPath();
        final ErrorCodeConfig config = readConfig(projectDir);
        final var crawler = new ErrorMessageDeclarationCrawler(projectDir, getClasspath(), getJavaSourceVersion(),
                Objects.requireNonNullElse(this.excludes, Collections.emptyList()));
        final var srcMainPath = projectDir.resolve(Path.of("src", "main", "java"));
        final var srcTestPath = projectDir.resolve(Path.of("src", "test", "java"));
        final var crawlResult = crawler.crawl(srcMainPath, srcTestPath);
        final List<Finding> findings = validateErrorDeclarations(config, crawlResult);
        createTargetDirIfNotExists();
        // [impl->dsn~report-writer~1]
        new ErrorCodeReportWriter().writeReport(new ErrorCodeReport(this.project.getArtifactId(),
                this.project.getVersion(), crawlResult.getErrorMessageDeclarations()), REPORT_PATH);
        reportResult(crawlResult.getErrorMessageDeclarations().size(), findings);
    }

    private void createTargetDirIfNotExists() {
        final Path targetDir = REPORT_PATH.getParent();
        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (final IOException exception) {
                throw new IllegalStateException(
                        ExaError.messageBuilder("E-ECM-24")
                                .message("Failed to create 'target/' directory for error-code-report.").toString(),
                        exception);
            }
        }
    }

    private void reportResult(final int numErrorDeclaration, final List<Finding> findings) throws MojoFailureException {
        final var log = getLog();
        if (!findings.isEmpty()) {
            findings.forEach(finding -> log.error(finding.getMessage()));
            throw new MojoFailureException(ExaError.messageBuilder("E-ECM-3")
                    .message("Error code validation had errors (see previous errors).").toString());
        }
        log.info("Found " + numErrorDeclaration + " valid error message declarations.");
    }

    private List<Finding> validateErrorDeclarations(final ErrorCodeConfig config,
            final ErrorMessageDeclarationCrawler.Result crawlResult) {
        final List<Finding> findings = new LinkedList<>();
        findings.addAll(crawlResult.getFindings());
        final ErrorMessageDeclarationValidator validator = new ErrorMessageDeclarationValidatorFactory()
                .getValidator(config);
        findings.addAll(validator.validate(crawlResult.getErrorMessageDeclarations()));
        return findings;
    }

    private ErrorCodeConfig readConfig(final Path projectDir) throws MojoFailureException {
        try {
            return new ErrorCodeConfigReader(projectDir).read();
        } catch (final ErrorCodeConfigException exception) {
            throw new MojoFailureException(exception.getMessage(), exception.getCause());
        }
    }

    private int getJavaSourceVersion() {
        try {
            final var compilerPlugin = this.project.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            final Xpp3Dom configuration = (Xpp3Dom) compilerPlugin.getConfiguration();
            final String value = configuration.getChild("source").getValue();
            return Integer.parseInt(value);
        } catch (final Exception exception) {
            final var sourceVersion = 5;
            getLog().warn(ExaError.messageBuilder("W-ECM-14")
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
