package com.exasol.errorcodecrawlermavenplugin.writer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exsol.errorcodemodel.ErrorCodeReport;


class ProjectReportWriterTest {
    @TempDir Path projectDirectory;

    // [utest->dsn~create-target-directory~1]
    @Test
    void testTargetDirectoryCreated() {
        Path reportPath = projectDirectory.resolve(ProjectReportWriter.REPORT_PATH);
        Path targetPath = reportPath.getParent();
        assertFalse(Files.exists(targetPath));
        ProjectReportWriter projectReportWriter = new ProjectReportWriter(projectDirectory);
        projectReportWriter.writeReport(new ErrorCodeReport("test", "test", Collections.emptyList()));
        assertTrue(Files.exists(targetPath));
    }

}

