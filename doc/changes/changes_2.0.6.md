# Error Code Crawler Maven Plugin 2.0.6, released 2026-02-09

Code name: Fix Java 21 switch pattern matching

## Summary

This release fixes an issue when using switch with pattern matching and sealed classes on Java 21 where the plugin
would fail because of a missing `default` branch.

## Bugfixes

* #118: Error crawler fails with switch and sealed enums

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:2.1.3` to `2.1.4`
* Updated `com.exasol:error-reporting-java:1.0.1` to `1.0.2`
* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0` to `2.21.0`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0` to `2.21.0`
* Updated `fr.inria.gforge.spoon:spoon-core:11.2.0` to `11.3.0`

### Runtime Dependency Updates

* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `1.7.36`

### Test Dependency Updates

* Updated `com.exasol:maven-plugin-integration-testing:1.1.2` to `1.1.4`
* Updated `com.exasol:maven-project-version-getter:1.2.0` to `1.2.2`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.7` to `4.3.1`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.junit.jupiter:junit-jupiter:5.10.2` to `6.0.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.5` to `2.0.6`
* Updated `com.exasol:project-keeper-maven-plugin:5.2.3` to `5.4.5`
* Updated `com.exasol:quality-summarizer-maven-plugin:0.2.0` to `0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1` to `9.0.2`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.0` to `3.14.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.3` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.7` to `3.2.8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.2` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.3` to `3.5.4`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.18.0` to `2.20.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.13` to `0.8.14`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751` to `5.5.0.6356`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.7.0` to `0.9.0`
