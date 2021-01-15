# Error Code Crawler

[![Build Status](https://travis-ci.com/exasol/error-code-crawler-maven-plugin.svg?branch=master)](https://travis-ci.com/exasol/error-code-crawler-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.exasol/error-code-crawler-maven-plugin)](https://search.maven.org/artifact/com.exasol/error-code-crawler-maven-plugin)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aerror-code-crawler-maven-plugin&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Aerror-code-crawler-maven-plugin)

This maven plugin analyzes invocations of the [Exasol error code builder](https://github.com/exasol/error-reporting-java/) from Java source code. It runs some validations on these definitions, for example, that no error code is defined twice.

**This plugin is still under development.** It can not write a report yet, but only run the validations.

## Installation

Add the plugin to your `pom.xml`:

```xml

<plugins>
    <plugin>
        <groupId>com.exasol</groupId>
        <artifactId>error-code-crawler-maven-plugin</artifactId>
        <version>LATEST VERSION</version>
        <executions>
            <execution>
                <goals>
                    <goal>verify</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

This will add the plugin to the maven `verify` lifecycle phase.

## Usage

You can also invoke this plugin manually using `mvn error-code-crawler:verify`.

## Additional Information

* [Changelog](doc/changes/changelog.md)
* [License](LICENSE)
* [Dependencies](NOTICE)
