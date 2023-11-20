# Error Code Crawler Maven Plugin 1.3.2, released 2023-11-20

Code name: Fix CVE-2023-4218 and CVE-2023-4043 in dependencies

## Summary

This release fixes the following vulnerabilities in the following runtime dependencies:
* CVE-2023-4218 in `org.eclipse.jdt:org.eclipse.jdt.core`
* CVE-2023-4043 in `org.eclipse.parsson:parsson`

## Security

* #95: Fix vulnerabilities in dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2` to `2.16.0`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2` to `2.16.0`
* Updated `fr.inria.gforge.spoon:spoon-core:10.4.1` to `10.4.2`
* Removed `org.apache.commons:commons-compress:1.24.0`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.2` to `3.15.3`
* Updated `org.junit.jupiter:junit-jupiter:5.10.0` to `5.10.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.1` to `1.3.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.12` to `2.9.16`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.5.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.0` to `2.16.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.10` to `0.8.11`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `3.10.0.2594`
