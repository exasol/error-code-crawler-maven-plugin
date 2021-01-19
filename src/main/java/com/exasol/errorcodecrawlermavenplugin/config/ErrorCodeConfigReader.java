package com.exasol.errorcodecrawlermavenplugin.config;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ErrorCodeConfigReader {
    public static final String CONFIG_NAME = "errorCodeConfig.yml";
    private final File errorConfigFile;

    public ErrorCodeConfigReader(final Path projectDir) throws ErrorCodeConfigException {
        this.errorConfigFile = projectDir.resolve(CONFIG_NAME).toFile();
        if (!this.errorConfigFile.exists()) {
            throw new ErrorCodeConfigException(messageBuilder("E-ECM-9")
                    .message("Could not find errorCodeConfig.yml in the current project.")
                    .mitigation(
                            "Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin.")
                    .toString());
        }
    }

    public ErrorCodeConfig read() throws ErrorCodeConfigException {
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            return mapper.readValue(this.errorConfigFile, ErrorCodeConfig.class);
        } catch (final IOException exception) {
            throw new ErrorCodeConfigException(
                    messageBuilder("E-ECM-7").message("Failed to read projects errorCodeConfig.yml.").toString(),
                    exception);
        }
    }
}
