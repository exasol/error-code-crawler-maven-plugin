# Error Code Crawler Maven Plugin 2.1.0, released 2026-06-22

Code name: Read new compiler plugin version configuration

## Summary

This release reads the Java version from the new `<release>` configuration of the Maven Java compiler plugin. If this is missing, the plugin falls back to the old `<source>` configuration.

## Features

* #122: Read Java version from `<release>` configuration of compiler plugin

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.2` to `2.22.0`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.2` to `2.22.0`
* Updated `fr.inria.gforge.spoon:spoon-core:11.3.0` to `11.4.0`

### Test Dependency Updates

* Updated `com.exasol:maven-plugin-integration-testing:1.1.4` to `1.1.5`
* Updated `nl.jqno.equalsverifier:equalsverifier:4.4.1` to `4.5`
* Added `org.jacoco:org.jacoco.agent:0.8.14`
* Updated `org.junit.jupiter:junit-jupiter:6.0.3` to `6.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.7` to `2.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:5.4.6` to `5.6.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.2` to `10.0.0`
* Added `org.apache.maven.plugins:maven-dependency-plugin:3.10.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.4` to `3.5.5`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.4.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.4` to `3.5.5`
* Updated `org.itsallcode:openfasttrace-maven-plugin:2.3.0` to `2.3.1`
