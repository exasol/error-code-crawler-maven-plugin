package com.exasol.errorcodecrawlermavenplugin.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ErrorCodeConfigReaderTest {
    @TempDir
    Path tempDir;

    @Test
    void testRead() throws IOException, ErrorCodeConfigException {
        copyResourceToTestProject("errorCodeConfig/valid.yml");
        final ErrorCodeConfig read = new ErrorCodeConfigReader(this.tempDir).read();
        assertThat(read.getPackagesForErrorTag("EXM"), containsInAnyOrder("com.exasol.example"));
    }

    @Test
    void testConfigFileMissing() {
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class,
                () -> new ErrorCodeConfigReader(this.tempDir));
        assertThat(exception.getMessage(), equalTo(
                "E-ECM-9: Could not find errorCodeConfig.yml in the current project. Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin."));
    }

    @Test
    void testInvalidRoot() throws IOException, ErrorCodeConfigException {
        copyResourceToTestProject("errorCodeConfig/invalidRoot.yml");
        final ErrorCodeConfigReader reader = new ErrorCodeConfigReader(this.tempDir);
        final ErrorCodeConfigException exception = assertThrows(ErrorCodeConfigException.class, reader::read);
        assertThat(exception.getMessage(), equalTo("E-ECM-7: Failed to read projects errorCodeConfig.yml."));
    }

    private void copyResourceToTestProject(final String resourceName) throws IOException {
        Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(resourceName)),
                this.tempDir.resolve(ErrorCodeConfigReader.CONFIG_NAME), StandardCopyOption.REPLACE_EXISTING);
    }
}
