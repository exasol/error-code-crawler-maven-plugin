package com.exasol.errorcodecrawlermavenplugin.crawler;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.builder.*;
import spoon.reflect.CtModel;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

class SpoonParser {

    private final Builder config;

    private SpoonParser(final Builder config) {
        this.config = config;
    }

    CtModel buildModel() {
        final Launcher spoon = configureSpoon();
        final JDTBuilder jdtBuilder = createJdtBuilder(spoon);
        spoon.getModelBuilder().build(jdtBuilder);
        return spoon.getModel();
    }

    private Launcher configureSpoon() {
        final Launcher spoon = new Launcher();
        final var environment = spoon.getEnvironment();
        environment.setNoClasspath(false);
        environment.setComplianceLevel(config.javaSourceVersion);
        for (final Path path : config.sourcePath) {
            spoon.addInputResource(path.toString());
        }
        return spoon;
    }

    private JDTBuilder createJdtBuilder(final Launcher spoon) {
        final Environment environment = spoon.getEnvironment();
        final ClasspathOptions<?> classpathOptions = new ClasspathOptions<>()
                .encoding(environment.getEncoding().displayName()).classpath(environment.getSourceClasspath());
        final ComplianceOptions<?> complianceOptions = new ComplianceOptions<>()
                .compliance(environment.getComplianceLevel());
        final AdvancedOptions<?> advancedOptions = new AdvancedOptions<>().preserveUnusedVars().continueExecution();
        final SourceOptions<?> sourceOptions = new SourceOptions<>()
                .sources(((JDTBasedSpoonCompiler) spoon.getModelBuilder()).getSource().getAllJavaFiles());
        return new ModuleAwareJDTBuilder(config.modulePath) //
                .classpathOptions(classpathOptions) //
                .complianceOptions(complianceOptions) //
                .advancedOptions(advancedOptions) //
                .sources(sourceOptions);
    }

    /**
     * This class adds the module path to the JDT command line options.
     */
    private static class ModuleAwareJDTBuilder extends JDTBuilderImpl {
        private final List<Path> modulePath;

        private ModuleAwareJDTBuilder(final List<Path> modulePath) {
            this.modulePath = modulePath;
        }

        @Override
        public String[] build() {
            final String[] originalArgs = super.build();
            final String[] args = Arrays.copyOf(originalArgs, originalArgs.length + 2);
            args[originalArgs.length] = "--module-path";
            args[originalArgs.length + 1] = getModulePathArg();
            return args;
        }

        private String getModulePathArg() {
            return modulePath.stream().map(Path::toString).collect(joining(File.pathSeparator));
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private int javaSourceVersion;
        private List<Path> sourcePath = emptyList();
        private List<Path> modulePath = emptyList();

        Builder javaSourceVersion(final int javaSourceVersion) {
            this.javaSourceVersion = javaSourceVersion;
            return this;
        }

        Builder sourcePath(final List<Path> sourcePath) {
            this.sourcePath = sourcePath;
            return this;
        }

        Builder modulePath(final List<Path> modulePath) {
            this.modulePath = modulePath;
            return this;
        }

        SpoonParser build() {
            return new SpoonParser(this);
        }
    }
}
