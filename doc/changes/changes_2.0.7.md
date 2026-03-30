# Error Code Crawler Maven Plugin 2.0.7, released 2026-03-30

Code name: Fixed vulnerability CVE-2025-67030 in org.codehaus.plexus:plexus-utils:jar:3.6.0:provided

## Summary

This release fixes the following vulnerability:

### CVE-2025-67030 (CWE-22) in dependency `org.codehaus.plexus:plexus-utils:jar:3.6.0:provided`
Directory Traversal vulnerability in the extractFile method of org.codehaus.plexus.util.Expand in plexus-utils before 6d780b3378829318ba5c2d29547e0012d5b29642. This allows an attacker to execute arbitrary code
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2025-67030?component-type=maven&component-name=org.codehaus.plexus%2Fplexus-utils&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-67030
* https://github.com/advisories/GHSA-6fmv-xxpf-w3cw
* https://www.sonatype.com/products/sonatype-guide/oss-index-users

## Security

* #120: Fixed vulnerability CVE-2025-67030 in dependency `org.codehaus.plexus:plexus-utils:jar:3.6.0:provided`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.0` to `2.21.2`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.0` to `2.21.2`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:4.3.1` to `4.4.1`
* Updated `org.junit.jupiter:junit-jupiter:6.0.2` to `6.0.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.6` to `2.0.7`
* Updated `com.exasol:project-keeper-maven-plugin:5.4.5` to `5.4.6`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.1` to `3.15.0`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.10.2` to `3.15.2`
* Updated `org.apache.maven.plugins:maven-source-plugin:3.2.1` to `3.4.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.20.1` to `2.21.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.8.0` to `2.3.0`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.9.0` to `0.10.0`
