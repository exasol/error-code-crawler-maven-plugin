# error-code-crawler-maven-plugin 1.1.1, released 2022-03-31

Code name: Fixed wrong src url when used with lombok

## Summary

In this release we fixed the bug that this plugin added references to npn existing source files when crawling projects that use Project Lombok.

Starting from this release this plugin will no longer report source positions for projects that use a non-default source path.

## Bug Fixes:

* #71: Fixed wrong src url when used with lombok

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:2.0.0` to `2.1.0`

### Test Dependency Updates

* Updated `org.jacoco:org.jacoco.agent:0.8.7` to `0.8.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.0.0` to `1.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.2.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.0` to `3.9.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:2.8` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M2` to `3.0.0-M1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3` to `3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.2` to `3.3.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.9.0` to `2.8.1`
* Added `org.projectlombok:lombok-maven-plugin:1.18.20.0`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0` to `3.1.0`
