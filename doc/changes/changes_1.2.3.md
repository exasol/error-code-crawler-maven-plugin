# Error Code Crawler Maven Plugin 1.2.3, released 2023-04-13

Code name: Support Java 17

## Summary

This release supports crawling Java 17 source code by upgrading the parser library `fr.inria.gforge.spoon:spoon-core` to `10.3.0`.

## Features

* #51: Added support for Java 17

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1` to `2.14.2`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1` to `2.14.2`
* Updated `fr.inria.gforge.spoon:spoon-core:10.2.0` to `10.3.0`
* Removed `org.slf4j:slf4j-jdk14:2.0.6`

### Runtime Dependency Updates

* Added `org.slf4j:slf4j-jdk14:1.7.36`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.12.3` to `3.14.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.2.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.8.0` to `2.9.6`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.2.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.6.4` to `3.8.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.14.2`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.5.0` to `1.6.1`
