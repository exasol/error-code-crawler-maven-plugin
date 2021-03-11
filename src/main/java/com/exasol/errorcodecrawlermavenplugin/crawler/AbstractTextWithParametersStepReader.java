package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ErrorMessageBuilder;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * Abstract basis for step readers that read from method that have one String parameter with placeholders followed by
 * inline parameters values.
 */
abstract class AbstractTextWithParametersStepReader implements MessageBuilderStepReader {
    private final Set<String> supportedSignatures;

    /**
     * Create a new instance of {@link AbstractTextWithParametersStepReader}.
     * 
     * @param supportedSignatures set of supported signatures
     */
    AbstractTextWithParametersStepReader(final Set<String> supportedSignatures) {
        this.supportedSignatures = supportedSignatures;
    }

    /**
     * Add the text of message / mitigation to the error code builder.
     * 
     * @param text             text to add
     * @param errorCodeBuilder error code builder to add the text to
     */
    abstract void addTextToBuilder(final String text, final ErrorMessageDeclaration.Builder errorCodeBuilder);

    @Override
    public void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            final Path projectDirectory) throws InvalidSyntaxException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        assert !arguments.isEmpty();
        final CtExpression<?> messageArgument = arguments.get(0);
        final String text = new ArgumentReader(builderCall.getExecutable().getSignature())
                .readStringArgumentValue(messageArgument);
        addTextToBuilder(text, errorCodeBuilder);
        new DirectParameterReader().readInlineParameters(arguments.size() - 1, text, errorCodeBuilder);
    }

    @Override
    public boolean canRead(final String className, final String methodSignature) {
        return className.equals(ErrorMessageBuilder.class.getSimpleName())
                && this.supportedSignatures.contains(methodSignature);
    }
}
