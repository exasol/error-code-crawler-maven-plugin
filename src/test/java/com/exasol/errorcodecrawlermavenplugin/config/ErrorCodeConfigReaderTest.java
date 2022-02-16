package com.exasol.errorcodecrawlermavenplugin.config;

import static com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader.CONFIG_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//[utest->dsn~config-parser~1]
class ErrorCodeConfigReaderTest {
    @TempDir
    Path tempDir;

    @Test
    void testRead() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n" //
                + "  EXM:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example\n" //
                + "    highest-index: 6");
        final ErrorCodeConfig read = new ErrorCodeConfigReader(this.tempDir).read();
        assertThat(read.getPackagesForErrorTag("EXM"), containsInAnyOrder("com.exasol.example"));
        assertThat(read.getHighestIndexForErrorTag("EXM"), equalTo(6));
    }

    @Test
    void testReadFileWithMissingHighestIndexReturnsZero() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n"//
                + "  EXM:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example\n" //
                + "");
        final ErrorCodeConfig read = new ErrorCodeConfigReader(this.tempDir).read();
        assertThat(read.getPackagesForErrorTag("EXM"), containsInAnyOrder("com.exasol.example"));
        assertThat(read.getHighestIndexForErrorTag("EXM"), equalTo(0));
    }

    @Test
    void testConfigFileMissing() {
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class,
                () -> new ErrorCodeConfigReader(this.tempDir));
        assertThat(exception.getMessage(), equalTo("E-ECM-9: Could not find " + CONFIG_NAME
                + " in the current project. Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin."));
    }

    @Test
    void testInvalidRoot() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("unknown: 123");
        final ErrorCodeConfigReader reader = new ErrorCodeConfigReader(this.tempDir);
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, reader::read);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-53: Failed to read projects " + CONFIG_NAME + " because of invalid file format."));
        assertThat(exception.getCause().getMessage(), equalTo(
                "E-ECM-52: Invalid error_code_config.yml. Missing error tags. Add error tags to project configuration."));
    }

    @Test
    void testMissingTags() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:");
        final ErrorCodeConfigReader reader = new ErrorCodeConfigReader(this.tempDir);
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, reader::read);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-53: Failed to read projects " + CONFIG_NAME + " because of invalid file format."));
        assertThat(exception.getCause().getMessage(), equalTo(
                "E-ECM-52: Invalid error_code_config.yml. Missing error tags. Add error tags to project configuration."));
    }

    private void writeConfigFileToTestProject(final String content) throws IOException {
        Files.write(this.tempDir.resolve(CONFIG_NAME), content.getBytes(StandardCharsets.UTF_8));
    }
}
