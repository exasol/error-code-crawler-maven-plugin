package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;
import com.exasol.errorreporting.ExaError;

/**
 * This class parses {@link ErrorCode}s from their string representation.
 */
public class ErrorCodeParser {
    private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("([^-]+)-([^-]+(?:-[^\\d][^-]+)*+)-(\\d+)");

    /**
     * Read an {@link ErrorCode}s from it's string representation.
     * 
     * @param errorCodeString error code's string representation (e.g. E-EX-1)
     * @param sourcePosition  pointer to the source code position, used in error messages
     * @return built {@link ErrorCode}
     * @throws InvalidSyntaxException on syntax errors
     */
    public ErrorCode parse(final String errorCodeString, final String sourcePosition) throws InvalidSyntaxException {
        final Matcher matcher = ERROR_CODE_PATTERN.matcher(errorCodeString);
        if (!matcher.matches()) {
            throw new InvalidSyntaxException(ExaError.messageBuilder("E-ECM-10")
                    .message("The error code {{error code}} has an invalid format. ({{source position}})")
                    .parameter("error code", errorCodeString).unquotedParameter("source position", sourcePosition)
                    .toString());
        }
        final ErrorCode.Type errorType = parseErrorType(matcher.group(1), errorCodeString, sourcePosition);
        final String errorTag = matcher.group(2);
        final int errorIndex = Integer.parseInt(matcher.group(3));
        return new ErrorCode(errorType, errorTag, errorIndex);
    }

    private ErrorCode.Type parseErrorType(final String errorTypeString, final String errorCode,
            final String sourcePosition) throws InvalidSyntaxException {
        try {
            return ErrorCode.Type.valueOf(errorTypeString);
        } catch (final IllegalArgumentException exception) {
            throw new InvalidSyntaxException(
                    ExaError.messageBuilder("E-ECM-11").message("Illegal error code {{error code}}.")
                            .mitigation("The codes must start with 'W-', 'E-' or 'F-'. ({{source position}})")
                            .parameter("error code", errorCode).unquotedParameter("source position", sourcePosition)
                            .toString());
        }
    }
}