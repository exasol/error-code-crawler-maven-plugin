package com.exasol.errorcodecrawlermavenplugin;

import static com.exasol.errorcodecrawlermavenplugin.config.ErrorCodeConfigReader.CONFIG_NAME;
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
        Path subProjectDir = projectDir.toFile().getCanonicalFile().toPath().resolve("sub-project");
        Path subProjectMainSrcJava = subProjectDir.resolve(Path.of("src", "main", "java"));
        Path subProjectMainSrcPackage = subProjectMainSrcJava
                .resolve(Path.of("com", "exasol", "errorcodecrawlermavenplugin", "examples"));
        subProjectMainSrcPackage.toFile().mkdirs();
        Files.copy(EXAMPLES_PATH.resolve("Test1.java"), //
                subProjectMainSrcPackage.resolve("Test1.java"), //
                StandardCopyOption.REPLACE_EXISTING);
        final Path expectedPath = Path.of("sub-project/src/main/java/com/exasol/errorcodecrawlermavenplugin/examples/Test1.java");

        runSubProjectErrorCodeCrawlerMojo(subProjectDir);

        final ErrorCodeReport result = new ErrorCodeReportReader()
                .readReport(subProjectDir.resolve("target/error_code_report.json"));
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

    private void runSubProjectErrorCodeCrawlerMojo(Path subProjectDir) throws MojoFailureException, IOException {
        final MavenProject project = new MavenProject();
        project.setFile(this.projectDir.resolve("pom.xml").toFile());
        final InputStream configStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(configStream), //
                this.projectDir.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        final ErrorCodeCrawlerMojo errorCodeCrawlerMojo = new ErrorCodeCrawlerMojo();
        final MavenProject subProject = new MavenProject();
        subProject.setFile(subProjectDir.resolve("pom.xml").toFile());
        subProject.setParent(project);
        errorCodeCrawlerMojo.project = subProject;
        errorCodeCrawlerMojo.skip = "false";

        final InputStream subProjectConfigStream = ErrorCodeCrawlerMojoIT.class.getClassLoader().getResourceAsStream("testProject/" + CONFIG_NAME);
        Files.copy(Objects.requireNonNull(subProjectConfigStream), //
                subProjectDir.resolve(CONFIG_NAME), //
                StandardCopyOption.REPLACE_EXISTING);

        errorCodeCrawlerMojo.execute();
    }
}