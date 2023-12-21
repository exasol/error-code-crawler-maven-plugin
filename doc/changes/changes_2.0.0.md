# Error Code Crawler Maven Plugin 2.0.0, released 2023-12-22

Code name: Fix CVE-2023-4218 and CVE-2023-4043 in dependencies

## Summary

This release fixes the following vulnerabilities in the following runtime dependencies:
* CVE-2023-4218 in `org.eclipse.jdt:org.eclipse.jdt.core`
* CVE-2023-4043 in `org.eclipse.parsson:parsson`

Please note that starting with this release, Error Code Crawler requires Java 17 to run.

## Security

* #95: Fix vulnerabilities in dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:2.1.2` to `2.1.3`
* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2` to `2.16.0`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2` to `2.16.0`
* Updated `fr.inria.gforge.spoon:spoon-core:10.4.1` to `10.4.2`
* Removed `org.apache.commons:commons-compress:1.24.0`

### Runtime Dependency Updates

* Added `org.eclipse.jdt:org.eclipse.jdt.core:3.36.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.9` to `1.7.36`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.2` to `3.15.4`
* Updated `org.junit.jupiter:junit-jupiter:5.10.0` to `5.10.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.1` to `2.0.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.12` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.3`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.5.0` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.9.0` to `3.10.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.3`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.0` to `2.16.2`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.10` to `0.8.11`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `3.10.0.2594`
