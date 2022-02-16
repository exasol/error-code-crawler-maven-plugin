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
        final ErrorCodeConfig config = this.readConfig();
        assertThat(config.getPackagesForErrorTag("EXM"), containsInAnyOrder("com.exasol.example"));
        assertThat(config.getHighestIndexForErrorTag("EXM"), equalTo(6));
    }

    @Test
    void testReadMultiplePackages() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n" //
                + "  EXM:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example.a\n" //
                + "      - com.exasol.example.b\n" //
                + "    highest-index: 6");
        final ErrorCodeConfig config = this.readConfig();
        assertThat(config.getPackagesForErrorTag("EXM"),
                containsInAnyOrder("com.exasol.example.a", "com.exasol.example.b"));
        assertThat(config.getHighestIndexForErrorTag("EXM"), equalTo(6));
    }

    @Test
    void testReadNoPackages() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n" //
                + "  EXM:\n" //
                + "    packages:\n" //
                + "    highest-index: 6");
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, this::readConfig);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-53: Failed to read projects " + CONFIG_NAME + " because of invalid file format."));
        assertThat(exception.getCause().getMessage(), equalTo("E-ECM-55: No packages defined for error code 'EXM'."));
    }

    @Test
    void testReadMultipleTags() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n" //
                + "  EXMA:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example.a\n" //
                + "    highest-index: 6\n" //
                + "  EXMB:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example.b\n" //
                + "    highest-index: 7");
        final ErrorCodeConfig config = this.readConfig();
        assertThat(config.getPackagesForErrorTag("EXMA"), containsInAnyOrder("com.exasol.example.a"));
        assertThat(config.getHighestIndexForErrorTag("EXMA"), equalTo(6));
        assertThat(config.getPackagesForErrorTag("EXMB"), containsInAnyOrder("com.exasol.example.b"));
        assertThat(config.getHighestIndexForErrorTag("EXMB"), equalTo(7));
    }

    @Test
    void testReadFileWithMissingHighestIndexFails() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:\n"//
                + "  EXM:\n" //
                + "    packages:\n" //
                + "      - com.exasol.example");
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, this::readConfig);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-56: Highest index is zero or missing in " + CONFIG_NAME + " for error tags EXM."));
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
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, this::readConfig);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-53: Failed to read projects " + CONFIG_NAME + " because of invalid file format."));
        assertThat(exception.getCause().getMessage(), equalTo(
                "E-ECM-52: Invalid error_code_config.yml. Missing error tags. Add error tags to project configuration."));
    }

    @Test
    void testMissingTags() throws IOException, ErrorCodeConfigException {
        writeConfigFileToTestProject("error-tags:");
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, this::readConfig);
        assertThat(exception.getMessage(),
                equalTo("E-ECM-53: Failed to read projects " + CONFIG_NAME + " because of invalid file format."));
        assertThat(exception.getCause().getMessage(), equalTo(
                "E-ECM-52: Invalid error_code_config.yml. Missing error tags. Add error tags to project configuration."));
    }

    private ErrorCodeConfig readConfig() throws ErrorCodeConfigException {
        return new ErrorCodeConfigReader(this.tempDir).read();
    }

    private void writeConfigFileToTestProject(final String content) throws IOException {
        Files.write(this.tempDir.resolve(CONFIG_NAME), content.getBytes(StandardCharsets.UTF_8));
    }
}
