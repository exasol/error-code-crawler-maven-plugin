# Error Code Crawler Maven Plugin 2.1.2, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-19032, CVE-2026-68497

## Summary

This release fixes the following 2 vulnerabilities:

### CVE-2026-19032 (CWE-470) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
com.fasterxml.jackson.core/jackson-databind - Unrestricted URI schemes in Path deserialization
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-19032?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-19032
* https://github.com/FasterXML/jackson-databind/pull/6129

### CVE-2026-68497 (CWE-770) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
jackson-databind - Allocation of Resources Without Limits or Throttling
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-68497?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-68497
* https://github.com/FasterXML/jackson-databind/pull/6127

## Security

* #128: Fixed vulnerability CVE-2026-19032 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`
* #129: Fixed vulnerability CVE-2026-68497 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.1` to `2.22.2`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.1` to `2.22.2`
* Updated `fr.inria.gforge.spoon:spoon-core:11.4.0` to `11.5.0`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter:6.1.2` to `6.1.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.1` to `2.1.2`
