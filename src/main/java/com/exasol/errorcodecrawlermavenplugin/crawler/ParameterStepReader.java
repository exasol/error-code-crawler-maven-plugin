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
        final String signature = builderCall.getExecutable().getSignature();
        final String parameterName = new ArgumentReader().readStringArgumentValue(arguments.get(0), signature);
        final String description = readDescription(arguments, signature);
        final boolean quoted = !signature.startsWith("unquoted");
        errorCodeBuilder.addParameter(parameterName, description, quoted);
    }

    private String readDescription(final List<CtExpression<?>> arguments, final String signature)
            throws InvalidSyntaxException {
        if (arguments.size() == 3) {
            return new ArgumentReader().readStringArgumentValue(arguments.get(2), signature);
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