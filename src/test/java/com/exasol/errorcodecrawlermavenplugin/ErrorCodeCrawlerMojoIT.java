package com.exasol.errorcodecrawlermavenplugin;

import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * This integration test tests the maven plugin in a save environment. Since we don't want to install the plugin to the
 * user's maven repository, it creates a temporary maven home, and installs the plugin there. Then the test creates a
 * temporary project, runs the plugin on that project and checks the output.
 */
@Tag("integration")
class ErrorCodeCrawlerMojoIT {
    private static final String CURRENT_VERSION = getCurrentProjectVersion();
    private static final Path PLUGIN_JAR = Path.of("target",
            "error-code-crawler-maven-plugin-" + CURRENT_VERSION + ".jar");
    private static final Path ERROR_CODE_CRAWLER_POM = Path.of("pom.xml");
    private static final Path EXAMPLES_PATH = Path.of("src", "test", "java", "com", "exasol",
            "errorcodecrawlermavenplugin", "examples");

    @TempDir
    static Path mavenRepo;

    /**
     * TempDir only supports one temp directory per test class. For that we can not use it here again but create and
     * drop it by hand.
     */
    private Path projectDir;
    private Path projectsSrc;
    /**
     * When you enable debugging here, connect with a debugger to localhost:8000 during the test run. Since zhe test
     * wait for the debugger, this should be disabled on commits so that CI runs through.
     */
    private static final boolean DEBUG = false;
    private Path projectsTestSrc;

    @BeforeAll
    static void beforeAll() throws VerificationException, IOException {
        final File testDir = ResourceExtractor.simpleExtractResources(ErrorCodeCrawlerMojoIT.class, "/testProject");
        writeCurrentPluginVersionToPom(testDir);
        final Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.setCliOptions(List.of(//
                "-Dfile=" + PLUGIN_JAR.toAbsolutePath().toString(), //
                "-DlocalRepositoryPath=" + mavenRepo.toAbsolutePath(), //
                "-DpomFile=" + ERROR_CODE_CRAWLER_POM.toAbsolutePath()));
        verifier.executeGoal("install:install-file");
        verifier.verifyErrorFreeLog();
    }

    private static void writeCurrentPluginVersionToPom(final File testDir) throws IOException {
        final Path pom = testDir.toPath().resolve("pom.xml");
        final String pomContent = Files.readString(pom);
        Files.writeString(pom, pomContent.replace("CURRENT_VERSION", CURRENT_VERSION),
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        this.projectDir = Files.createTempDirectory("testProject");
        this.projectsSrc = this.projectDir
                .resolve(Path.of("src", "main", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        this.projectsTestSrc = this.projectDir
                .resolve(Path.of("src", "test", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        if (!(this.projectsSrc.toFile().mkdirs() && this.projectsTestSrc.toFile().mkdirs())) {
            throw new IllegalStateException("Failed to create test projects src folder.");
        }
        Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("testProject/pom.xml")),
                this.projectDir.resolve("pom.xml"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("testProject/error_code_config.yml")),
                this.projectDir.resolve("error_code_config.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.deleteDirectory(this.projectDir.toFile());
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
    void testCrawlingWithHigherJavaSourceVersion() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Java10.java"), this.projectsSrc.resolve("Java10.java"),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        verifier.executeGoal("error-code-crawler:verify");
        verifier.verifyErrorFreeLog();
    }

    @Test
    void testMissingErrorConfig() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.delete(this.projectDir.resolve("error_code_config.yml"));
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString(
                "E-ECM-9: Could not find error_code_config.yml in the current project. Please create the file. You can find a reference at: https://github.com/exasol/error-code-crawler-maven-plugin."));
    }

    @Test
    void testWrongPackageErrorConfig() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), this.projectsSrc.resolve("Test1.java"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.copy(
                Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("testProject/wrongPackageerror_code_config.yml")),
                this.projectDir.resolve("error_code_config.yml"), StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString(
                "[ERROR] E-ECM-13: According to this project's error_code_config.yml, the error tag 'TEST' is not allowed for the package 'com.exasol.errorcodecrawlermavenplugin.examples'. The config allows the tag 'TEST' for the following packages: ['com.other']."));
    }

    @ParameterizedTest
    @CsvSource({ //
            "DuplicateErrorCode.java, E-ECM-4", //
            "TestWithUndeclaredParameter.java, E-ECM-17", //
            "IllegalUnnamedParameter.java, E-ECM-19",//
    })
    void testValidations(final String testFile, final String expectedString) throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve(testFile), this.projectsTestSrc.resolve(testFile),
                StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString(expectedString));
    }

    private Verifier getVerifier() throws VerificationException {
        final Verifier verifier = new Verifier(this.projectDir.toFile().getAbsolutePath());
        verifier.setLocalRepo(mavenRepo.toAbsolutePath().toString());
        if (DEBUG) {
            verifier.setDebug(true);
            verifier.setDebugJvm(true);
        }
        return verifier;
    }
}