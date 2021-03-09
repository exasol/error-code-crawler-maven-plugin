package com.exasol.errorcodecrawlermavenplugin.crawler;

import java.nio.file.Path;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;

import spoon.reflect.code.CtInvocation;

/**
 * Interface for classes that read invocations of the error code builder.
 */
interface MessageBuilderStepReader {
    /**
     * Read one method invocation.
     * 
     * @param builderCall      error message builder method invocation
     * @param errorCodeBuilder {@link ErrorMessageDeclaration.Builder} to append the result to
     * @param projectDirectory project's root directory
     * @throws InvalidSyntaxException if the method call has an invalid syntax
     */
    void read(final CtInvocation<?> builderCall, final ErrorMessageDeclaration.Builder errorCodeBuilder,
            Path projectDirectory) throws InvalidSyntaxException;

    /**
     * Get if this reader can read a given method call.
     * 
     * @param className       name of the class the method is defined in
     * @param methodSignature signature of the method
     * @return {@code true} if this reader can read the given method call
     */
    boolean canRead(String className, String methodSignature);
}
