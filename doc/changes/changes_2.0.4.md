# Error Code Crawler Maven Plugin 2.0.4, released 2025-06-11

Code name: Support Java 21

## Summary

This release supports crawling Java 21 source code by upgrading the parser library `fr.inria.gforge.spoon:spoon-core` to `11.2.1-beta-20`.

## Features

* #113: Added support for Java 21

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1` to `2.19.0`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1` to `2.19.0`
* Updated `fr.inria.gforge.spoon:spoon-core:10.4.2` to `11.2.1-beta-20`

### Runtime Dependency Updates

* Updated `org.eclipse.jdt:org.eclipse.jdt.core:3.36.0` to `3.41.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.3` to `2.0.4`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.0` to `5.2.1`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.2.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.13.0` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.1` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.2` to `3.2.7`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.1` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.3` to `3.11.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.18.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922` to `5.1.0.4751`
* Added `org.sonatype.central:central-publishing-maven-plugin:0.7.0`
* Removed `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.13`
