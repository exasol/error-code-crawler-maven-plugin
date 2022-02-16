package com.exasol.errorcodecrawlermavenplugin.validation;

import java.util.Optional;
import java.util.stream.Stream;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfig;
import com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorIdentifier;
import com.exsol.errorcodemodel.ErrorIdentifier.SyntaxException;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

class HighestIndexValidator extends AbstractIndependentErrorMessageDeclarationValidator {
    private final ErrorCodeConfig config;

    HighestIndexValidator(final ErrorCodeConfig config) {
        this.config = config;
    }

    @Override
    protected Stream<Finding> validateSingleErrorMessageDeclaration(final ErrorMessageDeclaration declaration) {
        return parseIdentifier(declaration.getIdentifier())
                .map(identifier -> validateIdentifier(identifier, declaration)) //
                .orElseGet(Stream::empty); // invalid error codes are handled by a different validator
    }

    private Stream<Finding> validateIdentifier(final ErrorIdentifier identifier,
            final ErrorMessageDeclaration declaration) {
        if (!this.config.hasErrorTag(identifier.getTag())) {
            // unknown error codes are handled by a different validator
            return Stream.empty();
        }
        final int highestIndex = this.config.getHighestIndexForErrorTag(identifier.getTag());
        if (highestIndex == 0) {
            // highest index not configured
            return Stream.empty();
        }
        if (highestIndex >= identifier.getIndex()) {
            return Stream.empty();
        }
        return Stream.of(new Finding(ExaError.messageBuilder("E-ECM-54")
                .message("Highest index for tag {{tag}} configured in " + ErrorCodeConfigReader.CONFIG_NAME
                        + " is {{highest index|uq}} but code {{code|uq}} in {{source position|uq}} is higher.")
                .parameter("tag", identifier.getTag()) //
                .parameter("highest index", highestIndex) //
                .parameter("code", identifier.toString()) //
                .parameter("source position", PositionFormatter.getFormattedPosition(declaration)) //
                .mitigation("Update highest index in " + ErrorCodeConfigReader.CONFIG_NAME + ".") //
                .toString()));
    }

    private Optional<ErrorIdentifier> parseIdentifier(final String identfier) {
        try {
            return Optional.of(ErrorIdentifier.parse(identfier));
        } catch (final SyntaxException e) {
            return Optional.empty();
        }
    }
}
