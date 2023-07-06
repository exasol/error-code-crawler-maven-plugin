package com.exasol.errorcodecrawlermavenplugin;

import static com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader.CONFIG_NAME;
import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.mavenpluginintegrationtesting.MavenIntegrationTestEnvironment;
import com.exsol.errorcodemodel.*;

/**
 * This integration test tests the maven plugin in a save environment. Since we don't want to install the plugin to the
 * user's maven repository, it creates a temporary maven home, and installs the plugin there. Then the test creates a
 * temporary project, runs the plugin on that project and checks the output.
 */
@Tag("integration")
// [itest->dsn~mvn-verify-goal~1]
class ErrorCodeCrawlerMojoIT {
    private static MavenIntegrationTestEnvironment testEnvironment;
    private static final String CURRENT_VERSION = getCurrentProjectVersion();
    private static final Path PLUGIN_JAR = Path.of("target",
            "error-code-crawler-maven-plugin-" + CURRENT_VERSION + ".jar");
    private static final Path ERROR_CODE_CRAWLER_POM = Path.of(".flattened-pom.xml");
    private static final Path EXAMPLES_PATH = Path.of("src", "test", "java", "com", "exasol",
            "errorcodecrawlermavenplugin", "examples");

    @TempDir
    Path projectDir;

    @BeforeAll
    static void beforeAll() {
        testEnvironment = new MavenIntegrationTestEnvironment();
        testEnvironment.installPlugin(PLUGIN_JAR.toFile(), ERROR_CODE_CRAWLER_POM.toFile());
    }

    @Test
    // [itest->dsn~src-directories]
    void testValidCrawling() throws VerificationException, IOException {
        getVerifier().withDefaultPom().withJavaFile("Test1.java").verify().assertNoErrors();
    }

    @Test
    void testErrorReport() throws VerificationException, IOException {
        final String path = Path.of("src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java")
                .toString().replace("\\", "\\\\");
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("Test1.java") //
                .verify() //
                .assertReport(equalTo(
                        // [itest->dsn~report-writer~1]
                        "{\"$schema\":\"https://schemas.exasol.com/error_code_report-1.0.0.json\"," //
                                + "\"projectName\":\"project-to-test\",\"projectVersion\":\"1.0.0\"," //
                                + "\"errorCodes\":[{\"identifier\":\"E-TEST-1\",\"message\":\"Test message\"," //
                                + "\"messagePlaceholders\":[],\"sourceFile\":" //
                                + "\"" + path + "\"," //
                                + "\"sourceLine\":10,\"mitigations\":[]}]}"));
    }

    @Test
    void testCrawlingWithHigherJavaSourceVersion() throws VerificationException, IOException {
        getVerifier().withDefaultPom().withJavaFile("Java10.java").verify().assertNoErrors();
    }

    @Test
    void testCrawlingIgnoresTestSources() throws VerificationException, IOException {
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("Java10.java") //
                .withTestFile("DuplicateErrorCode.java") //
                .verify() //
                .assertNoErrors();
    }

    @Test
    void testCrawlingWithTestsAndModuleInfo() throws VerificationException, IOException {
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("Java10.java") //
                .withTestFile("Test1.java") //
                .withEmptyModulesFile() //
                .verify() //
                .assertNoErrors();
    }

    @Test
    void testCrawlingWithModuleInfo() throws VerificationException, IOException {
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("Java10.java") //
                .withDefaultModuleInfoFile() //
                .verify() //
                .assertNoErrors();
    }

    @Test
    void testMissingErrorConfig() throws Exception {
        getVerifier().withDefaultPom() //
                .withJavaFile("Test1.java") //
                .withoutConfiguration() //
                .withDefaultModuleInfoFile() //
                .verifyException(containsString("E-ECM-9: Could not find " + CONFIG_NAME
                        + " in the current project. Please create the file."
                        + " You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin."));
    }

    @Test
    // [itest->dsn~error-identifier-belongs-to-package-validator~1]
    void testWrongPackageErrorConfig() throws IOException {
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("Test1.java") //
                .withConfiguration("testProject/wrong_package_error_code_config.yml") //
                .verifyException(containsString("[ERROR] E-ECM-13: According to this project's " + CONFIG_NAME
                        + ", the error tag 'TEST' is not allowed for the package 'com.exasol.errorcodecrawlermavenplugin.examples'."
                        + " The config allows the tag 'TEST' for the following packages: ['com.other']."));
    }

    @ParameterizedTest
    @CsvSource({ //
            "DuplicateErrorCode.java, E-ECM-4", // [itest->dsn~duplication-validator~1]
            "TestWithUndeclaredParameter.java, E-ECM-17", // [itest->dsn~parameters-validator~1]
            "IllegalUnnamedParameter.java, E-ECM-19", // [itest->dsn~empty-parameter-name-validator~1]
            "InvalidErrorCodeSyntax.java, E-ECMOJ-2", // [itest->dsn~identifier-validator~2]
    })
    // [itest->dsn~validator~1]
    void testValidationFindings(final String testFile, final String expectedString) throws IOException {
        getVerifier() //
                .withDefaultPom()//
                .withJavaFile(testFile) //
                .verifyException(containsString(expectedString));
    }

    // [itest->dsn~skip-execution~1]
    @Test
    void testSkip() throws IOException {
        getVerifier() //
                .withDefaultPom() //
                .withJavaFile("DuplicateErrorCode.java") //
                .withSystemProperty("error-code-crawler.skip", "true") //
                .verifyNoException();
    }

    @Test
    void testSkipWithConfiguration() throws IOException {
        getVerifier() //
                .withPom(mavenModel(CURRENT_VERSION, null, "true")) //
                .withJavaFile("DuplicateErrorCode.java") //
                .verifyNoException();
    }

