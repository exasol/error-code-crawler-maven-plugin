# Design of the Error Code Crawler Maven Plugin

This file contains the design from the Error Code Crawler Maven Plugin (ECM). The design fulfills the requirements we described in [requirements.md](requirements.md).

## Architecture

`arch~components~1`

ECM consists of the following main components:

* A crawler: Components that reads extracts the error code declarations from Java code into a Java class structure
* A validator: Component that validates the declarations
* A config parser: Component that parses the `error_code_config.yml`
* A report writer (**missing**): Component that writes the error code report

Needs: dsn

## The Crawler

`dsn~error-declaration-crawler~1`

For implementing the error declaration crawler we decided to use [Spoon](https://spoon.gforge.inria.fr/). This library can create an Abstract Syntax Tree (AST) from Java source code. The error declaration crawler then goes over the AST and searches for invocations of the [error-reporting](https://github.com/exasol/error-reporting-java/) API.

In contrast to using regular expression matching, this approach has the following advantages:

* The crawler gets the source code in a unified form. Whitespace and line breaks can't cause errors
* The crawler can handle string concatenation with constants in error messages

One could want to support string concatenation with variables or crawl error declarations like:

```java
class Test {
    void test() {
        var builder = ExaError.messageBuilder("E-TEST-1");
        if (x) {
            builder.message("Message 1");
        } else {
            builder.message("Message 2");
        }
        throw new IllegalStateException(builder.toString());
    }
}
```

Supporting this is, however, not desirable since it make it impossible to determine the error message using static code analysis, since the error message will only be determined at runtime.

Instead, just rewrite your code to:

```java
class Test {
    void test() {
        if (x) {
            throw new IllegalStateException(ExaError.messageBuilder("E-TEST-1").message("Message 1").toString());
        } else {
            throw new IllegalStateException(ExaError.messageBuilder("E-TEST-2").message("Message 2").toString());
        }
    }
}
```

Covers:

* `arch~components~1`

Needs: impl, utest

## The Validator

`dsn~validator~1`

The validator consists of multiple sub-validators for the different aspects. The validator executes them.

Covers:

* `arch~components~1`

Needs: impl, itest

### Identifier Validator

`dsn~identifier-validator~2`

The identifier validator checks that the error identifiers have the correct format using a regular expression.

Covers:

* `req~verify-error-identifier-format~2`

Needs: impl, utest, itest

### Duplication Validator

`dsn~duplication-validator~1`

The duplication validator checks that each error identifier is unique.

The requirements only require that the whole identifier is unique. We decided however, to be more strict and also not allow identifiers that only differ by the number. So this implementation considers `E-TEST-1` and `F-TEST-1` as duplicate. This approach has two advantages:

* Developers can simply change the severity of the code without searching for a new number (For example change `E-TEST-1` to `F-TEST-1`)
* For generating the next free code developers only want to keep track of the highest used number. By that limitation they only need to keep track of the highest number per identifier and not per project-shorttag severity combination.

Covers:

* `req~verify-no-duplicate-error-codes~1`

Needs: impl, utest, itest

### Empty Parameter Name Validator

`dsn~empty-parameter-name-validator~1`

Covers:

* `verify-no-empty-parameter-names~1`

Needs: impl, utest, itest

### Parameters are Provided Validator

`dsn~parameters-validator~1`

Covers:

* `req~verify-parameters-are-provided~1`

Needs: impl, utest, itest

### Error Identifier Belongs to Package Validator

`dsn~error-identifier-belongs-to-package-validator~1`

Covers:

* `req~verify-error-codes-declared-in-correct-package~1`

Needs: impl, utest, itest

## Config Parser

`dsn~config-parser~1`

This component reads the `error_code_config.yml` and does some basic syntax validation.

Covers:

* `arch~components~1`

Needs: impl, utest

## Report Writer

`dsn~report-writer~1`

ECM uses the report writer from `error-code-model-java` to write the JSON report.

Needs: impl, itest

Covers:

* `feat~create-error-code-report~1`

## Implementation as Maven Plugin

`dsn~mvn-plugin~1`

We decided to implement ECM as a maven plugin. By that it can be executed from the Maven build and has direct access to the Maven Project.

An alternative would be to implement ECM as standalone app and then add a Maven-plugin that invokes it. However, since we currently only use Maven as build system for Java we did not add this layer of decoupling.

Covers:

* `feat~mvn-integration~1`

## Maven Verify Goal

`dsn~mvn-verify-goal~1`

ECM adds a Maven goal called `verify`. Using this goal, users can run the validation.

Covers:

* `feat~mvn-integration~1`

Needs: impl, itest

## Skip Property

`dsn~skip-execution~1`

Users can disable the execution of ECM by setting the java property `error-code-crawler.skip` to `true`.

Covers:

* `req~skip-execution~1`

Needs: impl, itest, utest


## Create Target Directory if it does not exist

`dsn~create-target-directory~1`

ECM error code report writer creates the project `target` directory if it does not exist (see `ProjectReportWriter`).

Covers:

* `feat~create-error-code-report~1`

Needs: impl, utest

### Default Source Directories

`dsn~src-directories-1`

By default, ECM crawls `src/main/java` and `src/main/test`.

Covers:

* `feat~src-directories`

Needs: impl, itest

### Src Directory Override

`dsn~src-directory-override`

Users can override the sources to crawl. ECM supports this by a `sourcePaths` property in the plugin configuration in the `pom.xml`.

Covers:

* `feat~src-directory-override`

Needs: impl, itest

### No Source Location in Report if Custom Source Path is Used

`dsn~no-src-location-in-report-for-custom-source-path~1`

ECM adds the source position of each error message declaration to the error-code-report. That allows the catalog to generate links for jumping to the source location.

For projects that specify a different source (typically code generated by the delombok plugin) that's not possible, since the code is just generated and not checked into the repository. For that reason we decided not to report the source location for projects that specify a `sourcePath`.

An alternate solution special for deloboked code would be to replace the prefix of the generated directory with the original source directory. However then the line number would not fit, since Lombok expands the annotations. That could again be solved by searching in the original source for the error tag and by that determining the line number. However, we decided against this solution since for now the source position is not important enough.

Needs: impl, utest


### Maven Plugin should be Thread-Safe

`dsn~mvn-plugin-thread-safe~1`

In order to support parallel execution of the maven plugin and remove WARNING messages during parallel execution, maven plugin should be marked as thread-safe.
The following checklist was used to make sure that plugin is thread-safe (see https://cwiki.apache.org/confluence/display/MAVEN/Parallel+builds+in+Maven+3 for more details):
* Checked that no static fields/variables in plugin/plugin code are subject to threading problems.
* Special attention was paid to find mutable static member variables of not thread-safe classes.
* No mutable static member variables of not thread-safe classes were found.
* Checked thread safety of any other third party libraries and made sure that they don't have mutable static member variables of not thread-safe classes.
* The plugin does not use singleton plexus components in `components.xml`.
* The plugin does not use any known tainted libraries.

Covers:

* `feat~mvn-plugin-parallel-execution~1`

Needs: impl
