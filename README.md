# Error Code Crawler

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

## Configuration

Each project has an individual error-tag (e.g. `EXM`). Different sub packages within the project can have different sub tags (`EXM-MODEL`). This plugin validates that the classes of the project only use the corresponding short tag.

For that you need no configure the projects error-tag mapping in the `errorCodeConfig.yml` in your projects root.

Example:

```yaml
error-tags:
  EXM:
    packages:
      - com.exasol.example
    highest-index: 3
  EXM-MODEL:
    packages:
      - com.exasol.example.model
    highest-index: 0
```

This configuration defines which java-packages or classes belong to which error code short tag. A package always includes all sub packages. If two error-tags match, the crawler will take the more specific one.

For example the class `com.exasol.example.Test` belongs to a sub package of `com.exasol.example` --> `ECM` and to `com.exasol.example.model` --> `ECM-MODEL`. Since the last package name is more specific (longer) this crawler will validate the tag `ECM-MODEL` for these classes. It will not accept `ECM` there.

The `highest-index` property contains the index of the last error code declaration. So in the example the highest error code could be `E-EXM-3`. This plugin validates that value of this property is higher or equal that the actual highest. You can safely use this property to determine the next error code.

## Usage

You can also invoke this plugin manually using `mvn error-code-crawler:verify`.

## Additional Information

* [Changelog](doc/changes/changelog.md)
* [License](LICENSE)
* [Dependencies](NOTICE)
