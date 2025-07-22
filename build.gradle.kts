val googleGuava: String by project
val log4jAPI: String by project
val log4jCore: String by project
val apacheLang: String by project
val jetBrainsAnnotations: String by project
val junitJupiter: String by project

val mavenUserName: String? = System.getenv("MAVEN_USERNAME")
val mavenPassword: String? = System.getenv("MAVEN_PASSWORD")

plugins {
	id("java")
	id("maven-publish")
	id("com.github.joschi.licenser") version "0.6.1"
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
		exclude(group = "com.google.guava", module = "failureaccess")
		exclude(group = "com.google.guava", module = "listenablefuture")
	}
	// Apache
	implementation("org.apache.logging.log4j:log4j-api:${log4jAPI}") // Logging
	implementation("org.apache.logging.log4j:log4j-core:${log4jCore}") // Logging
	implementation("org.apache.commons:commons-lang3:${apacheLang}") // Utility
	// Other
	implementation("org.jetbrains:annotations:${jetBrainsAnnotations}") // Annotations
	// Test
	testImplementation("org.junit.jupiter:junit-jupiter:${junitJupiter}")
}

tasks.named<JavaCompile>("compileJava") {
	dependsOn("updateLicenses")
}

license {
	header = file("header.txt")
	include("**/*.java")
	exclude("**/Main.java")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
	environment("env.default", "3")
	environment("env.custom", "c")
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named<Javadoc>("javadoc") {
	exclude("**/Main.java")
	options {
		memberLevel = JavadocMemberLevel.PRIVATE
		(this as StandardJavadocDocletOptions).addStringOption("tag", "apiNote:a:API Note:")
	}
}

artifacts {
	archives(tasks.named<Jar>("sourcesJar"))
	archives(tasks.named<Jar>("javadocJar"))
}

tasks.register<JavaExec>("run") {
	group = "runs"
	mainClass.set("net.luis.utils.Main")
	classpath = sourceSets["main"].runtimeClasspath
	enableAssertions = true
	standardInput = System.`in`
	args = listOf()
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "net.luis"
			artifactId = "LUtils"
			version = "7.5.0"
			artifact(tasks.named<Jar>("jar"))
			artifact(tasks.named<Jar>("sourcesJar"))
			artifact(tasks.named<Jar>("javadocJar"))
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

tasks.named<Jar>("sourcesJar") {
	exclude("**/Main.java")
}

tasks.named<Jar>("jar") {
	exclude("net/luis/utils/Main.class")
	manifest {
		attributes(
			"Built-By" to "Luis Staudt",
			"Multi-Release" to "true"
		)
	}
}
