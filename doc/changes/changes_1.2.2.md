# error-code-crawler-maven-plugin 1.2.2, released 2023-01-19

Code name: Fixed vulnerabilities in dependencies on top of 1.2.1

## Summary

Ignored vulnerabilities as an exploit requires write access to the source code repository and attempts to inject malicious code will be detected during regular code reviews:
* [com.fasterxml.jackson.core:jackson-core:jar:2.13.4](https://ossindex.sonatype.org/component/pkg:maven/com.fasterxml.jackson.core/jackson-core@2.13.4) in compile
    * sonatype-2022-6438: 1 vulnerability (7.5)
* [org.yaml:snakeyaml:jar:1.33](https://ossindex.sonatype.org/component/pkg:maven/org.yaml/snakeyaml@1.33) in compile
    * CVE-2022-1471, severity CWE-502: Deserialization of Untrusted Data (9.8)

## Bugfixes

* #10: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.4` to `2.14.1`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4` to `2.14.1`
* Updated `org.slf4j:slf4j-jdk14:1.7.36` to `2.0.6`
* Removed `org.yaml:snakeyaml:1.33`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.10.1` to `3.12.3`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.1` to `5.9.2`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
