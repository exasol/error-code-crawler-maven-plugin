package com.exasol.errorcodecrawlermavenplugin;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

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
                this.project.getBasedir().toPath(), getClasspath());
        final Path srcMainPath = this.project.getBasedir().toPath().resolve(Path.of("src", "main"));
        final Path srcTestPath = this.project.getBasedir().toPath().resolve(Path.of("src", "test"));
        final ErrorMessageDeclarationCrawler.Result crawlResult = crawler.crawl(srcMainPath)
                .union(crawler.crawl(srcTestPath));
        findings.addAll(crawlResult.getFindings());
        findings.addAll(new ErrorMessageDeclarationValidator().validate(crawlResult.getErrorMessageDeclarations()));
        if (!findings.isEmpty()) {
            final Log log = getLog();
            findings.forEach(finding -> log.error(finding.getMessage()));
            throw new MojoFailureException(ExaError.messageBuilder("E-ECM-3")
                    .message("Error code validation had errors (see previous errors).").toString());
        }
    }

    /**
     * Get the class path of project under test.
     * 
     * @implNote We skip tge first entry of the compile classpath and the first two of the test class path since these
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
