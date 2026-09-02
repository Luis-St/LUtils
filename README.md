# LUtils
Utility Library for Java.\
This library is a collection of all useful classes and methods that I have written over the last few years during my work on different projects and learning new things.\
**Disclaimer**: Versions before `5.0.0` are not stable and may contain bugs.

## Dependencies
The library is built on top of the following libraries:

### Version 11.0.0

- Java 25
- Apache Commons Lang3 (3.18.0)
- Log4j2 (2.25.2)
- Google Guava (33.5.0-jre)
- JetBrains Annotations (26.0.2)
- BouncyCastle (1.85.2, optional: only needed for AES-GCM-SIV and SLH-DSA)

### Version 6.0.0

- Java 21
- Apache Commons Lang3 (3.17.0)
- Log4j2 (2.24.2)
- Google Guava (33.3.1-jre)
- JetBrains Annotations (26.0.1)

### Version 5.0.0

- Java 17
- Apache Commons Lang3 (3.14.0)
- Log4j2 (2.22.1)
- Google Guava (33.0.0-jre)
- JetBrains Annotations (24.1.0)

## Installation
If you like to use this library, you can use it with Maven or Gradle.
### Gradle setup (Recommended)
If you are using Gradle, add the following lines to your `build.gradle` file:

```groovy
repositories {
	maven {
		url "https://maven.luis-st.net/libraries/"
	}
}

dependencies {
	implementation "net.luis:LUtils:${version}"
}
```

### Maven setup
If you are using Maven, add the following lines to your `pom.xml` file:

```xml

<project>
	<repositories>
		<repository>
			<id>luis-st</id>
			<url>https://maven.luis-st.net/libraries/</url>
		</repository>
		<!-- Other repositories here -->
	</repositories>
	<dependencies>
		<dependency>
			<groupId>net.luis</groupId>
			<artifactId>LUtils</artifactId>
			<version>${version}</version>
		</dependency>
		<!-- Other dependencies here -->
	</dependencies>
</project>
```

## Packages
The library provides the following packages:

* `annotation`
    * `type`
* `collection`
    * `registry` (removed in 7.4.0)
        * `key` (removed in 7.4.0)
    * `util`
* `crypto` (since 11.0.0)
    * `algorithm`
    * `exception`
    * `key`
    * `util`
* `exception`
* `function`
    * `throwable`
* `io`
    * `codec` (since 7.0.0)
        * `decoder`
        * `encoder`
        * `function` (since 7.5.0)
        * `group` (removed in 7.5.0)
            * `function` (moved in 7.5.0 to `codec.function`)
            * `grouper` (removed in 7.5.0)
        * `provider`
        * `struct`
    * `data` (since 6.0.0)
        * `config`
        * `json`
            * `exception`
        * `properties`
            * `exception`
        * `xml`
            * `exception`
    * `exception`
    * `reader`
    * `token` (since 7.4.0)
        * `actions` (since 8.0.0)
            * `core`
            * `enhancers`
            * `filters`
            * `transformers`
        * `context`
        * `definition`
        * `grammar` (since 8.0.0)
        * `rule` (removed in 7.5.0)
            * `actions` (moved in 8.0.0 to `token.actions`)
            * `rules` (moved in 8.0.0 to `token.rules`)
        * `rules` (since 8.0.0)
            * `assertions`
                * `anchors`
            * `combinators`
            * `matchers`
            * `quantifiers`
            * `reference`
        * `stream` (since 8.0.0)
        * `tokens`
        * `type` (since 8.0.0)
            * `classifier`
* `lang` (since 5.5.0)
    * `concurrency`
* `logging`
    * `factory`
* `math`
    * `algorithm`
* `resources`
* `util`
    * `getter`
    * `unsafe`
        * `classpath` (not tested)
        * `reflection`

## Cryptography
The `crypto` package (since `11.0.0`) is a post-quantum-first helper layer over the JCA.\
Algorithms are types rather than strings, so a typo is a compile error, and the weak primitives
(RSA, DSA, MD5, SHA-1, CBC, ECB, 3DES, PKCS#1 v1.5) are not modelled at all and cannot be selected
by accident.

* Hashing, HMAC and HKDF: `Hashes`, `Hasher`, `Macs`, `Kdf`
* Authenticated encryption: `Aead` (AES-256-GCM, ChaCha20-Poly1305, AES-256-GCM-SIV)
* Key encapsulation: `Kems` (ML-KEM, X25519/X448 as DHKEM, and hybrids of the two)
* Signatures: `Signatures` (Ed25519, Ed448, ECDSA, ML-DSA, SLH-DSA, and hybrids)
* Public-key encryption: `Sealed`, `SealedStream`, `SealedForMany`
* Passwords and key files: `Passwords` (PBKDF2 in the PHC format), `Pem`

Every artifact written to disk or the wire starts with a magic, a version and a `CryptoSuite` id, so
the default suite can change without invalidating anything already written. The default is
`CryptoSuite.HYBRID_V1`, which stays secure as long as either the classical or the post-quantum half
holds.

BouncyCastle is optional and never installed implicitly: call `Providers.installBouncyCastle()` once
at startup if AES-256-GCM-SIV or SLH-DSA is needed, and `Providers.require(CryptoSuite.current())`
to fail loudly on a misconfigured runtime instead of at the first encrypted request.

## Documentation
The documentation is available at [docs.luis-st.net](https://docs.luis-st.net/net.luis.utils/module-summary.html).\
\
The documentation is not update automatically, so it may be outdated.\
If you find any issues, please report them.
## Examples
For examples and usage you can take a look at the tests in the `src/test/java` directory.
