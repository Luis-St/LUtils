# LUtils
Utility Library for Java.\
This library is a collection of all useful classes and methods that I have written over the last few years during my work on different projects and learning new things.\
**Disclaimer**: Versions before `5.0.0` are not stable and may contain bugs.
## Dependencies
The library is built on top of the following libraries:
### Version 8.0.0

- Java 25
- Apache Commons Lang3 (3.18.0)
- Log4j2 (2.25.2)
- Google Guava (33.5.0-jre)
- JetBrains Annotations (26.0.2)

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

## Documentation
The documentation is available at [docs.luis-st.net](https://docs.luis-st.net/net.luis.utils/module-summary.html).\
\
The documentation is not update automatically, so it may be outdated.\
If you find any issues, please report them.
## Examples
For examples and usage you can take a look at the tests in the `src/test/java` directory.
