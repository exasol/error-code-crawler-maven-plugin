package com.exasol.errorcodecrawlermavenplugin.config;

import static com.exasol.errorreporting.ExaError.messageBuilder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * This class reads the error code config file into a {@link ErrorCodeConfig}.
 */
//[impl->dsn~config-parser~1]
public class ErrorCodeConfigReader {
    /** Name of the error code configuration file. */
    public static final String CONFIG_NAME = "error_code_config.yml";
    private final File errorConfigFile;

    /**
     * Create a new instance of {@link ErrorCodeConfigReader}.
     * 
     * @param projectDir projects directory
     * @throws ErrorCodeConfigException if the config does not exist
     */
    public ErrorCodeConfigReader(final Path projectDir) throws ErrorCodeConfigException {
        this.errorConfigFile = projectDir.resolve(CONFIG_NAME).toFile();
        if (!this.errorConfigFile.exists()) {
            throw new ErrorCodeConfigException(messageBuilder("E-ECM-9")
                    .message("Could not find " + CONFIG_NAME + " in the current project.")
                    .mitigation(
                            "Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin.")
                    .toString());
        }
    }

    /**
     * Read the configuration.
     * 
     * @return java representation of the config: {@link ErrorCodeConfig}
     * @throws ErrorCodeConfigException if the config has an invalid syntax
     */
    public ErrorCodeConfig read() throws ErrorCodeConfigException {
        final ErrorCodeConfig config = readYamlFile();
        validateHighestIndex(config);
        return config;
    }

    private void validateHighestIndex(final ErrorCodeConfig config) throws ErrorCodeConfigException {
        final List<String> tagsWithoutHighestIndex = config.getErrorTags().stream()
                .filter(tag -> config.getHighestIndexForErrorTag(tag) == 0) //
                .sorted().collect(toList());
        if (!tagsWithoutHighestIndex.isEmpty()) {
            throw new ErrorCodeConfigException(messageBuilder("E-ECM-56")
                    .message(
                            "Highest index is zero or missing in " + CONFIG_NAME + " for error tags {{error tags|uq}}.")
                    .parameter("error tags", tagsWithoutHighestIndex.stream().collect(joining(", "))) //
                    .toString());
        }
    }

    private ErrorCodeConfig readYamlFile() throws ErrorCodeConfigException {
        try {
            final var mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            return mapper.readValue(this.errorConfigFile, ErrorCodeConfig.class);
        } catch (final ValueInstantiationException exception) {
            throw new ErrorCodeConfigException(messageBuilder("E-ECM-53")
                    .message("Failed to read projects " + CONFIG_NAME + " because of invalid file format.").toString(),
                    exception.getCause());
        } catch (final IOException exception) {
            throw new ErrorCodeConfigException(
                    messageBuilder("E-ECM-7").message("Failed to read projects " + CONFIG_NAME + ".").toString(),
                    exception);
        }
    }
}
