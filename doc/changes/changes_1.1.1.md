# error-code-crawler-maven-plugin 1.1.1, released 2022-04-04

Code name: Fixed wrong src url when used with lombok

## Summary

In this release we fixed the bug that this plugin added references to npn existing source files when crawling projects that use Project Lombok.

Starting from this release this plugin will no longer report source positions for projects that use a non-default source path.

## Bug Fixes:

* #71: Fixed wrong src url when used with lombok

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:2.0.1` to `2.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.0` to `1.1.1`
