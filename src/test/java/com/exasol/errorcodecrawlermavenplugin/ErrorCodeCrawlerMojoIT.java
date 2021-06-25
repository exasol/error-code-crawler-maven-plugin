package com.exasol.errorcodecrawlermavenplugin;

import static com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader.CONFIG_NAME;
import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.mavenpluginintegrationtesting.MavenIntegrationTestEnvironment;

/**
 * This integration test tests the maven plugin in a save environment. Since we don't want to install the plugin to the
 * user's maven repository, it creates a temporary maven home, and installs the plugin there. Then the test creates a
 * temporary project, runs the plugin on that project and checks the output.
 */
@Tag("integration")
//[itest->dsn~mvn-verify-goal~1]
class ErrorCodeCrawlerMojoIT {
    private static MavenIntegrationTestEnvironment testEnvironment;
    private static final String CURRENT_VERSION = getCurrentProjectVersion();
    private static final Path PLUGIN_JAR = Path.of("target",
            "error-code-crawler-maven-plugin-" + CURRENT_VERSION + ".jar");
    private static final Path ERROR_CODE_CRAWLER_POM = Path.of("pom.xml");
    private static final Path EXAMPLES_PATH = Path.of("src", "test", "java", "com", "exasol",
            "errorcodecrawlermavenplugin", "examples");

    @TempDir
    Path projectDir;
    private Path projectsSrc;
    private Path projectsTestSrc;

    @BeforeAll
    static void beforeAll() {
        testEnvironment = new MavenIntegrationTestEnvironment();
        testEnvironment.installPlugin(PLUGIN_JAR.toFile(), ERROR_CODE_CRAWLER_POM.toFile());
    }

    @BeforeEach
    void beforeEach() throws IOException {
        this.projectsSrc = this.projectDir
                .resolve(Path.of("src", "main", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        this.projectsTestSrc = this.projectDir
                .resolve(Path.of("src", "test", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        if (!(this.projectsSrc.toFile().mkdirs() && this.projectsTestSrc.toFile().mkdirs())) {
            throw new IllegalStateException("Failed to create test projects src folder.");
        }
        writePomWithCurrentVersionToTestProject();
        writeErrorCodeConfigToTestProject();
    }

    private void writeErrorCodeConfigToTestProject() throws IOException {
        Files.copy(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME)),
                this.projectDir.resolve(CONFIG_NAME), StandardCopyOption.REPLACE_EXISTING);
    }

    private void writePomWithCurrentVersionToTestProject() throws IOException {
        final String pomContent = new String(Objects
                .requireNonNull(getClass().getClassLoader().getResourceAsStream("testProject/pom.xml")).readAllBytes(),
                StandardCharsets.UTF_8);
        final String pomWithCurrentVersion = pomContent.replace("CURRENT_VERSION", CURRENT_VERSION);
        Files.writeString(this.projectDir.resolve("pom.xml"), pomWithCurrentVersion);
    }

    @Test
    void testValidCrawling() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        verifier.executeGoal("error-code-crawler:verify");
        verifier.verifyErrorFreeLog();
    }

    @Test
    void testErrorReport() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        verifier.executeGoal("error-code-crawler:verify");
        final String report = Files.readString(this.projectDir.resolve(Path.of("target", "error_code_report.json")));
        assertThat(report, equalTo(// [itest->dsn~report-writer~1]
                "{\"$schema\":\"https://schemas.exasol.com/error_code_report-0.2.0.json\",\"projectName\":\"project-to-test\",\"projectVersion\":\"1.0.0\",\"errorCodes\":[{\"identifier\":\"E-TEST-1\",\"message\":\"Test message\",\"messagePlaceholders\":[],\"sourceFile\":\"src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java\",\"sourceLine\":10,\"mitigations\":[]}]}"));
    }

    @Test
    void testCrawlingWithHigherJavaSourceVersion() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Java10.java"), this.projectsSrc.resolve("Java10.java"),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        verifier.executeGoal("error-code-crawler:verify");
        verifier.verifyErrorFreeLog();
    }

    @Test
    void testMissingErrorConfig() throws IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.delete(this.projectDir.resolve(CONFIG_NAME));
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString("E-ECM-9: Could not find " + CONFIG_NAME
                + " in the current project. Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin."));
    }

    @Test
    // [itest->dsn~error-identifier-belongs-to-package-validator~1]
    void testWrongPackageErrorConfig() throws IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.copy(
                Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("testProject/wrong_package_error_code_config.yml")),
                this.projectDir.resolve(CONFIG_NAME), StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString("[ERROR] E-ECM-13: According to this project's " + CONFIG_NAME
                + ", the error tag 'TEST' is not allowed for the package 'com.exasol.errorcodecrawlermavenplugin.examples'. The config allows the tag 'TEST' for the following packages: ['com.other']."));
    }

    @ParameterizedTest
    @CsvSource({ //
            "DuplicateErrorCode.java, E-ECM-4", // [itest->dsn~duplication-validator~1]
            "TestWithUndeclaredParameter.java, E-ECM-17", // [itest->dsn~parameters-validator~1]
            "IllegalUnnamedParameter.java, E-ECM-19", // [itest->dsn~empty-parameter-name-validator~1]
            "InvalidErrorCodeSyntax.java, E-ECMOJ-2",// [itest->dsn~identifier-validator~1]
    })
    // [itest->dsn~validator~1]
    void testValidations(final String testFile, final String expectedString) throws IOException {
        Files.copy(EXAMPLES_PATH.resolve(testFile), this.projectsTestSrc.resolve(testFile),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString(expectedString));
    }

    private Verifier getVerifier() {
        return testEnvironment.getVerifier(this.projectDir);
    }
}