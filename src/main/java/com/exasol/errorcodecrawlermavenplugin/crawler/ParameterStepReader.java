package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ErrorMessageBuilder;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * Reader for invocations of {@link ErrorMessageBuilder#parameter(String, Object)},
 * {@link ErrorMessageBuilder#parameter(String, Object, String)},
 * {@link ErrorMessageBuilder#unquotedParameter(String, Object)} and
 * {@link ErrorMessageBuilder#unquotedParameter(String, Object, String)}.
 */
class ParameterStepReader implements MessageBuilderStepReader {
    private static final Set<String> SUPPORTED_SIGNATURES = Set.of("parameter(java.lang.String,java.lang.Object)",
            "unquotedParameter(java.lang.String,java.lang.Object)",
            "parameter(java.lang.String,java.lang.Object,java.lang.String)",
            "unquotedParameter(java.lang.String,java.lang.Object,java.lang.String)");

    @Override
    public void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            final Path projectDirectory) throws InvalidSyntaxException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        assert arguments.size() > 1;
        final var signature = builderCall.getExecutable().getSignature();
        final var parameterName = new ArgumentReader(signature).readStringArgumentValue(arguments.get(0));
        final var description = readDescription(arguments, signature);
        errorCodeBuilder.addParameter(parameterName, description);
    }

    private String readDescription(final List<CtExpression<?>> arguments, final String signature)
            throws InvalidSyntaxException {
        if (arguments.size() == 3) {
            return new ArgumentReader(signature).readStringArgumentValue(arguments.get(2));
        } else {
            return null;
        }
    }

    @Override
    public boolean canRead(final String className, final String methodSignature) {
        return className.equals(ErrorMessageBuilder.class.getSimpleName())
                && SUPPORTED_SIGNATURES.contains(methodSignature);
    }
}
