# error-code-crawler-maven-plugin 1.2.0, released 2022-09-27

Code name: Fix vulnerabilities in dependencies

## Summary

This release fixes CVE-2022-38751 and CVE-2022-38752 in snakeyaml. Additionally lombok has been removed.

## Features

* #78: Fixed vulnerabilities in dependencies

## Refactorings

* #79: Removed Lombok

## Dependency Updates

### Compile Dependency Updates

* Added `org.yaml:snakeyaml:1.33`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.2` to `1.2.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.4.6` to `2.8.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
* Removed `org.projectlombok:lombok-maven-plugin:1.18.20.0`
