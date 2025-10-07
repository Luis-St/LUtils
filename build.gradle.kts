import net.luis.lm.LineEnding
import java.time.Year

val googleGuava: String by project
val log4jAPI: String by project
val log4jCore: String by project
val apacheLang: String by project
val jetBrainsAnnotations: String by project
val junitJupiter: String by project
val junitPlatformLauncher: String by project

val mavenUserName: String? = System.getenv("MAVEN_USERNAME")
val mavenPassword: String? = System.getenv("MAVEN_PASSWORD")
val projectVersion: String? = System.getenv("VERSION")?.replace("v", "", ignoreCase = true)

plugins {
	id("java")
	id("maven-publish")
	id("net.luis.lm")
}

repositories {
	mavenCentral()
}

dependencies {
	// Google
	implementation("com.google.guava:guava:${googleGuava}") {  // Utility
		exclude(group = "org.checkerframework")
		exclude(group = "com.google.code.findbugs")
		exclude(group = "com.google.errorprone")
		exclude(group = "com.google.j2objc")
	}
	// Apache
	implementation("org.apache.logging.log4j:log4j-api:${log4jAPI}") // Logging
	implementation("org.apache.logging.log4j:log4j-core:${log4jCore}") // Logging
	implementation("org.apache.commons:commons-lang3:${apacheLang}") // Utility
	// Other
	implementation("org.jetbrains:annotations:${jetBrainsAnnotations}") // Annotations
	// Test
	testImplementation("org.junit.jupiter:junit-jupiter:${junitJupiter}")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:${junitPlatformLauncher}")
}

licenseManager {
	header = "header.txt"
	lineEnding = LineEnding.LF
	spacingAfterHeader = 1
	
	variable("year", Year.now())
	variable("author", "Luis Staudt")
	variable("project", rootProject.name)
	
	sourceSets = listOf("main", "test")
	
	include("**/*.java")
	exclude("**/Main.java")
}

tasks.compileJava {
	dependsOn(tasks.named("updateLicenses"))
	
	doFirst {
		options.compilerArgs.addAll(listOf("--module-path", classpath.asPath))
		classpath = files()
	}
}

tasks.register<JavaExec>("run") {
	group = "runs"
	mainClass.set("net.luis.utils.Main")
	
	enableAssertions = true
	standardInput = System.`in`
	args = listOf()
	
	classpath = files()
	jvmArgs = listOf(
		"--module-path", sourceSets["main"].runtimeClasspath.asPath,
		"--module", "net.luis.utils/net.luis.utils.Main"
	)
}

tasks.named<Test>("test") {
	useJUnitPlatform()
	environment("env.default", "3")
	environment("env.custom", "c")
	
	doLast {
		delete("./logs/debug.log")
		delete("./logs/info.log")
		delete("./logs/error.log")
		delete("./logs/errors.log")
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
	modularity.inferModulePath.set(true)
	
	withSourcesJar()
	withJavadocJar()
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			if (projectVersion != null) {
				groupId = "net.luis"
				artifactId = "LUtils"
				version = projectVersion
				artifact(tasks.named<Jar>("jar"))
				artifact(tasks.named<Jar>("sourcesJar"))
				artifact(tasks.named<Jar>("javadocJar"))
			} else {
				System.err.println("No version provided. Set the VERSION environment variable.")
			}
		}
	}
	repositories {
		if (mavenUserName != null && mavenPassword != null) {
			maven {
				url = uri("https://maven.luis-st.net/libraries/")
				credentials {
					username = mavenUserName
					password = mavenPassword
				}
			}
		} else {
			System.err.println("No credentials provided. Publishing to maven.luis-st.net not possible.")
		}
	}
}

tasks.named<Javadoc>("javadoc") {
	exclude("**/Main.java")
	options {
		memberLevel = JavadocMemberLevel.PRIVATE
		(this as StandardJavadocDocletOptions).addStringOption("tag", "apiNote:a:API Note:")
	}
}

tasks.named<Jar>("sourcesJar") {
	exclude("**/Main.java")
}

tasks.named<Jar>("jar") {
	exclude("net/luis/utils/Main.class")
	manifest {
		attributes(
			"Built-By" to "Luis Staudt",
			"Multi-Release" to "true",
			"Implementation-Title" to rootProject.name,
			"Implementation-Version" to (projectVersion ?: "0.0.0"),
			"Implementation-Vendor" to "Luis Staudt",
			"Implementation-URL" to "https://www.luis-st.net/",
		)
	}
}
