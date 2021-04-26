package com.exasol.errorcodecrawlermavenplugin.crawler;

import static com.exasol.errorcodecrawlermavenplugin.crawler.PositionFormatter.formatPosition;

import java.nio.file.Path;
import java.util.List;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ExaError;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * {@link MessageBuilderStepReader} for invocations of
 * {@link com.exasol.errorreporting.ExaError#messageBuilder(String)}.
 */
public class ExaErrorStepReader implements MessageBuilderStepReader {
    private static final ErrorCodeParser ERROR_CODE_READER = new ErrorCodeParser();
    private static final String SIGNATURE = "messageBuilder(java.lang.String)";

    @Override
    public void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            final Path projectDirectory) throws InvalidSyntaxException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        assert arguments.size() == 1;
        final var errorCode = new ArgumentReader(SIGNATURE).readStringArgumentValue(arguments.get(0));
        errorCodeBuilder.errorCode(ERROR_CODE_READER.parse(errorCode, formatPosition(builderCall.getPosition())));
        errorCodeBuilder.setPosition(
                projectDirectory.relativize(builderCall.getPosition().getFile().toPath()).toString(),
                builderCall.getPosition().getLine());
    }

    @Override
    public boolean canRead(final String className, final String methodSignature) {
        return className.equals(ExaError.class.getSimpleName()) && methodSignature.equals(SIGNATURE);
    }
}
