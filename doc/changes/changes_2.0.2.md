# Error Code Crawler Maven Plugin 2.0.2, released 2024-??-??

Code name:

## Summary

This release fixes writing error report failure when target directory does not exist.

## Bugfixes

* #100: Fixed writing error report when the target directory does not exist
* #103: Marked error code crawler plugin as thread-safe to prevent WARNING messages and support multi-threading
* #99: Fixed source file complete path by adding sub-project prefix for multi-module projects

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.1` to `2.0.2`
