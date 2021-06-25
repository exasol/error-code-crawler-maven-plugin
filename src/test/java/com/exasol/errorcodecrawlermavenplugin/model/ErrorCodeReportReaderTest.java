package com.exasol.errorcodecrawlermavenplugin.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ErrorCodeReportReaderTest {

    @Test
    void testReadReport(@TempDir final Path tempDir) throws ErrorCodeReportReader.ReadException {
        final ErrorCodeReport report = new ErrorCodeReport("my-demo-project", "1.2.3",
                List.of(ErrorMessageDeclaration.builder().identifier(//
                        "E-Test-1")//
                        .prependMessage("My message.")//
                        .addParameter("TEST", "A parameter")//
                        .setPosition("test.java", 2).build()));
        final Path reportFile = tempDir.resolve("error_code_report.json");
        new ErrorCodeReportWriter().writeReport(report, reportFile);
        final ErrorCodeReport readReport = new ErrorCodeReportReader().readReport(reportFile);
        assertThat(readReport, equalTo(report));
    }
}