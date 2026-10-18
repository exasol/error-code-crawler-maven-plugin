# Error Code Crawler Maven Plugin 2.1.3, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-110257, CVE-2026-110258, CVE-2026-110259, CVE-2026-110260, CVE-2026-110261, CVE-2026-110263

## Summary

This release fixes the following 6 vulnerabilities:

### CVE-2026-110257 (CWE-789) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 5.0 before 9.11 contains an excessive memory allocation vulnerability in ClassReader.readBootstrapMethodsAttribute() that sizes an int array from num_bootstrap_methods without validating attribute_length. Attackers can supply a crafted class file of about 100 bytes that forces roughly 2 GB allocation, causing an OutOfMemoryError that crashes the JVM or consuming thread.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110257?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110257
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110257.json

### CVE-2026-110258 (CWE-789) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 3.0 before 9.11 contains an excessive memory allocation vulnerability in Label.addSubroutineRetSuccessors() that fails to enforce the single-RET-per-JSR rule. Attackers can supply a crafted invalid class file with multiple RET instructions for one JSR, triggering a roughly 2 GB allocation that crashes the JVM.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110258?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110258
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110258.json

### CVE-2026-110259 (CWE-674) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 3.0 before 9.11 contains an uncontrolled recursion vulnerability in ClassWriter.replaceAsmInstructions() that allows attackers to cause denial of service through crafted class files. Attackers can supply a class with a large method containing many long forward jumps, triggering unbounded toByteArray() recursion and a StackOverflowError during transformation.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110259?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110259
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110259.json

### CVE-2026-110260 (CWE-407) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 3.0 before 9.11 contains a quadratic memory allocation vulnerability in MethodWriter.computeMaxStackAndLocal() and computeAllFrames() when using COMPUTE_MAXS or COMPUTE_FRAMES. Attackers can supply a crafted class file with thousands of overlapping exception handlers, causing excessive Edge allocations and an OutOfMemoryError that crashes the JVM.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110260?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110260
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110260.json

### CVE-2026-110261 (CWE-407) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 3.0 before 9.11 contains a denial of service vulnerability that allows attackers to exhaust CPU by supplying crafted class files processed with ClassWriter.COMPUTE_FRAMES. Attackers can craft methods that make the unbounded MethodWriter.computeAllFrames() fix point algorithm converge slowly, tying up transforming threads for unbounded time.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110261?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110261
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110261.json

### CVE-2026-110263 (CWE-674) in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
OW2 ASM 3.0 before 9.11 contains an uncontrolled recursion vulnerability in SignatureReader.parseType() that allows attackers to exhaust the stack via deeply nested generic type signatures. Attackers can supply a crafted class file with a long Signature attribute containing many nested type arguments to trigger a StackOverflowError during class transformation, causing denial of service.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110263?component-type=maven&component-name=org.ow2.asm%2Fasm&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110263
* https://github.com/CVEProject/cvelistV5/blob/main/cves/2026/110xxx/CVE-2026-110263.json

## Security

* #137: Fixed vulnerability CVE-2026-110257 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
* #138: Fixed vulnerability CVE-2026-110258 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
* #139: Fixed vulnerability CVE-2026-110259 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
* #140: Fixed vulnerability CVE-2026-110260 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
* #141: Fixed vulnerability CVE-2026-110261 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`
* #142: Fixed vulnerability CVE-2026-110263 in dependency `org.ow2.asm:asm:jar:9.9.1:provided`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.2` to `2.22.4`
* Updated `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.2` to `2.22.4`

### Test Dependency Updates

* Updated `org.apache.maven.shared:maven-verifier:1.8.0` to `2.0.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.2` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.5` to `5.7.7`
