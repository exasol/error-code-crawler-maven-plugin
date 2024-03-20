package com.exasol.errorcodecrawlermavenplugin.aggregator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.writer.ProjectReportWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.*;

/**
 * This class is the entrypoint for the aggregate phase. It collects the reports from multiple submodules and merges
 * them into one.
 */
@Mojo(name = "aggregate", defaultPhase = LifecyclePhase.VERIFY)
public class ErrorReportAggregatorMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() {
        final Path projectDir = this.project.getBasedir().toPath();
        final List<Path> reportPaths = findReportsOfNestedProjects(projectDir);
        final List<ReadReport> reports = readReports(reportPaths);
        validateNoOverlappingTags(reports, projectDir);
        final List<ErrorMessageDeclaration> allErrorDeclarations = reports.stream()
                .flatMap(readReport -> readReport.getReport().getErrorMessageDeclarations().stream())
                .collect(Collectors.toList());
        final ErrorCodeReport mergedReport = new ErrorCodeReport(null, null, allErrorDeclarations);
        new ProjectReportWriter(projectDir).writeReport(mergedReport);
    }

    private void validateNoOverlappingTags(final List<ReadReport> reports, final Path projectDir) {
        final Map<String, Path> packagesByTags = new HashMap<>();
        for (final ReadReport readReport : reports) {
            readReport.getReport().getErrorMessageDeclarations().stream()
                    .map(declaration -> this.parseIdentifier(declaration).getTag()).distinct().forEach(tag -> {
                        if (packagesByTags.containsKey(tag)) {
                            throw new IllegalStateException(ExaError.messageBuilder("E-ECM-35").message(
                                    "The error tag {{tag}} is used in two nested reportPaths {{path1}} and {{path2}}.",
                                    tag, projectDir.relativize(readReport.getReportPath()),
                                    projectDir.relativize(packagesByTags.get(tag)))
                                    .mitigation("Please make sure that the different subprojects use different tags.")
                                    .toString());
                        } else {
                            packagesByTags.put(tag, readReport.getReportPath());
                        }
                    });
        }
    }

    private List<ReadReport> readReports(final List<Path> reportPaths) {
        return reportPaths.stream().map(reportPath -> new ReadReport(reportPath, readReport(reportPath)))
                .collect(Collectors.toList());
    }

    private ErrorIdentifier parseIdentifier(final ErrorMessageDeclaration errorMessageDeclaration) {
        try {
            return ErrorIdentifier.parse(errorMessageDeclaration.getIdentifier());
        } catch (final ErrorIdentifier.SyntaxException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("E-ECM-34")
                    .message("Found invalid error identifier {{identifier}} while aggregating the reports.",
                            errorMessageDeclaration.getIdentifier())
                    .toString());
        }
    }

    private ErrorCodeReport readReport(final Path report) {
        try {
            return new ErrorCodeReportReader().readReport(report);
        } catch (final ErrorCodeReportReader.ReadException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-ECM-33")
                            .message("Failed to read nested error-code-report {{path}}.", report).toString(),
                    exception);
        }
    }

    private List<Path> findReportsOfNestedProjects(final Path projectDir) {
        try (final Stream<Path> dirStream = Files.walk(projectDir);) {
            return dirStream.filter(path -> path.endsWith(Path.of("error_code_report.json"))
                    && !path.equals(projectDir.resolve(ProjectReportWriter.REPORT_PATH))).collect(Collectors.toList());
        } catch (final IOException exception) {
            throw new UncheckedIOException(ExaError.messageBuilder("E-ECM-32")
                    .message("Exception while scanning project for nested reports.").toString(), exception);
        }
    }

    private static class ReadReport {
        private final Path reportPath;
        private final ErrorCodeReport report;

        /**
         * @param reportPath path to report
         * @param report     report data
         */
        public ReadReport(final Path reportPath, final ErrorCodeReport report) {
            this.reportPath = reportPath;
            this.report = report;
        }

        public Path getReportPath() {
            return this.reportPath;
        }

        public ErrorCodeReport getReport() {
            return this.report;
        }
    }
}
