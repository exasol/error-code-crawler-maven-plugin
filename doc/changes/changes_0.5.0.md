# error-code-crawler-maven-plugin 0.5.0, released 2021-06-25

Code name: Writing error-code-report

## Summary

Starting from this release ECM writes an `error_code_report.json` to `target/`. This report contains information about the error message declarations of the project.

## Features:

* #26: Writing error-code-report

## Refactoring

* #43: Refactored integration test to use maven-plugin-integration-testing
* #49: Moved model to error-code-model-java

## Documentation

* #46: Add requirements and design

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:error-code-model-java:0.1.0`

### Test Dependency Updates

* Added `com.exasol:maven-plugin-integration-testing:0.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.4.0` to `0.5.0`
* Added `org.itsallcode:openfasttrace-maven-plugin:1.2.0`