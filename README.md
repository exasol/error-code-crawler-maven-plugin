# Error Code Crawler

This maven plugin invocation of the [Exasol error code builder](https://github.com/exasol/error-reporting-java/) from Java source code. It runs some validations on these definitions, for example, that no error code is defined twice.

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
