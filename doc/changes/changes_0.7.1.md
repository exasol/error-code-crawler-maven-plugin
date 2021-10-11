# error-code-crawler-maven-plugin 0.7.1, released 2021-10-11

Code name: Fix NullPointerException

## Summary

This release fixes a NullPointerException when the error codes are not specified in the config. Now the error message contains a helpful message and mitigation.

## Features

* #53: Fixed a NullPointerException when the error codes are not specified in the config

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.0` to `0.7.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0` to `3.3.1`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.6.0` to `3.6.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
