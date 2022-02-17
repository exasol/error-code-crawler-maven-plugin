# error-code-crawler-maven-plugin 0.8.0, released 2022-02-17

Code name: Enforce stricter error code format

## Summary

**This is a breaking change:** Before, error codes with more than one module name (e.g. `E-EXA-MOD1-MOD2-42`) where allowed. To unify error codes we limit this now to at most one module name (e.g. `E-EXA-MOD1-42`) as specified in the [spec](https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/doc/requirements.md#verify-error-identifier). Tags can now have at most 10 characters and the severity (`F`, `W`, `E`) is now optional, defaulting to `E`. This allows using error codes like `SQL-1234`. See the ABNF grammar in the [requirement specification](https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/doc/requirements.md#verify-error-identifier).

This release also checks if the value of `highest-index` specified in `error_code_config.yml` is correct.

## Features

* #27: Added stricter code format validation
* #30: Added validation for the highest error code index
* #39: Mentioned location of offending short tag in validation error definitions

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:1.0.0` to `2.0.0`
* Updated `com.exasol:error-reporting-java:0.4.0` to `0.4.1`
* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0` to `2.13.1`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0` to `2.13.1`
* Updated `fr.inria.gforge.spoon:spoon-core:9.1.0` to `10.0.0`
* Removed `org.apache.maven:maven-artifact:3.8.3`
* Removed `org.apache.maven:maven-core:3.8.3`
* Removed `org.apache.maven:maven-plugin-api:3.8.3`
* Removed `org.apache.maven:maven-project:2.2.1`
* Updated `org.slf4j:slf4j-jdk14:1.7.32` to `1.7.36`

### Test Dependency Updates

* Updated `com.exasol:maven-plugin-integration-testing:1.0.0` to `1.1.0`
* Updated `com.exasol:maven-project-version-getter:1.0.0` to `1.1.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.7.1` to `3.9`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.1` to `5.8.2`
* Updated `org.junit.jupiter:junit-jupiter:5.8.1` to `5.8.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.1` to `1.0.0`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.0` to `1.3.4`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.15`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:2.7` to `3.0.0-M2`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.1` to `3.3.2`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.6.1` to `3.6.4`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.9.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.2.0` to `1.4.0`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
