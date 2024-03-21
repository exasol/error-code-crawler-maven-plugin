package com.exasol.errorcodecrawlermavenplugin.aggregator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exsol.errorcodemodel.*;

class ErrorReportAggregatorMojoTest {
    private static final ErrorMessageDeclaration ERROR_TEST_1 = ErrorMessageDeclaration.builder().identifier("E-TEST-1")
            .prependMessage("My message").setPosition("test.java", 1).build();
    private static final ErrorMessageDeclaration ERROR_OTHER_1 = ErrorMessageDeclaration.builder()
            .identifier("E-OTHER-1").prependMessage("My message").setPosition("other.java", 1).build();
    @TempDir
    Path tempDir;

    public static ErrorCodeReport createReportWith(final ErrorMessageDeclaration... errorMessageDeclarations) {
        return new ErrorCodeReport(null, null, List.of(errorMessageDeclarations));
    }

    @Test
    void testMergeReports() throws IOException, MojoFailureException, ErrorCodeReportReader.ReadException {
        writeReport(Path.of("nested1/target"), ERROR_TEST_1);
        writeReport(Path.of("nested2/target"), ERROR_OTHER_1);
        runAggregatorMojo();
        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(this.tempDir.resolve("target/error_code_report.json"));
        assertThat(result.getErrorMessageDeclarations(), Matchers.containsInAnyOrder(ERROR_TEST_1, ERROR_OTHER_1));
    }

    @Test
    void testOverlappingReports() throws IOException {
        writeReport(Path.of("nested1/target"), ERROR_TEST_1);
        writeReport(Path.of("nested2/target"), ERROR_TEST_1);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, this::runAggregatorMojo);
        assertThat(exception.getMessage(),
                startsWith("E-ECM-35: The error tag 'TEST' is used in two nested reportPaths"));
    }

    @Test
    void testInvalidIdentifier() throws IOException {
        writeReport(Path.of("nested1/target"), ErrorMessageDeclaration.builder().identifier("INVALID")
                .prependMessage("My message").setPosition("test.java", 1).build());
        final IllegalStateException exception = assertThrows(IllegalStateException.class, this::runAggregatorMojo);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-34: Found invalid error identifier 'INVALID' while aggregating the reports."));
    }

    private void writeReport(final Path target, final ErrorMessageDeclaration... errorMessageDeclarations)
            throws IOException {
        final Path nested1Target = this.tempDir.resolve(target);
        Files.createDirectories(nested1Target);
        new ErrorCodeReportWriter().writeReport(createReportWith(errorMessageDeclarations),
                nested1Target.resolve("error_code_report.json"));
    }

    private void runAggregatorMojo() throws MojoFailureException {
        final ErrorReportAggregatorMojo aggregatorMojo = new ErrorReportAggregatorMojo();
        final MavenProject project = new MavenProject();
        project.setFile(this.tempDir.resolve("pom.xml").toFile());// sets basedir
        aggregatorMojo.project = project;
        aggregatorMojo.execute();
    }
}