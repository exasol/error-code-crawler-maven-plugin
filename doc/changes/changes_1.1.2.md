# error-code-crawler-maven-plugin 1.1.2, released 2022-??-??

Code name: 1.1.2: Upgrade dependencies

## Summary

## Features

* #74: Upgraded dependencies to fix [CVE-2020-36518](https://ossindex.sonatype.org/vulnerability/CVE-2020-36518) and [CVE-2021-26291](https://ossindex.sonatype.org/vulnerability/CVE-2021-26291)

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1` to `2.13.3`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1` to `2.13.3`
* Updated `fr.inria.gforge.spoon:spoon-core:10.0.0` to `10.1.1`

### Test Dependency Updates

* Updated `com.exasol:maven-plugin-integration-testing:1.1.1` to `1.1.2`
* Removed `junit:junit:4.13.2`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.9` to `3.10`
* Updated `org.apache.maven.shared:maven-verifier:1.7.2` to `1.8.0`
* Updated `org.jacoco:org.jacoco.agent:0.8.5` to `0.8.8`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.1` to `1.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.2.0` to `2.4.6`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.9.0` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.2.0` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.1` to `3.4.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.10.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.4.0` to `1.5.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8` to `1.6.13`
