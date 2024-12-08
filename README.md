# LUtils
Utility Library for Java.\
This library is a collection of all useful classes and methods that I have written over the last few years during my work on different projects and learning new things.\
**Disclaimer**: Versions before `5.0.0` are not stable and may contain bugs.
## Dependencies
This library is built with Java 21 and is compatible with Java 21 and higher.\
The library is built on top of the following libraries:
### Version 6.0.0

- Apache Commons Lang3 (3.17.0)
- Log4j2 (2.24.2)
- Google Guava (33.3.1-jre)
- JetBrains Annotations (26.0.1)

### Version 5.0.0

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
    * `registry` (update required)
        * `key` (update required)
    * `util`
* `exception`
* `function`
* `io`
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
* `logging`
    * `factory`
* `math`
    * `algorithm`
* `resources`
* `util`
    * `unsafe`
        * `classpath` (not tested)
        * `reflection`

## Documentation
The documentation is available at [docs.luis-st.net](https://docs.luis-st.net/net.luis.utils/module-summary.html).
## Examples
For examples and usage you can take a look at the tests in the `src/test/java` directory.
