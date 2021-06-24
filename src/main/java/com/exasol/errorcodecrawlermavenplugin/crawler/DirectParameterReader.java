package com.exasol.errorcodecrawlermavenplugin.crawler;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.Placeholder;
import com.exasol.errorreporting.PlaceholderMatcher;

/**
 * Reader inline parameters from messages.
 */
class DirectParameterReader {

    /**
     * Read the inline parameters from messages.
     * 
     * @param inlineParameterArgumentsCount count of arguments used for inline parameters
     * @param text                          text containing the placeholders
     * @param errorCodeBuilder              {@link ErrorMessageDeclaration.Builder} to append the parameters to
     */
    public void readInlineParameters(final int inlineParameterArgumentsCount, final String text,
            final ErrorMessageDeclaration.Builder errorCodeBuilder) {
        if (inlineParameterArgumentsCount > 0) {
            final Iterable<Placeholder> placeholders = PlaceholderMatcher.findPlaceholders(text);
            var placeholderCounter = 0;
            for (final Placeholder placeholder : placeholders) {
                if (placeholderCounter >= inlineParameterArgumentsCount) {
                    break; // there is not argument for this placeholder
                }
                errorCodeBuilder.addParameter(placeholder.getName(), null);
                placeholderCounter++;
            }
        }
    }
}
