package com.exasol.errorcodecrawlermavenplugin.writer;

import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorCodeReportWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class wraps {@link ErrorCodeReportWriter}  and project report path
 */
public class ProjectReportWriter {

    /**
     * Represents Error Code Report Path
     */
    public static final Path REPORT_PATH = Path.of("target", "error_code_report.json");

    private final ErrorCodeReportWriter writer;

    private final Path reportPath;

    /**
     * Creates a new instance of {@link ProjectReportWriter}
     *
     * @param projectDir represents project directory Path
     */
    public ProjectReportWriter(Path projectDir) {
        this.reportPath = projectDir.resolve(REPORT_PATH);
        this.writer = new ErrorCodeReportWriter();
    }

    /**
     * Decorates {@link ErrorCodeReport} writeReport method and makes sure that target directory exists before writing
     * the report
     * 
     * @param report {@link ErrorCodeReport}
     */
    // [impl->dsn~create-target-directory~1]
    public void writeReport(ErrorCodeReport report) {
        createTargetDirIfNotExists(reportPath);
        writer.writeReport(report, reportPath);
    }

    private void createTargetDirIfNotExists(final Path reportPath) {
        final Path targetDir = reportPath.getParent();
        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (final IOException exception) {
                throw new IllegalStateException(ExaError.messageBuilder("E-ECM-36")
                        .message("Failed to create directory {{path}} for merged report.", targetDir).toString(),
                        exception);
            }
        }
    }
}
