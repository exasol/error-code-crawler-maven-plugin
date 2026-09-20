# Error Code Crawler Maven Plugin 2.1.3, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-91776, CVE-2026-91777

## Summary

This release fixes the following 2 vulnerabilities:

### CVE-2026-91776 (CWE-770) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.2:compile`
Jackson-databind - Unbounded growth of type id cache in TypeDeserializer
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-91776?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-91776
* https://github.com/FasterXML/jackson-databind/pull/6203

### CVE-2026-91777 (CWE-407) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.2:compile`
Jackson-databind - Denial of Service (DoS)
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-91777?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-91777
* https://github.com/FasterXML/jackson-databind/pull/6204

## Security

* #131: Fixed vulnerability CVE-2026-91776 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.2:compile`
* #132: Fixed vulnerability CVE-2026-91777 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.2:compile`

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.2` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.5` to `5.7.6`
