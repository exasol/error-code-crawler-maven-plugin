package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.nio.file.Path;
import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ErrorMessageBuilder;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * Reader for invocations of {@link ErrorMessageBuilder#mitigation(String)}.
 */
class MitigationStepReader implements MessageBuilderStepReader {
    private static final String SIGNATURE = "mitigation(java.lang.String)";

    @Override
    public void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            final Path projectDirectory) throws InvalidSyntaxException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        assert arguments.size() == 1;
        final CtExpression<?> messageArgument = arguments.get(0);
        errorCodeBuilder.prependMitigation(new ArgumentReader(SIGNATURE).readStringArgumentValue(messageArgument));
    }

    @Override
    public boolean canRead(final String className, final String methodSignature) {
        return className.equals(ErrorMessageBuilder.class.getSimpleName()) && methodSignature.equals(SIGNATURE);
    }
}
