# Error Code Crawler Maven Plugin 2.0.1, released 2024-02-29

Code name: Fix CVE-2024-25710 and CVE-2024-26308 in `org.apache.commons:commons-compress`

## Summary

This release fixes CVE-2024-25710 and CVE-2024-26308 in compile dependency `org.apache.commons:commons-compress`.

## Security

* #101: Fixed CVE-2024-26308 in compile dependency `org.apache.commons:commons-compress`
* #102: Fixed CVE-2024-25710 in compile dependency `org.apache.commons:commons-compress`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.0` to `2.16.1`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0` to `2.16.1`

### Runtime Dependency Updates

* Updated `org.slf4j:slf4j-jdk14:1.7.36` to `2.0.12`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.4` to `3.15.7`
* Updated `org.junit.jupiter:junit-jupiter:5.10.1` to `5.10.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.0` to `2.0.1`
* Updated `com.exasol:project-keeper-maven-plugin:3.0.0` to `4.1.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.11.0` to `3.12.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.3` to `3.2.5`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.3` to `3.2.5`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.5.0` to `1.6.0`
