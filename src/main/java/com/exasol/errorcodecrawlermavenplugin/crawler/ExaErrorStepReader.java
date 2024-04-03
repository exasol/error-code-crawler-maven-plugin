package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.nio.file.Path;
import java.util.List;

import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * {@link MessageBuilderStepReader} for invocations of
 * {@link com.exasol.errorreporting.ExaError#messageBuilder(String)}.
 */
public class ExaErrorStepReader implements MessageBuilderStepReader {
    private static final String SIGNATURE = "messageBuilder(java.lang.String)";

    private Path rootProjectDirectory;

    /**
     * Create a new instance of {@link ExaErrorStepReader}.
     *
     * @param rootProjectDirectory  root project directory of multimodule project to which all paths are relative (equivalent to projectDirectory for single-module projects)
     */
    public ExaErrorStepReader(Path rootProjectDirectory) {
        this.rootProjectDirectory = rootProjectDirectory;
    }

    @Override
    public void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            final Path projectDirectory) throws InvalidSyntaxException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        assert arguments.size() == 1;
        final var errorCode = new ArgumentReader(SIGNATURE).readStringArgumentValue(arguments.get(0));
        errorCodeBuilder.identifier(errorCode);
        errorCodeBuilder.setPosition(
                rootProjectDirectory.relativize(builderCall.getPosition().getFile().toPath()).toString(),
                builderCall.getPosition().getLine());
    }

    @Override
    public boolean canRead(final String className, final String methodSignature) {
        return className.equals(ExaError.class.getSimpleName()) && methodSignature.equals(SIGNATURE);
    }
}
