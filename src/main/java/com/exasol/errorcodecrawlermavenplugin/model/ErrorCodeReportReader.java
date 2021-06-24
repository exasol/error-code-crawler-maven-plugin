package com.exasol.errorcodecrawlermavenplugin.model;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import javax.json.*;
import javax.json.spi.JsonProvider;

import com.exasol.errorreporting.ExaError;

/**
 * This class reads an {@link ErrorCodeReport} from JSON file.
 */
public class ErrorCodeReportReader {
    private static final Set<String> SUPPORTED_SCHEMAS = Set
            .of("https://schemas.exasol.com/error_code_report-0.2.0.json");
    private static final JsonProvider JSON = JsonProvider.provider();

    /**
     * Read an {@link ErrorCodeReport} from JSON file.
     *
     * @param source json file to report from
     * @throws ReadException if something goes wrong
     * @return read {@link ErrorCodeReport}
     */
    public ErrorCodeReport readReport(final Path source) throws ReadException {
        final JsonObject reportJson = readJson(source);
        verifySchemaIsSupported(reportJson);
        final JsonArray errorCodesJson = reportJson.getJsonArray("errorCodes");
        final List<ErrorMessageDeclaration> errorMessageDeclarations = readErrorMessageDeclarations(errorCodesJson);
        return new ErrorCodeReport(reportJson.getString("projectName", ""), reportJson.getString("projectVersion", ""),
                errorMessageDeclarations);
    }

    private List<ErrorMessageDeclaration> readErrorMessageDeclarations(final JsonArray errorCodesJson) {
        final List<ErrorMessageDeclaration> declarations = new ArrayList<>();
        for (final JsonValue jsonValue : errorCodesJson) {
            final JsonObject errorCodeJson = jsonValue.asJsonObject();
            declarations.add(readErrorMessageDeclaration(errorCodeJson));
        }
        return declarations;
    }

    private ErrorMessageDeclaration readErrorMessageDeclaration(final JsonObject errorCodeJson) {
        final ErrorMessageDeclaration.Builder errorDeclarationBuilder = ErrorMessageDeclaration.builder()//
                .identifier(errorCodeJson.getString("identifier", ""))//
                .prependMessage(errorCodeJson.getString("message", ""))
                .setPosition(errorCodeJson.getString("sourceFile", ""), errorCodeJson.getInt("sourceLine", -1));
        readParameters(errorCodeJson, errorDeclarationBuilder);
        readMitigations(errorCodeJson, errorDeclarationBuilder);
        return errorDeclarationBuilder.build();
    }

    private void readMitigations(final JsonObject errorCodeJson,
            final ErrorMessageDeclaration.Builder errorDeclarationBuilder) {
        for (final JsonValue jsonValue : errorCodeJson.getJsonArray("mitigations")) {
            final JsonString mitigation = (JsonString) jsonValue;
            errorDeclarationBuilder.appendMitigation(mitigation.getString());
        }
    }

    private void readParameters(final JsonObject errorCodeJson,
            final ErrorMessageDeclaration.Builder errorDeclarationBuilder) {
        final JsonArray placeholdersJson = errorCodeJson.getJsonArray("messagePlaceholders");
        for (final JsonValue jsonValue : placeholdersJson) {
            final JsonObject placeholderJson = jsonValue.asJsonObject();
            errorDeclarationBuilder.addParameter(placeholderJson.getString("placeholder", ""),
                    placeholderJson.getString("description", ""));
        }
    }

    private void verifySchemaIsSupported(final JsonObject reportJson) throws ReadException {
        final String schema = reportJson.getString("$schema", "");
        if (!SUPPORTED_SCHEMAS.contains(schema)) {
            throw new ReadException(
                    ExaError.messageBuilder("E-ECM-23").message("Unsupported error-report schema {{schema}}.", schema)
                            .mitigation("Supported schemas: {{supported schemas}}.", SUPPORTED_SCHEMAS).toString());
        }
    }

    private JsonObject readJson(final Path source) throws ReadException {
        try (final FileReader fileReader = new FileReader(source.toFile());
                final JsonReader reader = JSON.createReader(fileReader);) {
            return reader.readObject();
        } catch (final IOException exception) {
            throw new ReadException(
                    ExaError.messageBuilder("E-ECM-22").message("Failed to read error-code-report.").toString(),
                    exception);
        }
    }

    /**
     * Exception that is thrown when something went wrong with reading the error-report.
     */
    public static class ReadException extends Exception {
        /**
         * Create a new instance of {@link ReadException}.
         *
         * @param message exception message
         */
        public ReadException(final String message) {
            super(message);
        }

        /**
         * Create a new instance of {@link ReadException}.
         *
         * @param message exception message
         * @param cause   cause
         */
        public ReadException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
