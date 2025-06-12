package com.exasol.errorcodecrawlermavenplugin;

import static com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader.CONFIG_NAME;
import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exsol.errorcodemodel.ErrorCodeReport;
import com.exsol.errorcodemodel.ErrorCodeReportReader;
import com.exsol.errorcodemodel.ErrorMessageDeclaration;

class ErrorCodeCrawlerMojoTest {

    private static final Path EXAMPLES_PATH = Path.of("src", "test", "java", "com", "exasol",
            "errorcodecrawlermavenplugin", "examples");

    private static final String CURRENT_VERSION = getCurrentProjectVersion();

    @TempDir
    Path projectDir;

    // [utest->dsn~skip-execution~1]
    @ParameterizedTest
    @CsvSource({ //
            "true, false", //
            "false, true",//
    })
    void testIsEnabled(final String propertyValue, final boolean expectedResult) {
        final ErrorCodeCrawlerMojo errorCodeCrawler = new ErrorCodeCrawlerMojo();
        errorCodeCrawler.skip = propertyValue;
        assertThat(errorCodeCrawler.isEnabled(), equalTo(expectedResult));
    }

    // [utest->dsn~skip-execution~1]
    @Test
    void testIsEnabledWithInvalidValue() {
        final ErrorCodeCrawlerMojo errorCodeCrawler = new ErrorCodeCrawlerMojo();
        errorCodeCrawler.skip = "illegal-value";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                errorCodeCrawler::isEnabled);
        assertThat(exception.getMessage(), equalTo(
                "E-ECM-51: Invalid value 'illegal-value' for property 'error-code-crawler.skip'. Please set the property to 'true' or 'false'."));
    }

    @Test
    void testSubProjectReport() throws IOException, MojoFailureException, ErrorCodeReportReader.ReadException {
        Path projectPath = projectDir.toFile().getCanonicalFile().toPath();
        Path subProjectPath = projectPath.resolve("sub-project");
        Path subProjectMainSrcJava = subProjectPath.resolve(Path.of("src", "main", "java"));
        Path subProjectMainSrcPackage = subProjectMainSrcJava
                .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        subProjectMainSrcPackage.toFile().mkdirs();
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), //
                subProjectMainSrcPackage.resolve("Test1.java"), //
                StandardCopyOption.REPLACE_EXISTING);
        final Path expectedPath = Path.of("sub-project/src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java");

        runSubProjectErrorCodeCrawlerMojo(projectPath, subProjectPath);

        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(subProjectPath.resolve("target/error_code_report.json"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(first.getSourceFile(), equalTo(expectedPath.toString())),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }

    @Test
    void testSimpleProjectPomReport() throws IOException, MojoFailureException, ErrorCodeReportReader.ReadException {
        Path projectPath = projectDir.toFile().getCanonicalFile().toPath();
        final Path expectedPath = Path.of("src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java");

        Path projectMainSrcJava = projectPath.resolve(Path.of("src", "main", "java"));
        Path projectMainSrcPackage = projectMainSrcJava
                .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        projectMainSrcPackage.toFile().mkdirs();
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), //
                projectMainSrcPackage.resolve("Test1.java"), //
                StandardCopyOption.REPLACE_EXISTING);

        runSimpleProjectErrorCodeCrawlerMojo(projectPath);

        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(projectPath.resolve("target/error_code_report.json"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(first.getSourceFile(), equalTo(expectedPath.toString())),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }

    @Test
    void testProjectWithParentPomReport() throws IOException, MojoFailureException, ErrorCodeReportReader.ReadException {
        Path projectPath = projectDir.toFile().getCanonicalFile().toPath();
        final Path expectedPath = Path.of("src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java");

        Path projectMainSrcJava = projectPath.resolve(Path.of("src", "main", "java"));
        Path projectMainSrcPackage = projectMainSrcJava
                .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        projectMainSrcPackage.toFile().mkdirs();
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), //
                projectMainSrcPackage.resolve("Test1.java"), //
                StandardCopyOption.REPLACE_EXISTING);

        runProjectWithParentPomErrorCodeCrawlerMojo(projectPath);

        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(projectPath.resolve("target/error_code_report.json"));
        final List<ErrorMessageDeclaration> errorCodes = result.getErrorMessageDeclarations();
        final ErrorMessageDeclaration first = errorCodes.get(0);
        assertAll(//
                () -> assertThat(errorCodes.size(), equalTo(1)),
                () -> assertThat(first.getIdentifier(), equalTo("E-TEST-1")),
                () -> assertThat(first.getSourceFile(), equalTo(expectedPath.toString())),
                () -> assertThat(first.getLine(), equalTo(10)), //
                () -> assertThat(first.getMessage(), equalTo("Test message"))//
        );
    }

    private void runSimpleProjectErrorCodeCrawlerMojo(Path projectPath) throws MojoFailureException, IOException {

        final ErrorCodeCrawlerMojo errorCodeCrawlerMojo = new ErrorCodeCrawlerMojo();
        final MavenProject project = new MavenProject();
        project.setFile(projectPath.resolve("pom.xml").toFile());
        project.setModel(TestMavenModel.create(CURRENT_VERSION));
        errorCodeCrawlerMojo.project = project;
        errorCodeCrawlerMojo.skip = "false";

        //if null, then executionRootDirectory is equal to projectPath
        errorCodeCrawlerMojo.executionRootDirectory = null;

        final InputStream configStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(configStream), //
                this.projectDir.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        errorCodeCrawlerMojo.execute();
    }

    private void runProjectWithParentPomErrorCodeCrawlerMojo(Path projectPath) throws MojoFailureException, IOException {
        final MavenProject parentProject = new MavenProject();
        Path parentProjectPath = this.projectDir.resolve("parent-pom");
        parentProject.setFile(parentProjectPath.resolve("pom.xml").toFile());

        final ErrorCodeCrawlerMojo errorCodeCrawlerMojo = new ErrorCodeCrawlerMojo();
        final MavenProject project = new MavenProject();
        project.setFile(projectPath.resolve("pom.xml").toFile());
        project.setParent(parentProject);
        project.setModel(TestMavenModel.create(CURRENT_VERSION));
        errorCodeCrawlerMojo.project = project;
        errorCodeCrawlerMojo.skip = "false";
        errorCodeCrawlerMojo.executionRootDirectory = projectPath.toString();

        final InputStream configStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(configStream), //
                this.projectDir.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        errorCodeCrawlerMojo.execute();
    }

    private void runSubProjectErrorCodeCrawlerMojo(Path projectPath, Path subProjectPath) throws MojoFailureException, IOException {
        final MavenProject project = new MavenProject();
        project.setFile(this.projectDir.resolve("pom.xml").toFile());
        final InputStream configStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(configStream), //
                this.projectDir.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        final ErrorCodeCrawlerMojo errorCodeCrawlerMojo = new ErrorCodeCrawlerMojo();
        final MavenProject subProject = new MavenProject();
        subProject.setFile(subProjectPath.resolve("pom.xml").toFile());
        subProject.setParent(project);
        subProject.setModel(TestMavenModel.create(CURRENT_VERSION));
        errorCodeCrawlerMojo.project = subProject;
        errorCodeCrawlerMojo.skip = "false";
        errorCodeCrawlerMojo.executionRootDirectory = projectPath.toString();

        final InputStream subProjectConfigStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(subProjectConfigStream), //
                subProjectPath.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        errorCodeCrawlerMojo.execute();
    }
}