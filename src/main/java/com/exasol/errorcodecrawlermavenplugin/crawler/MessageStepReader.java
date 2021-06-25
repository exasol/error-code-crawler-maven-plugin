package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.util.Set;

import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * Reader for invocations of {@link ErrorMessageBuilder#message(String, Object...)}.
 */
class MessageStepReader extends AbstractTextWithParametersStepReader {
    private static final Set<String> SUPPORTED_SIGNATURES = Set.of("message(java.lang.String,java.lang.Object[])",
            "message(java.lang.String)");

    /**
     * Create a new instance of {@link MessageStepReader}.
     */
    MessageStepReader() {
        super(SUPPORTED_SIGNATURES);
    }

    @Override
    void addTextToBuilder(final String text, final ErrorMessageDeclaration.Builder errorCodeBuilder) {
        errorCodeBuilder.prependMessage(text);
    }
}
