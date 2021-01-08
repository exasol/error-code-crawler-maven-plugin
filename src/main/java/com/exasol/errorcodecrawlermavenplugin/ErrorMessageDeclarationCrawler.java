package com.exasol.errorcodecrawlermavenplugin;

import java.nio.file.Path;
import java.util.*;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorMessageDeclaration;
import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exasol.errorreporting.ExaError;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.code.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Crawler that reads invocations of {@link com.exasol.errorreporting.ExaError#messageBuilder(String)}.
 */
public class ErrorMessageDeclarationCrawler {
    private static final String POSITION = "position";
    private static final String ERRORREPORTING_PACKAGE = "com.exasol.errorreporting";
    private static final String ERROR_MESSAGE_BUILDER = "ErrorMessageBuilder";
    private final Path projectDirectory;
    private final String[] classPath;

    /**
     * Create a new instance of {@link ErrorMessageDeclarationCrawler}.
     * 
     * @param projectDirectory project directory to which all paths as relative for paths in messages
     * @param classPath        classPath with the dependencies of the classes to crawl. In the unit-tests for some
     *                         reason this can be empty. Probably Spoon then picks the class path of this project. When
     *                         run from a jar the classpath is however required.
     */
    public ErrorMessageDeclarationCrawler(final Path projectDirectory, final String[] classPath) {
        this.projectDirectory = projectDirectory;
        this.classPath = classPath;
    }

    /**
     * Crawl error codes for a file / folder.
     * 
     * @param pathsToCrawl file(s) / folder(s) to crawl.
     * @return {@link Result} with found error codes and findings
     */
    public Result crawl(final Path... pathsToCrawl) {
        final List<Finding> findings = new LinkedList<>();
        final List<ErrorMessageDeclaration> errorMessageDeclarations = new ArrayList<>();
        final SpoonAPI spoon = initSpoon(pathsToCrawl);
        for (final CtInvocation<?> methodInvocation : spoon.getModel().getRootPackage()
                .getElements(new TypeFilter<>(CtInvocation.class))) {
            crawl(methodInvocation, findings, errorMessageDeclarations);
        }
        return new Result(errorMessageDeclarations, findings);
    }

    private SpoonAPI initSpoon(final Path... pathsToCrawl) {
        final SpoonAPI spoon = new Launcher();
        final Environment environment = spoon.getEnvironment();
        environment.setSourceClasspath(this.classPath);
        environment.setNoClasspath(false);
        for (final Path path : pathsToCrawl) {
            spoon.addInputResource(path.toString());
        }
        spoon.buildModel();
        spoon.getFactory();
        return spoon;
    }

    /**
     * Find calls to {@link ErrorMessageBuilder#toString()} and then run it's target chain.
     * 
     * @param methodInvocation         method invocation to analyze
     * @param findings                 list of finding to append to
     * @param errorMessageDeclarations list of error codes to append to
     */
    private void crawl(final CtInvocation<?> methodInvocation, final List<Finding> findings,
            final List<ErrorMessageDeclaration> errorMessageDeclarations) {
        final CtExecutableReference<?> method = methodInvocation.getExecutable();
        final CtTypeReference<?> declaringType = method.getDeclaringType();
        if (declaringType == null) {
            return;
        }
        final String methodsClassName = declaringType.getSimpleName();
        if (declaringType.getPackage() == null) {
            return;
        }
        final String methodsPackageName = declaringType.getPackage().getQualifiedName();
        if (methodsPackageName.equals(ERRORREPORTING_PACKAGE) && methodsClassName.equals(ERROR_MESSAGE_BUILDER)
                && method.getSignature().equals("toString()")) {
            try {
                errorMessageDeclarations.add(readErrorCode(methodInvocation));
            } catch (final CrawlFailedException exception) {
                findings.add(exception.getFinding());
            }
        }
    }

    /**
     * Read one ErrorMessageDeclaration code from a builder call.
     * <p>
     * This method iterates the builder call from the {@link ErrorMessageBuilder#toString()} to the
     * {@link ExaError#messageBuilder(String)}. So in the opposite order of invocation.During the iteration it collects
     * information about the error code.
     * </p>
     * 
     * @param methodInvocation invocation of {@link ErrorMessageBuilder#toString()}
     * @return crawled ErrorCode
     * @throws CrawlFailedException in case the call has an invalid syntax
     */
    private ErrorMessageDeclaration readErrorCode(final CtInvocation<?> methodInvocation) throws CrawlFailedException {
        CtExpression<?> target = methodInvocation.getTarget();
        final ErrorMessageDeclaration.Builder errorCodeBuilder = ErrorMessageDeclaration.builder();
        while (target instanceof CtInvocation) {
            final CtInvocation<?> builderCall = (CtInvocation<?>) target;
            addBuilderStep(builderCall, errorCodeBuilder);
            target = builderCall.getTarget();
        }
        return errorCodeBuilder.build();
    }

    /**
     * Read information from a call to a method of the {@link ErrorMessageBuilder} and add it to the passed
     * errorCodeBuilder.
     * 
     * @param builderCall      call to one of the methods of {@link ErrorMessageBuilder} or {@link ExaError}
     * @param errorCodeBuilder error code builder to add the error-code to.
     * @throws CrawlFailedException if the invocation is invalid
     */
    private void addBuilderStep(final CtInvocation<?> builderCall,
            final ErrorMessageDeclaration.Builder errorCodeBuilder) throws CrawlFailedException {
        final CtExecutableReference<?> executable = builderCall.getExecutable();
        final CtTypeReference<?> declaringType = executable.getDeclaringType();
        final String declaringTypeName = declaringType.getSimpleName();
        if (declaringTypeName.equals("ExaError")
                && executable.getSignature().equals("messageBuilder(java.lang.String)")) {
            readMessageBuilderStep(builderCall, errorCodeBuilder);
        }
    }

    /**
     * Read the error-code and the source code position from a call to {@link ExaError#messageBuilder(String)} and add
     * them to the passed errorCodeBuilder.
     * 
     * @param builderCall      invocation of {@link ExaError#messageBuilder(String)}
     * @param errorCodeBuilder error code builder to add the error-code to.
     * @throws CrawlFailedException if the invocation is invalid
     */
    private void readMessageBuilderStep(final CtInvocation<?> builderCall,
            final ErrorMessageDeclaration.Builder errorCodeBuilder) throws CrawlFailedException {
        final List<CtExpression<?>> arguments = builderCall.getArguments();
        checkMessageBuildersArgumentLength(builderCall, arguments);
        final CtExpression<?> argument = arguments.get(0);
        checkMessageBuildersArgumentIsLiteral(builderCall, argument);
        final CtLiteral<?> argumentLiteralValue = (CtLiteral<?>) argument;
        final Object argumentValue = argumentLiteralValue.getValue();
        checkMessageBuildersArgumentValueIsString(builderCall, argumentValue);
        final String errorCode = (String) argumentValue;
        errorCodeBuilder.errorCode(errorCode);
        errorCodeBuilder.setPosition(
                this.projectDirectory.relativize(builderCall.getPosition().getFile().toPath()).toString(),
                builderCall.getPosition().getLine());
    }

    private void checkMessageBuildersArgumentValueIsString(final CtInvocation<?> builderCall,
            final Object argumentValue) throws CrawlFailedException {
        if (!(argumentValue instanceof String)) {
            throw new CrawlFailedException(ExaError.messageBuilder("E-ECM-5")
                    .message("ExaError#messageBuilder(String) must be a string literal.").message(" ({{position}})")
                    .unquotedParameter(POSITION, getFormattedPosition(builderCall)).toString());
        }
    }

    private void checkMessageBuildersArgumentIsLiteral(final CtInvocation<?> builderCall,
            final CtExpression<?> argument) throws CrawlFailedException {
        if (!(argument instanceof CtLiteral)) {
            throw new CrawlFailedException(ExaError.messageBuilder("E-ECM-2")
                    .message("ExaError#messageBuilder(String)'s parameter must be a literal.")
                    .message(" ({{position}})").unquotedParameter(POSITION, getFormattedPosition(builderCall))
                    .toString());
        }
    }

    private void checkMessageBuildersArgumentLength(final CtInvocation<?> builderCall,
            final List<CtExpression<?>> arguments) throws CrawlFailedException {
        if (arguments.size() != 1) {
            throw new CrawlFailedException(ExaError.messageBuilder("F-ECM-1")
                    .message("ExaError#messageBuilder(String) should ony have one argument but had {{numArgs}}.")
                    .parameter("numArgs", arguments.size()).message(" ({{position}})")
                    .unquotedParameter(POSITION, getFormattedPosition(builderCall)).ticketMitigation().toString());
        }
    }

    private String getFormattedPosition(final CtInvocation<?> invocation) {
        return invocation.getPosition().getFile().getName() + ":" + invocation.getPosition().getLine();
    }

    private static class CrawlFailedException extends Exception {
        private final transient Finding finding;

        public CrawlFailedException(final String message) {
            this.finding = new Finding(message);
        }

        public Finding getFinding() {
            return this.finding;
        }
    }

    /**
     * Result of {@link ErrorMessageDeclarationCrawler#crawl(Path...)}
     */
    public static class Result {
        private final List<ErrorMessageDeclaration> errorMessageDeclarations;
        private final List<Finding> findings;

        private Result(final List<ErrorMessageDeclaration> errorMessageDeclarations, final List<Finding> findings) {
            this.errorMessageDeclarations = errorMessageDeclarations;
            this.findings = findings;
        }

        /**
         * Get the crawled error codes.
         * 
         * @return crawled error codes
         */
        public List<ErrorMessageDeclaration> getErrorMessageDeclarations() {
            return this.errorMessageDeclarations;
        }

        /**
         * Get the findings that occurred during the crawling.
         * 
         * @return list of findings that occurred during the crawling
         */
        public List<Finding> getFindings() {
            return this.findings;
        }
    }
}
