# LUtils
Utility Library for Java.\
This library is a collection of all useful classes and methods that I have written over the last few years during my work on different projects and learning new things.

**Disclaimer**: Versions before `5.0.0` are not stable and may contain bugs.

## Dependencies
This library is built with Java 17 and is compatible with Java 17 and higher.\
The library is built on top of the following libraries:
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
If you are using maven, add the following lines to your `pom.xml` file:
```xml
<project>
    ...
    <repositories>
        <repository>
            <id>luis-st</id>
            <url>https://maven.luis-st.net/libraries/</url>
        </repository>
    </repositories>
    ...
    <dependencies>
        <dependency>
            <groupId>net.luis</groupId>
            <artifactId>LUtils</artifactId>
            <version>${version}</version>
        </dependency>
    </dependencies>
    ...
</project>
```

## Packages
The library provides the following packages:
* `annotation`
    * `type`
* `collection`
* `exception`
* `function`
* `io`
* `logging`
* `math`
* `resources`
* `util`
    * `unsafe`
        * `classpath`
        * `reflection`

## Documentation
The documentation is available at [docs.luis-st.net](https://docs.luis-st.net/LUtils/).

## Examples
For examples and usage you can take a look at the tests in the `src/test/java` directory.
