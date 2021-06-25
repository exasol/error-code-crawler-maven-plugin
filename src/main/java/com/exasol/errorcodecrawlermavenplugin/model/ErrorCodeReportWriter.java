package com.exasol.errorcodecrawlermavenplugin.model;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.json.*;
import javax.json.spi.JsonProvider;

import com.exasol.errorreporting.ExaError;

/**
 * This class writes an {@link ErrorCodeReport} to JSON file.
 */
public class ErrorCodeReportWriter {
    public static final String SCHEMA = "https://schemas.exasol.com/error_code_report-0.2.0.json";
    private static final JsonProvider JSON = JsonProvider.provider();

    /**
     * Write an {@link ErrorCodeReport} to JSON file.
     * 
     * @param report report to write
     * @param target target file
     */
    public void writeReport(final ErrorCodeReport report, final Path target) {
        final JsonObject reportJson = renderReport(report);
        try (final FileWriter fileWriter = new FileWriter(target.toFile());
                final JsonWriter jsonWriter = JSON.createWriter(fileWriter)) {
            jsonWriter.write(reportJson);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-ECM-21").message("Failed to write error-code-report.").toString(),
                    exception);
        }
    }

    private JsonObject renderReport(final ErrorCodeReport report) {
        final JsonObjectBuilder reportJson = JSON.createObjectBuilder();
        reportJson.add("$schema", SCHEMA);
        reportJson.add("projectName", report.getProjectName());
        reportJson.add("projectVersion", report.getProjectVersion());
        reportJson.add("errorCodes", renderErrorCodes(report.getErrorMessageDeclarations()));
        return reportJson.build();
    }

    private JsonArrayBuilder renderErrorCodes(final List<ErrorMessageDeclaration> errorMessageDeclarations) {
        final JsonArrayBuilder errorCodesBuilder = JSON.createArrayBuilder();
        for (final ErrorMessageDeclaration errorMessageDeclaration : errorMessageDeclarations) {
            errorCodesBuilder.add(renderErrorCode(errorMessageDeclaration));
        }
        return errorCodesBuilder;
    }

    private JsonObjectBuilder renderErrorCode(final ErrorMessageDeclaration errorMessageDeclaration) {
        final JsonObjectBuilder errorCode = JSON.createObjectBuilder();
        addIfNotNull(errorCode, "identifier", errorMessageDeclaration.getIdentifier());
        addIfNotNull(errorCode, "message", errorMessageDeclaration.getMessage());
        errorCode.add("messagePlaceholders", renderPlaceholders(errorMessageDeclaration.getNamedParameters()));
        addIfNotNull(errorCode, "sourceFile", errorMessageDeclaration.getSourceFile());
        errorCode.add("sourceLine", errorMessageDeclaration.getLine());
        errorCode.add("mitigations", renderMitigations(errorMessageDeclaration));
        return errorCode;
    }

    private JsonArrayBuilder renderMitigations(final ErrorMessageDeclaration errorMessageDeclaration) {
        final JsonArrayBuilder mitigationsJson = JSON.createArrayBuilder();
        for (final String mitigation : errorMessageDeclaration.getMitigations()) {
            mitigationsJson.add(mitigation);
        }
        return mitigationsJson;
    }

    private JsonArrayBuilder renderPlaceholders(final List<NamedParameter> placeholders) {
        final JsonArrayBuilder placeholdersJsonBuilder = JSON.createArrayBuilder();
        for (final NamedParameter placeholder : placeholders) {
            final JsonObjectBuilder placeholderJsonBuilder = JSON.createObjectBuilder();
            placeholderJsonBuilder.add("placeholder", placeholder.getName());
            addIfNotNull(placeholderJsonBuilder, "description", placeholder.getDescription());
            placeholdersJsonBuilder.add(placeholderJsonBuilder);
        }
        return placeholdersJsonBuilder;
    }

    private void addIfNotNull(final JsonObjectBuilder builder, final String key, final String value) {
        if (value != null) {
            builder.add(key, value);
        }
    }
}
