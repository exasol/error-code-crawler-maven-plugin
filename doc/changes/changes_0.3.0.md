# error-code-crawler-maven-plugin 0.3.0, released 2021-04-27

Code name: Parameter validation

## Features:

* #22: Added validation to check that build parameters are declared
* #24: Added support for new API of error-reporting-java 0.4.0
* #28: Added validation that there are no unnamed parameters

## Refactoring

* #32: Replace explicit types by var

## Bugfixes

* #31: Fixed NullPointerException when assigning ErrorMessageBuilder to variable (now throwing a proper error message)

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.2.2` to `0.4.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.2.0` to `0.3.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `0.7.0`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
