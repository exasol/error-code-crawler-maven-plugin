package com.exasol.errorcodecrawlermavenplugin;

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

class ExasolErrorCrawlerIT {
    private static final Path PLUGIN_JAR = Path.of("target", "error-code-crawler-maven-plugin-0.1.0.jar");
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

    @BeforeAll
    static void beforeAll() throws VerificationException, IOException {
        final File testDir = ResourceExtractor.simpleExtractResources(ExasolErrorCrawlerIT.class, "/testProject");
        final Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.setCliOptions(List.of(//
                "-Dfile=" + PLUGIN_JAR.toAbsolutePath().toString(), //
                "-DlocalRepositoryPath=" + mavenRepo.toAbsolutePath(), //
                "-DpomFile=" + ERROR_CODE_CRAWLER_POM.toAbsolutePath()));
        verifier.executeGoal("install:install-file");
        verifier.verifyErrorFreeLog();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        this.projectDir = Files.createTempDirectory("mavenRepo");
        this.projectsSrc = this.projectDir
                .resolve(Path.of("src", "main", "java", "com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        if (!this.projectsSrc.toFile().mkdirs()) {
            throw new IllegalStateException("Failed to create test projects src folder.");
        }
        Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("testProject/pom.xml")),
                this.projectDir.resolve("pom.xml"), StandardCopyOption.REPLACE_EXISTING);
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
    void testDuplicateErrorCode() throws VerificationException, IOException {
        Files.copy(EXAMPLES_PATH.resolve("DuplicateErrorCode.java"),
                this.projectsSrc.resolve("DuplicateErrorCode.java"), StandardCopyOption.REPLACE_EXISTING);
        final Verifier verifier = getVerifier();
        final VerificationException exception = assertThrows(VerificationException.class,
                () -> verifier.executeGoal("error-code-crawler:verify"));
        assertThat(exception.getMessage(), containsString(
                "[ERROR] E-ECM-4: Found duplicate error code: 'E-TEST-1' was declared multiple times: DuplicateErrorCode.java:8, DuplicateErrorCode.java:12."));
    }

    private Verifier getVerifier() throws VerificationException {
        final Verifier verifier = new Verifier(this.projectDir.toFile().getAbsolutePath());
        verifier.setLocalRepo(mavenRepo.toAbsolutePath().toString());
        // verifier.setDebug(true);
        // verifier.setDebugJvm(true);
        return verifier;
    }
}