    @Test
    // [impl->dsn~src-directory-override]
    // [utest->dsn~no-src-location-in-report-for-custom-source-path~1]
    void testDifferentSourcePath() throws IOException, VerificationException, ErrorCodeReportReader.ReadException {
        final String alternateSrcPath = "generated-sources/";
        final TestMavenModel model = mavenModel(CURRENT_VERSION, List.of(alternateSrcPath + "main/java"), null);
        final ITVerifier verifier = getVerifier() //
                .withPom(model) //
                .withJavaFile("Test1.java") //
                .moveSourcesTo(alternateSrcPath) //
                .verify() //
                .assertReport(containsString("E-TEST-1"));

        final ErrorCodeReport report = new ErrorCodeReportReader().readReport(verifier.getErrorCodeReportPath());
        final ErrorMessageDeclaration firstDeclaration = report.getErrorMessageDeclarations().get(0);
        assertAll(//
                () -> assertThat(firstDeclaration.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(firstDeclaration.getSourceFile(), equalTo("")),
                () -> assertThat(firstDeclaration.getLine(), equalTo(-1))//
        );
    }

    private ITVerifier getVerifier() throws IOException {
        return new ITVerifier(ErrorCodeCrawlerMojoIT.testEnvironment, this.projectDir);
    }

    static TestMavenModel mavenModel(final String version, final List<String> sourcePaths, final String skip) {
        return new TestMavenModel(new ErrorCodeCrawlerPluginDefinition(version, sourcePaths, skip));
    }

    static class ITVerifier {
        private final MavenIntegrationTestEnvironment testEnvironment;
        private final Path projectDir;
        private final Path projectMainSrcJava;
        private final Path projectMainSrcPackage;
        private final Path projectTestSrcPackage;
        private final Properties properties = new Properties();
        private Verifier mavenVerifier = null;

        ITVerifier(final MavenIntegrationTestEnvironment testEnvironment, final Path projectDir) throws IOException {
            this.testEnvironment = testEnvironment;
            this.projectDir = projectDir;
            this.projectMainSrcJava = this.projectDir.resolve(Path.of("src", "main", "java"));
            this.projectMainSrcPackage = this.projectMainSrcJava
                    .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
            this.projectTestSrcPackage = this.projectDir.resolve(
                    Path.of("src", "test", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
            if (!(this.projectMainSrcPackage.toFile().mkdirs() && this.projectTestSrcPackage.toFile().mkdirs())) {
                throw new IllegalStateException("Failed to create test projects src folder.");
            }
            withConfiguration("testProject/" + CONFIG_NAME);
        }

        ITVerifier withConfiguration(final String resourcePath) throws IOException {
            final InputStream stream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream(resourcePath);
            Files.copy(Objects.requireNonNull(stream), //
                    this.projectDir.resolve(CONFIG_NAME), //
                    StandardCopyOption.REPLACE_EXISTING);
            return this;
        }

        ITVerifier verifyException(final Matcher<String> matcher) {
            final VerificationException exception = assertThrows(VerificationException.class, () -> verify());
            assertThat(exception.getMessage(), matcher);
            return this;
        }

        ITVerifier moveSourcesTo(final String target) throws IOException {
            FileUtils.moveDirectory(this.projectDir.resolve("src").toFile(), //
                    this.projectDir.resolve(target).toFile());
            return this;
        }

        ITVerifier verifyNoException() {
            assertDoesNotThrow(() -> verify());
            return this;
        }

        ITVerifier withSystemProperty(final String key, final String value) {
            this.properties.put(key, value);
            return this;
        }

        ITVerifier withoutConfiguration() throws IOException {
            Files.delete(this.projectDir.resolve(CONFIG_NAME));
            return this;
        }

        ITVerifier withEmptyModulesFile() throws IOException {
            return withModulesFile("");
        }

        ITVerifier withDefaultPom() throws IOException {
            return withPom(mavenModel(CURRENT_VERSION, null, null));
        }

        ITVerifier withPom(final TestMavenModel model) throws IOException {
            model.writeAsPomToProject(this.projectDir);
            return this;
        }

        ITVerifier withJavaFile(final String name) throws IOException {
            return withFile(name, this.projectMainSrcPackage);
        }

        ITVerifier withTestFile(final String name) throws IOException {
            return withFile(name, this.projectTestSrcPackage);
        }

        ITVerifier withFile(final String name, final Path packagePath) throws IOException {
            Files.copy(EXAMPLES_PATH.resolve(name), //
                    packagePath.resolve(name), //
                    StandardCopyOption.REPLACE_EXISTING);
            return this;
        }

        ITVerifier withModulesFile(final String content) throws IOException {
            Files.writeString(this.projectMainSrcJava.resolve("module-info.java"),
                    "module dummy.module {" + content + "}");
            return this;
        }

        ITVerifier withDefaultModuleInfoFile() throws IOException {
            return withModulesFile("requires error.reporting.java;");
        }

        ITVerifier verify() throws VerificationException {
            this.mavenVerifier = this.testEnvironment.getVerifier(this.projectDir);
            this.mavenVerifier.setSystemProperties(this.properties);
            this.mavenVerifier.executeGoal("error-code-crawler:verify");
            return this;
        }

        ITVerifier assertNoErrors() throws VerificationException {
            this.mavenVerifier.verifyErrorFreeLog();
            return this;
        }

        Path getErrorCodeReportPath() {
            return this.projectDir.resolve(Path.of("target", "error_code_report.json"));
        }

        ITVerifier assertReport(final Matcher<String> matcher) throws IOException, VerificationException {
            final String report = Files.readString(getErrorCodeReportPath());
            assertThat(report, matcher);
            return this;
        }
    }
}