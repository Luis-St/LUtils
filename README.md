# LUtils
Utility Library for Java.\
This library is a collection of all useful classes and methods that I have written over the last few years during my work on different projects and learning new things.\
**Disclaimer**: Versions before `5.0.0` are not stable and may contain bugs.

## Dependencies
The current version is built on top of the following libraries:

- Java 25
- Google Guava (33.6.0-jre)
- Log4j2 (2.26.0)
- Apache Commons Lang3 (3.20.0)
- HikariCP (7.0.2)
- BouncyCastle (1.85.2)
- JSpecify (1.0.0)
- JetBrains Annotations (26.1.0)

The JDBC drivers for PostgreSQL, MySQL, MariaDB, SQL Server, H2 and SQLite are runtime dependencies only.

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
The library provides the following packages, all of them below `net.luis.utils`:

* `annotation`
    * `type`
* `collection`
    * `util`
* `crypto`
    * `algorithm`
    * `exception`
    * `key`
    * `util`
* `exception`
* `function`
    * `throwable`
* `grammar`
    * `lexer`
        * `rule`
            * `anchors`
            * `combinators`
            * `matchers`
            * `quantifiers`
        * `stream`
    * `parser`
        * `action`
            * `core`
            * `enhancers`
            * `filters`
            * `transformers`
        * `context`
        * `rule`
            * `assertions`
                * `anchors`
            * `combinators`
            * `core`
            * `matchers`
            * `quantifiers`
            * `reference`
        * `stream`
    * `token`
        * `type`
* `io`
    * `codec`
        * `constraint`
            * `builder`
            * `config`
                * `collection`
                * `io`
                * `numeric`
                * `temporal`
                    * `local`
                    * `offset`
                    * `zoned`
                * `validator`
            * `core`
                * `io`
                * `temporal`
            * `merged`
                * `collection`
                * `io`
                * `numeric`
                * `temporal`
                    * `local`
                    * `offset`
                    * `zoned`
            * `util`
        * `decoder`
        * `encoder`
        * `function`
        * `mapping`
        * `provider`
        * `types`
            * `array`
            * `i18n`
            * `io`
            * `primitive`
                * `numeric`
            * `stream`
            * `struct`
                * `collection`
            * `temporal`
                * `local`
                * `offset`
                * `zoned`
    * `data`
        * `binary`
            * `exception`
        * `config`
        * `ini`
            * `exception`
        * `json`
            * `exception`
        * `property`
            * `exception`
        * `toml`
            * `exception`
        * `toon`
            * `exception`
        * `xml`
            * `exception`
        * `yaml`
            * `exception`
    * `database`
        * `audit`
        * `condition`
            * `conditions`
                * `comparison`
                * `numeric`
                * `string`
                * `temporal`
        * `dialect`
            * `renderer`
                * `expression`
                    * `condition`
                    * `function`
        * `exception`
            * `client`
                * `dialect`
                * `transaction`
            * `database`
                * `concurrency`
                * `constraint`
                * `statement`
                * `transaction`
        * `expression`
            * `orderable`
        * `function`
            * `functions`
                * `aggregate`
                * `generic`
                * `numeric`
                    * `bitwise`
                    * `trigonometric`
                * `string`
                * `temporal`
                * `window`
            * `window`
                * `frame`
                    * `bound`
        * `index`
        * `migration`
            * `operation`
            * `store`
        * `query`
            * `crud`
            * `row`
            * `util`
        * `rendering`
        * `table`
        * `transaction`
        * `type`
            * `infer`
            * `parameter`
        * `util`
    * `exception`
    * `network`
        * `address`
            * `exception`
            * `format`
            * `ipv4`
            * `ipv6`
            * `mac`
        * `connection`
            * `context`
            * `event`
            * `exception`
            * `executor`
            * `ssl`
            * `tcp`
            * `udp`
        * `mail`
            * `message`
    * `reader`
* `lang`
    * `concurrent`
* `logging`
    * `factory`
* `math`
    * `algorithm`
* `resources`
* `util`
    * `getter`
    * `result`
    * `unsafe`
        * `classpath`
        * `reflection`

## Documentation
The documentation is available at [docs.luis-st.net](https://docs.luis-st.net/net.luis.utils/module-summary.html).\
\
The documentation is not update automatically, so it may be outdated.\
If you find any issues, please report them.

## Examples
For examples and usage you can take a look at the tests in the `src/test/java` directory.
