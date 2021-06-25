# Requirements for the Error Code Crawler Maven Plugin

The Error Code Crawler Maven Plugin (ECM) is a tool that analyzes invocations of the [Exasol Error Code Builder](https://github.com/exasol/error-reporting-java/) from Java source code and writes them into a json report.

## Terminology

* `Error Message Declaration`: A Java statement that builds an error message and typically throws an exception:
  Example:
  ```
  ExaError.messageBuilder("E-TEST-1").message("Failed to start database.").toString();
  ```
* `Error Identifier`: Each error code has a unique identifier. Example: `E-Test-1`. Also known as `Error Code`. An Error Identifier consists of:
    * `Severity`: The first letter defines the servity (`W`: Warning `E`: Error, `F`: Fatal)
    * `Project-Shorttag`: Identifier of the project / module. Example: `Test`
    * `Error-Number`: Number of the error. The number is used to make the codes unique.

## Features

### Verify Error Code Declarations

`feat~verify-error-code-declarations~1`

ECM checks that the error code declarations are valid.

Needs: req

#### Verify Error Identifier

`req~verify-error-identifier-format~1`

ECM checks that the error identifier matches the following [ABNF](https://en.wikipedia.org/wiki/Augmented_Backus%E2%80%93Naur_form):

```abnf
error-identifier = severity "-" project-short-tag [ "-" module-short-tag ] "-" error-number
 
severity = ( "F" / "E" / "W" )
 
project-short-tag = ALPHA 1*4ALPHANUM
 
module-short-tag = ALPHA 1*4ALPHANUM
 
error-number = 1*5ALPHANUM
```

Covers:

* `feat~verify-error-code-declarations~1`

Needs: dsn

#### Verify no Error Code is Declared Twice

`req~verify-no-duplicate-error-codes~1`

ECM makes sure that each error tag is only declared once in the code.

Rationale:

One use case of the error codes is to find the place in code where the error was thrown. For that reason it's important that the same error can only be thrown by a single statement.

Covers:

* `feat~verify-error-code-declarations~1`

Needs: dsn

#### Verify Parameters Names are not Empty

`verify-no-empty-parameter-names~1`

ECM makes sure that there are no error code declarations with empty parameter names.

Invalid Example:

```java
ExaError.messageBuilder("E-TEST-1").message("Unknown parameter: {{}}",value).toString();
```

Valid Example:

```java
ExaError.messageBuilder("E-TEST-1").message("Unknown parameter: {{parameter name}}",value).toString();
```

Rationale:

The online error catalog uses the parameters to display placeholders. For that reason each parameter must have a name.

Covers:

* `feat~verify-error-code-declarations~1`

Needs: dsn

#### Verify Parameters are Provided

`req~verify-parameters-are-provided~1`

ECM makes sure that the error message declaration provides a parameter value for each placeholder it declares.

Invalid Example:

```java
ExaError.messageBuilder("E-TEST-1").message("Unknown parameter: {{parameter name}}").toString();
```

Valid Example:

```java
ExaError.messageBuilder("E-TEST-1").message("Unknown parameter: {{parameter name}}",value).toString();
```

Covers:

* `feat~verify-error-code-declarations~1`

Needs: dsn

#### Verify Error Codes Declared in Correct Package

`req~verify-error-codes-declared-in-correct-package~1`

Each project must define a configuration file (`error_code_config.yml`) that declares error tags with corresponding packages.

Example:

```yml
error-tags:
  ECM:
    packages:
      - com.exasol.errorcodecrawlermavenplugin
```

ECM validates that an error tag is not declared in other packages.

Rationale:

Error tags should be scoped to packages or modules of the software. If a tag is declared somewhere else, it's probably a mistake.

Covers:

* `feat~verify-error-code-declarations~1`

Needs: dsn

### Create Error Code Report

`feat~create-error-code-report~1`

ECM can write an error code report in the format specified by the [error code report schema](https://github.com/exasol/schemas/blob/main/error_code_report-0.1.0.json).

Rationale:

All error-declarations of exasol open-source projects should be listed in a central [error-catalog](https://github.com/exasol/error-catalog). The generated file is the exchange format. [Release-droid](https://github.com/release-droid/) will add the generated files to the artifacts for each release on GitHub and from there the [error-catalog](https://github.com/exasol/error-catalog) will collect them.

Needs: dsn

### Mvn Integration

`feat~mvn-integration~1`

ECM is integrated into the maven build.

Rationale:

We use maven in all our Java projects. By adding it to the build we make sure that the validations run on local testing and during CI.

Needs: dsn


