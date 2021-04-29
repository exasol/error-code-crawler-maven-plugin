# error-code-crawler-maven-plugin 0.4.0, released 2021-04-29

Code name: Fixed source scope

## Summary

In this release we renamed `errorCodeConfig.yml` to `error_code_config.yml` in order to unify the casing of our project files. Don't forget to update the file when updating the plugin version.

## Refactoring

* #35: Removed hardcoded version numbers from tests
* #37: Renamed `errorCodeConfig.yml` to `error_code_config.yml`

## Bugfixes

* #40: Fixed source scope so that it does not crawl resources

## Dependency Updates

### Test Dependency Updates

* Added `com.exasol:maven-project-version-getter:0.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.3.0` to `0.4.0`
