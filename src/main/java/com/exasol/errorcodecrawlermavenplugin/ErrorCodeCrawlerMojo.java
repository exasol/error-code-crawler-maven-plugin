package com.exasol.errorcodecrawlermavenplugin;

import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigException;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader;
import com.exasol.errorcodecrawlermavenplugin.crawler.ErrorMessageDeclarationCrawler;
import com.exasol.errorcodecrawlermavenplugin.helper.ErrorMessageDeclarationHelper;
import com.exasol.errorcodecrawlermavenplugin.validation.ErrorMessageDeclarationValidator;
import com.exasol.errorcodecrawlermavenplugin.validation.ErrorMessageDeclarationValidatorFactory;
import com.exasol.errorcodecrawlermavenplugin.writer.ProjectReportWriter;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This class is the entry point of the plugin.
 */
// [impl->dsn~mvn-verify-goal~1]
// [impl->dsn~mvn-plugin-thread-safe~1]
@Mojo(name = "verify", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ErrorCodeCrawlerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

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

    // [impl->dsn~src-directories]
    // [impl->dsn~src-directory-override]
    private List<Path> getSourcePaths() {
        if (this.sourcePaths == null || this.sourcePaths.isEmpty()) {
            return List.of(Path.of("src/main/java"));
        } else {
            return this.sourcePaths.stream().map(Path::of).collect(toList());
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
            final var projectDir = this.project.getBasedir().toPath();
            final ErrorCodeConfig config = readConfig(projectDir);
            final List<Path> classpath = getClasspath();
            getLog().debug("Using classpath " + classpath);
            final var crawler = new ErrorMessageDeclarationCrawler(projectDir, classpath, getJavaSourceVersion(),
                    Objects.requireNonNullElse(this.excludes, Collections.emptyList()));
            final List<Path> absoluteSourcePaths = getSourcePaths().stream().map(projectDir::resolve).collect(toList());
            getLog().debug("Crawling " + absoluteSourcePaths.size() + " paths: " + absoluteSourcePaths);
            final var crawlResult = crawler.crawl(absoluteSourcePaths);
            final List<Finding> findings = validateErrorDeclarations(config, crawlResult);
            List<ErrorMessageDeclaration> errorMessageDeclarations = crawlResult.getErrorMessageDeclarations();
            if (hasCustomSourcePath()) {
                // [impl->dsn~no-src-location-in-report-for-custom-source-path~1]
                errorMessageDeclarations = removeSourcePositions(errorMessageDeclarations);
            } else {
                final boolean isSubProject = this.project.hasParent();
                if (isSubProject) {
                    String prefix = projectDir.toFile().getName();
                    errorMessageDeclarations = addPrefixToSourcePositions(prefix, errorMessageDeclarations);
                }
            }
            final ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDir);
            // [impl->dsn~report-writer~1]
            projectReportWriter.writeReport(new ErrorCodeReport(this.project.getArtifactId(),
                    this.project.getVersion(), errorMessageDeclarations));
            reportResult(errorMessageDeclarations.size(), findings);
        }
    }

    private List<ErrorMessageDeclaration> removeSourcePositions(final List<ErrorMessageDeclaration> declarations) {
        return declarations.stream().map(ErrorMessageDeclaration::withoutSourcePosition).collect(Collectors.toList());
    }

    private List<ErrorMessageDeclaration> addPrefixToSourcePositions(String prefix, final List<ErrorMessageDeclaration> declarations) {
        return declarations.stream().map(e -> ErrorMessageDeclarationHelper.copy(prefix, e)).toList();
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
     * @implNote We skip the first entry of the compile classpath since this are the built classes.
     * 
     * @return the class path
     */
    private List<Path> getClasspath() {
        try {
            final List<String> compileClasspath = this.project.getCompileClasspathElements();
            return compileClasspath.stream().skip(1) //
                    .map(Path::of) //
                    .collect(toList());
        } catch (final DependencyResolutionRequiredException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-ECM-6").message("Failed to extract project's class path.").toString(),
                    exception);
        }
    }
}
