package com.exasol.errorcodecrawlermavenplugin.writer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorCodeReportReader;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

class ProjectReportWriterTest {
    private static final ErrorMessageDeclaration ERROR_TEST_1 = ErrorMessageDeclaration.builder().identifier("E-TEST-1")
            .prependMessage("My message").setPosition("test.java", 1).build();

    @TempDir
    Path projectDirectory;

    // [utest->dsn~create-target-directory~1]
    @Test
    void testTargetDirectoryCreated() {
        Path reportPath = projectDirectory.resolve(ProjectReportWriter.REPORT_PATH);
        Path targetPath = reportPath.getParent();
        assertFalse(Files.exists(targetPath));
        ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDirectory);
        projectReportWriter.writeReport(new ErrorCodeReport(null, null, Collections.emptyList()));
        assertTrue(Files.exists(targetPath));
    }

    @Test
    void testErrorReportCreated() throws ErrorCodeReportReader.ReadException {
        ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDirectory);
        projectReportWriter.writeReport(new ErrorCodeReport(null, null, List.of(ERROR_TEST_1)));

        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(this.projectDirectory.resolve("target/error_code_report.json"));
        assertThat(result.getErrorMessageDeclarations(), Matchers.containsInAnyOrder(ERROR_TEST_1));
    }

    @Test
    void testErrorReportFailed() {
        projectDirectory.toFile().setWritable(false);
        ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDirectory);
        ErrorCodeReport errorCodeReport = new ErrorCodeReport(null, null, List.of(ERROR_TEST_1));
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> projectReportWriter.writeReport(errorCodeReport));
        assertTrue(exception.getMessage().startsWith("E-ECM-36: Failed to create directory"));
    }


}
