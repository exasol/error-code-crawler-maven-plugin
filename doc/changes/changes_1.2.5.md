# Error Code Crawler Maven Plugin 1.2.5, released 2023-??-??

Code name: Crawl projects with a Java module

## Summary

This release fixes crawling projects that contain a `module-info.java` file by excluding the `src/test/java/` folder. Please note that this currently only works when the module info file contains no dependencies, i.e. no `requires` entries. This issue must be investigated/fixed [upstream](https://github.com/INRIA/spoon/issues/5324).

## Bugfixes

* #85: Excluded test sources from crawling

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0` to `2.15.2`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0` to `2.15.2`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.14.1` to `3.14.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.4` to `1.2.5`
