package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.util.Set;

import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

/**
 * Reader for invocations of {@link ErrorMessageBuilder#mitigation(String, Object...)}.
 */
class MitigationStepReader extends AbstractTextWithParametersStepReader {
    private static final Set<String> SUPPORTED_SIGNATURES = Set.of("mitigation(java.lang.String,java.lang.Object[])",
            "mitigation(java.lang.String)");

    /**
     * Create a new instance of {@link MitigationStepReader}.
     */
    public MitigationStepReader() {
        super(SUPPORTED_SIGNATURES);
    }

    @Override
    void addTextToBuilder(final String text, final ErrorMessageDeclaration.Builder errorCodeBuilder) {
        errorCodeBuilder.prependMitigation(text);
    }
}
