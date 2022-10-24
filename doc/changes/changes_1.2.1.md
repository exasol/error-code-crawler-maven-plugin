# error-code-crawler-maven-plugin 1.2.1, released 2022-10-24

Code name: Fix vulnerabilities in dependencies

## Summary

This release fixes CVE-2022-42003 and CVE-2022-42004 in `com.fasterxml.jackson.core:jackson-databind`.

## Bugfixes

* #81: Fixed vulnerabilities in dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-code-model-java:2.1.0` to `2.1.1`
* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3` to `2.13.4`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3` to `2.13.4`
* Updated `fr.inria.gforge.spoon:spoon-core:10.1.1` to `10.2.0`
* Removed `org.apache.commons:commons-compress:1.21`

### Test Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.1.0` to `1.2.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.10` to `3.10.1`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.2` to `5.9.1`
* Updated `org.junit.jupiter:junit-jupiter:5.8.2` to `5.9.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.0` to `1.2.1`
