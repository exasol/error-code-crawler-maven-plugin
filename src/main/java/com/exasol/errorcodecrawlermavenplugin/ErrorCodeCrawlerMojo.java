package com.exasol.errorcodecrawlermavenplugin;

import java.nio.file.Path;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
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
import com.exasol.errorcodecrawlermavenplugin.writer.ProjectReportWriter;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * This class is the entry point of the plugin.
 */
// [impl->dsn~mvn-verify-goal~1]
// [impl->dsn~mvn-plugin-thread-safe~1]
@Mojo(name = "verify", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ErrorCodeCrawlerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "session.executionRootDirectory", required = true, readonly = true)
    String executionRootDirectory;

    // [impl->dsn~skip-execution~1]
    @Parameter(property = "error-code-crawler.skip", defaultValue = "false")
    String skip;

    /**
     * Glob patterns for files that should be excluded from validation.
     */
    @Parameter(name = "excludes")
    private List<String> excludes;// this variable must have the same name as the parameter

    @Parameter(name = "sourcePaths")
    private List<String> sourcePaths;

    /**
     * Constructs a new instance of {@code ErrorCodeCrawlerMojo}.
     * <p>
     * This constructor is required by Maven to instantiate the Mojo during the build lifecycle. All parameter
     * injection is handled by Maven through annotated fields, so this constructor does not perform any initialization logic.
     * </p>
     *
     * Note: This constructor should remain public and parameterless to be compatible with Maven's plugin instantiation mechanism.
     */
    public ErrorCodeCrawlerMojo() {
        // Default constructor required for instantiation
    }

    // [impl->dsn~src-directories]
    // [impl->dsn~src-directory-override]
    private List<Path> getSourcePaths(final Path rootProjectDir, final Path projectDir) {
        if (this.sourcePaths == null || this.sourcePaths.isEmpty()) {
            return List.of(Path.of(rootProjectDir.relativize(projectDir).toString(), "src", "main", "java"));
        } else {
            return this.sourcePaths.stream().map(Path::of).toList();
        }
    }

    private boolean hasCustomSourcePath() {
        return this.sourcePaths != null && !this.sourcePaths.isEmpty();
    }

    /**
     * Check if the plugin is enabled and should run.
     * 
     * @return {@code true} if the plugin is enabled, else {@code false}
     */
    // [impl->dsn~skip-execution~1]
    protected boolean isEnabled() {
        if ("true".equals(this.skip)) {
            getLog().info("Skipping error-code crawling.");
            return false;
        } else if ("false".equals(this.skip)) {
            return true;
        } else {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-ECM-51")
                    .message("Invalid value {{value}} for property 'error-code-crawler.skip'.", this.skip)
                    .mitigation("Please set the property to 'true' or 'false'.").toString());
        }
    }

    @Override
    public void execute() throws MojoFailureException {
        if (isEnabled()) {
            final var projectDir = getProjectDir();
            final var rootProjectDir = getRootProjectDir(projectDir);
            final ErrorCodeConfig config = readConfig(projectDir);
            final List<Path> classpath = getClasspath();
            getLog().debug("Using classpath " + classpath);
            final var crawler = new ErrorMessageDeclarationCrawler(rootProjectDir, projectDir, classpath, getJavaSourceVersion(),
                    Objects.requireNonNullElse(this.excludes, Collections.emptyList()));
            final List<Path> absoluteSourcePaths = getSourcePaths(rootProjectDir, projectDir).stream().map(rootProjectDir::resolve).toList();
            getLog().debug("Crawling " + absoluteSourcePaths.size() + " paths: " + absoluteSourcePaths);
            final var crawlResult = crawler.crawl(absoluteSourcePaths);
            final List<Finding> findings = validateErrorDeclarations(config, crawlResult);
            List<ErrorMessageDeclaration> errorMessageDeclarations = crawlResult.getErrorMessageDeclarations();
            if (hasCustomSourcePath()) {
                // [impl->dsn~no-src-location-in-report-for-custom-source-path~1]
                errorMessageDeclarations = removeSourcePositions(errorMessageDeclarations);
            }
            final ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDir);
            // [impl->dsn~report-writer~1]
            projectReportWriter.writeReport(new ErrorCodeReport(this.project.getArtifactId(),
                    this.project.getVersion(), errorMessageDeclarations));
            reportResult(errorMessageDeclarations.size(), findings);
        }
    }

    private Path getProjectDir() {
        return this.project.getBasedir().toPath();
    }

    private Path getRootProjectDir(final Path projectDir) {
        return StringUtils.isBlank(executionRootDirectory) ? projectDir : Path.of(executionRootDirectory);
    }

    private List<ErrorMessageDeclaration> removeSourcePositions(final List<ErrorMessageDeclaration> declarations) {
        return declarations.stream().map(ErrorMessageDeclaration::withoutSourcePosition).toList();
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

    int getJavaSourceVersion() {
        try {
            final var compilerPlugin = this.project.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            final Xpp3Dom configuration = (Xpp3Dom) compilerPlugin.getConfiguration();
            final String value = getCompilerSourceVersion(configuration);
            return Integer.parseInt(value);
        } catch (final Exception exception) {
            final var sourceVersion = 5;
            getLog().warn(ExaError.messageBuilder("W-ECM-14")
                    .message("Failed to read java source version from POM file. Falling back to {{version}}.")
                    .mitigation(
                            "This plugin reads the java source version from the <release> or <source> configuration of the maven-compiler-plugin. Check that the version is defined there correctly.")
                    .parameter("version", sourceVersion).toString());
            return sourceVersion;
        }
    }

    private String getCompilerSourceVersion(final Xpp3Dom configuration) {
        final Xpp3Dom release = configuration.getChild("release");
        if ((release != null) && StringUtils.isNotBlank(release.getValue())) {
            return release.getValue();
        }
        return configuration.getChild("source").getValue();
    }

    /**
     * Get the class path of project under test.
     * 
     * @implNote We skip the first entry of the compile classpath since this are the built classes.
     * 
     * @return the class path
     */
    private List<Path> getClasspath() {
        try {
            final List<String> compileClasspath = this.project.getCompileClasspathElements();
            return compileClasspath.stream().skip(1) //
                    .map(Path::of) //
                    .toList();
        } catch (final DependencyResolutionRequiredException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-ECM-6").message("Failed to extract project's class path.").toString(),
                    exception);
        }
    }
}
