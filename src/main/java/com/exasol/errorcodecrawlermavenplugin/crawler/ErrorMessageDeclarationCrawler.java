package com.exasol.errorcodecrawlermavenplugin.crawler;

import static java.util.Collections.emptyList;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorcodecrawlermavenplugin.Finding;
import com.exasol.errorreporting.ErrorMessageBuilder;
import com.exasol.errorreporting.ExaError;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Crawler that reads invocations of {@link com.exasol.errorreporting.ExaError#messageBuilder(String)}.
 */
// [impl->dsn~error-declaration-crawler~1]
public class ErrorMessageDeclarationCrawler {
    private static final String ERRORREPORTING_PACKAGE = "com.exasol.errorreporting";
    private static final List<MessageBuilderStepReader> STEP_READERS = List.of(new ExaErrorStepReader(),
            new ParameterStepReader(), new MessageStepReader(), new MitigationStepReader());
    private final Path projectDirectory;
    private final List<Path> classPath;
    private final int javaSourceVersion;
    private final List<PathMatcher> excludedFilesMatchers;

    /**
     * Create a new instance of {@link ErrorMessageDeclarationCrawler}.
     * 
     * @param projectDirectory  project directory to which all paths as relative for paths in messages
     * @param classPath         classPath with the dependencies of the classes to crawl. In the unit-tests for some
     *                          reason this can be empty. Probably Spoon then picks the class path of this project. When
     *                          run from a jar the classpath is however required.
     * @param javaSourceVersion java source version / language level of the project
     * @param excludedFiles     list of glob expressions for files to exclude from validation
     */
    public ErrorMessageDeclarationCrawler(final Path projectDirectory, final List<Path> classPath,
            final int javaSourceVersion, final List<String> excludedFiles) {
        this.projectDirectory = projectDirectory;
        this.classPath = classPath;
        this.javaSourceVersion = javaSourceVersion;
        this.excludedFilesMatchers = excludedFiles.stream()
                .map(filePattern -> FileSystems.getDefault().getPathMatcher("glob:" + filePattern))
                .collect(Collectors.toList());
    }

    /**
     * Crawl error codes for a file / folder.
     * 
     * @param pathsToCrawl file(s) / folder(s) to crawl.
     * @return {@link Result} with found error codes and findings
     */
    public Result crawl(final List<Path> pathsToCrawl) {
        try {
            final CtModel model = buildModel(pathsToCrawl);
            final List<CtInvocation<?>> methodInvocations = model.getRootPackage()
                    .getElements(new TypeFilter<>(CtInvocation.class));
            final List<Finding> findings = new LinkedList<>();
            final List<ErrorMessageDeclaration> errorMessageDeclarations = new ArrayList<>();
            for (final CtInvocation<?> methodInvocation : methodInvocations) {
                crawl(methodInvocation, findings, errorMessageDeclarations);
            }
            return new Result(errorMessageDeclarations, findings);
        } catch (final AssertionError exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-ECM-15")
                    .message("The error code builder call had an unexpected syntax.")
                    .mitigation(
                            "Make sure that this version of the crawler is compatible with the version of your error-reporting library.")
                    .toString(), exception);
        }
    }

    private CtModel buildModel(final List<Path> pathsToCrawl) {
        final boolean projectUsesModules = moduleInfoFileExists(pathsToCrawl);
        final SpoonParser parser = SpoonParser.builder() //
                .javaSourceVersion(this.javaSourceVersion) //
                .classPath(projectUsesModules ? emptyList() : this.classPath) //
                .modulePath(projectUsesModules ? this.classPath : emptyList()) //
                .sourcePath(pathsToCrawl) //
                .build();
        return parser.buildModel();
    }

    private boolean moduleInfoFileExists(final List<Path> pathsToCrawl) {
        return pathsToCrawl.stream().anyMatch(this::containsModuleInfo);
    }

    private boolean containsModuleInfo(final Path path) {
        return Files.isDirectory(path) && Files.exists(path.resolve("module-info.java"));
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
        final var sourceFile = methodInvocation.getPosition().getFile();
        if (sourceFile == null || isFileExcluded(sourceFile)) {
            return;
        }
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
        if (methodsPackageName.equals(ERRORREPORTING_PACKAGE)
                && methodsClassName.equals(ErrorMessageBuilder.class.getSimpleName())
                && method.getSignature().equals("toString()")) {
            try {
                errorMessageDeclarations.add(readErrorCode(methodInvocation));
            } catch (final InvalidSyntaxException exception) {
                findings.add(exception.getFinding());
            }
        }
    }

    private boolean isFileExcluded(final File sourceFile) {
        final var relativePath = this.projectDirectory.relativize(sourceFile.toPath());
        return this.excludedFilesMatchers.stream().anyMatch(matcher -> matcher.matches(relativePath));
    }

    private String getMethodsPackageName(final CtInvocation<?> methodInvocation) {
        CtElement parent = methodInvocation.getParent();
        while (!(parent instanceof CtPackage)) {
            parent = parent.getParent();
        }
        final CtPackage methodsPackage = (CtPackage) parent;
        return methodsPackage.getQualifiedName();
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
     * @return crawled ErrorIdentifier
     * @throws InvalidSyntaxException in case the call has an invalid syntax
     */
    private ErrorMessageDeclaration readErrorCode(final CtInvocation<?> methodInvocation)
            throws InvalidSyntaxException {
        CtExpression<?> target = methodInvocation.getTarget();
        final var errorCodeBuilder = ErrorMessageDeclaration.builder();
        while (target instanceof CtInvocation) {
            final CtInvocation<?> builderCall = (CtInvocation<?>) target;
            addBuilderStep(builderCall, errorCodeBuilder);
            target = builderCall.getTarget();
        }
        final ErrorMessageDeclaration messageDeclaration = errorCodeBuilder
                .declaringPackage(getMethodsPackageName(methodInvocation))//
                .build();
        assertCallIsComplete(methodInvocation, messageDeclaration);
        return messageDeclaration;
    }

    private void assertCallIsComplete(final CtInvocation<?> methodInvocation,
            final ErrorMessageDeclaration messageDeclaration) throws InvalidSyntaxException {
        if (messageDeclaration.getIdentifier() == null) {
            throw new InvalidSyntaxException(ExaError.messageBuilder("E-ECM-31").message(
                    "Invalid incomplete builder call at {{position|uq}}.\nThis typically happens when you assign the ErrorMessageBuilder to a local variable and then call `toString()` on that variable. "
                            + "Doing so is not allowed since it makes it impossible to determine all components of the error declaration using static code analysis. (You could for example use if statements to select a message).",
                    PositionFormatter.formatPosition(methodInvocation.getPosition()))
                    .mitigation("Declare the error message in on fluent-programming call.").toString());
        }
    }

    /**
     * Read information from a call to a method of the {@link ErrorMessageBuilder} and add it to the passed
     * errorCodeBuilder.
     * 
     * @param builderCall      call to one of the methods of {@link ErrorMessageBuilder} or {@link ExaError}
     * @param errorCodeBuilder error code builder to add the error-code to.
     * @throws InvalidSyntaxException if the invocation is invalid
     */
    private void addBuilderStep(final CtInvocation<?> builderCall,
            final ErrorMessageDeclaration.Builder errorCodeBuilder) throws InvalidSyntaxException {
        final CtExecutableReference<?> executable = builderCall.getExecutable();
        final CtTypeReference<?> declaringType = executable.getDeclaringType();
        final String declaringTypeName = declaringType.getSimpleName();
        final String methodSignature = executable.getSignature();
        final Optional<MessageBuilderStepReader> reader = STEP_READERS.stream()
                .filter(eachReader -> eachReader.canRead(declaringTypeName, methodSignature)).findAny();
        if (reader.isPresent()) {
            reader.get().read(builderCall, errorCodeBuilder, this.projectDirectory);
        }
    }

    /**
     * Result of {@link ErrorMessageDeclarationCrawler#crawl(List)}
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
