/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for INI read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class IniIntegrationTest {
	
	private static final IniConfig PARSE_TYPES_CONFIG = new IniConfig(
		true, true, "", Set.of(';', '#'), '=', 1,
		false, false, true,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	@Test
	void applicationConfigRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.addGlobal("appName", "MyApplication")
			.addGlobal("version", 1)
			.addGlobal("debug", false)
			.startSection("database")
			.add("host", "localhost")
			.add("port", 5432)
			.add("name", "myapp_db")
			.add("username", "admin")
			.add("maxConnections", 20)
			.add("timeout", 30000)
			.endSection()
			.startSection("logging")
			.add("level", "INFO")
			.add("file", "/var/log/myapp/application.log")
			.add("console", true)
			.add("maxFileSize", "100MB")
			.add("maxHistory", 30)
			.endSection()
			.startSection("features")
			.add("enableCache", true)
			.add("enableMetrics", true)
			.add("enableTracing", false)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals("MyApplication", result.getGlobalAsString("appName"));
		assertEquals(1, result.getGlobalAsInteger("version"));
		assertFalse(result.getGlobalAsBoolean("debug"));
		
		IniSection database = result.requireSection("database");
		assertEquals("localhost", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
		assertEquals("myapp_db", database.getAsString("name"));
		assertEquals(20, database.getAsInteger("maxConnections"));
		
		IniSection logging = result.requireSection("logging");
		assertEquals("INFO", logging.getAsString("level"));
		assertTrue(logging.getAsBoolean("console"));
		assertEquals(30, logging.getAsInteger("maxHistory"));
		
		IniSection features = result.requireSection("features");
		assertTrue(features.getAsBoolean("enableCache"));
		assertFalse(features.getAsBoolean("enableTracing"));
	}
	
	@Test
	void serverConfigRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.startSection("server")
			.add("host", "0.0.0.0")
			.add("port", 8080)
			.add("ssl", true)
			.add("sslPort", 8443)
			.endSection()
			.startSection("ssl")
			.add("keyStore", "/etc/ssl/keystore.jks")
			.add("keyStorePassword", "changeit")
			.add("trustStore", "/etc/ssl/truststore.jks")
			.add("protocol", "TLSv1.3")
			.endSection()
			.startSection("proxy")
			.add("enabled", true)
			.add("upstream", "backend.internal")
			.add("upstreamPort", 3000)
			.add("timeout", 60)
			.endSection()
			.startSection("cache")
			.add("enabled", true)
			.add("ttl", 3600)
			.add("maxSize", 1024)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		IniSection server = result.requireSection("server");
		assertEquals("0.0.0.0", server.getAsString("host"));
		assertEquals(8080, server.getAsInteger("port"));
		assertTrue(server.getAsBoolean("ssl"));
		assertEquals(8443, server.getAsInteger("sslPort"));
		
		IniSection ssl = result.requireSection("ssl");
		assertEquals("/etc/ssl/keystore.jks", ssl.getAsString("keyStore"));
		assertEquals("TLSv1.3", ssl.getAsString("protocol"));
		
		IniSection proxy = result.requireSection("proxy");
		assertTrue(proxy.getAsBoolean("enabled"));
		assertEquals("backend.internal", proxy.getAsString("upstream"));
		
		IniSection cache = result.requireSection("cache");
		assertEquals(3600, cache.getAsInteger("ttl"));
		assertEquals(1024, cache.getAsInteger("maxSize"));
	}
	
	@Test
	void databaseConfigRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.startSection("primary")
			.add("driver", "postgresql")
			.add("host", "db-primary.internal")
			.add("port", 5432)
			.add("database", "production")
			.add("username", "app_user")
			.add("poolMin", 5)
			.add("poolMax", 50)
			.add("poolIdle", 600)
			.endSection()
			.startSection("replica")
			.add("driver", "postgresql")
			.add("host", "db-replica.internal")
			.add("port", 5432)
			.add("database", "production")
			.add("username", "readonly_user")
			.add("poolMin", 2)
			.add("poolMax", 20)
			.endSection()
			.startSection("cache")
			.add("driver", "redis")
			.add("host", "redis.internal")
			.add("port", 6379)
			.add("database", 0)
			.add("ttl", 300)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals(3, result.sectionCount());
		
		IniSection primary = result.requireSection("primary");
		assertEquals("postgresql", primary.getAsString("driver"));
		assertEquals("db-primary.internal", primary.getAsString("host"));
		assertEquals(50, primary.getAsInteger("poolMax"));
		
		IniSection replica = result.requireSection("replica");
		assertEquals("readonly_user", replica.getAsString("username"));
		assertEquals(20, replica.getAsInteger("poolMax"));
		
		IniSection cache = result.requireSection("cache");
		assertEquals("redis", cache.getAsString("driver"));
		assertEquals(6379, cache.getAsInteger("port"));
	}
	
	@Test
	void loggingConfigRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.addGlobal("rootLevel", "INFO")
			.addGlobal("pattern", "[%date] [%level] %logger - %message")
			.startSection("console")
			.add("enabled", true)
			.add("level", "DEBUG")
			.add("color", true)
			.endSection()
			.startSection("file")
			.add("enabled", true)
			.add("path", "/var/log/app.log")
			.add("level", "INFO")
			.add("maxSize", 104857600)
			.add("maxFiles", 10)
			.add("compress", true)
			.endSection()
			.startSection("syslog")
			.add("enabled", false)
			.add("host", "syslog.internal")
			.add("port", 514)
			.add("facility", "LOCAL0")
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertTrue(result.hasGlobalProperties());
		assertEquals("INFO", result.getGlobalAsString("rootLevel"));
		
		IniSection console = result.requireSection("console");
		assertTrue(console.getAsBoolean("enabled"));
		assertEquals("DEBUG", console.getAsString("level"));
		
		IniSection file = result.requireSection("file");
		assertEquals("/var/log/app.log", file.getAsString("path"));
		assertEquals(104857600, file.getAsInteger("maxSize"));
		assertTrue(file.getAsBoolean("compress"));
		
		IniSection syslog = result.requireSection("syslog");
		assertFalse(syslog.getAsBoolean("enabled"));
		assertEquals("LOCAL0", syslog.getAsString("facility"));
	}
	
	@Test
	void gameConfigRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.startSection("graphics")
			.add("resolution", "1920x1080")
			.add("fullscreen", true)
			.add("vsync", true)
			.add("fpsLimit", 60)
			.add("quality", "high")
			.add("shadows", true)
			.add("antialiasing", 4)
			.endSection()
			.startSection("audio")
			.add("masterVolume", 80)
			.add("musicVolume", 70)
			.add("sfxVolume", 90)
			.add("voiceVolume", 100)
			.add("muteOnFocusLoss", true)
			.endSection()
			.startSection("controls")
			.add("mouseSensitivity", 50)
			.add("invertY", false)
			.add("autoAim", false)
			.endSection()
			.startSection("network")
			.add("region", "eu-west")
			.add("maxPing", 100)
			.add("autoReconnect", true)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		IniSection graphics = result.requireSection("graphics");
		assertEquals("1920x1080", graphics.getAsString("resolution"));
		assertTrue(graphics.getAsBoolean("fullscreen"));
		assertEquals(60, graphics.getAsInteger("fpsLimit"));
		assertEquals(4, graphics.getAsInteger("antialiasing"));
		
		IniSection audio = result.requireSection("audio");
		assertEquals(80, audio.getAsInteger("masterVolume"));
		assertEquals(70, audio.getAsInteger("musicVolume"));
		
		IniSection controls = result.requireSection("controls");
		assertEquals(50, controls.getAsInteger("mouseSensitivity"));
		assertFalse(controls.getAsBoolean("invertY"));
		
		IniSection network = result.requireSection("network");
		assertEquals("eu-west", network.getAsString("region"));
		assertTrue(network.getAsBoolean("autoReconnect"));
	}
	
	@Test
	void builderGeneratedMatchesManual() throws IOException {
		IniDocument builderDoc = IniBuilder.document()
			.addGlobal("name", "Test")
			.addGlobal("version", 1)
			.startSection("section1")
			.add("key1", "value1")
			.add("key2", 42)
			.endSection()
			.build();
		
		IniDocument manualDoc = new IniDocument();
		manualDoc.addGlobal("name", new IniValue("Test"));
		manualDoc.addGlobal("version", new IniValue(1));
		IniSection section1 = manualDoc.createSection("section1");
		section1.add("key1", new IniValue("value1"));
		section1.add("key2", new IniValue(42));
		
		ByteArrayOutputStream builderOutput = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(builderOutput))) {
			writer.writeIni(builderDoc);
		}
		
		ByteArrayOutputStream manualOutput = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(manualOutput))) {
			writer.writeIni(manualDoc);
		}
		
		assertEquals(
			builderOutput.toString(StandardCharsets.UTF_8),
			manualOutput.toString(StandardCharsets.UTF_8)
		);
	}
	
	@Test
	void nullValuesWithEmptyStyleRoundTrip() throws IOException {
		IniConfig emptyNullConfig = new IniConfig(
			true, true, "", Set.of(';', '#'), '=', 1,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("name", "MyApp")
			.addGlobal("description", (String) null)
			.addGlobal("version", "1.0.0")
			.startSection("settings")
			.add("theme", "dark")
			.add("customCss", (String) null)
			.add("fontSize", 14)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), emptyNullConfig)) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), emptyNullConfig)) {
			result = reader.readIni();
		}
		
		assertEquals("MyApp", result.getGlobalAsString("name"));
		assertTrue(result.getGlobal("description").isIniNull());
		assertEquals("1.0.0", result.getGlobalAsString("version"));
		
		IniSection settings = result.requireSection("settings");
		assertEquals("dark", settings.getAsString("theme"));
		assertTrue(settings.get("customCss").isIniNull());
		assertEquals(14, settings.getAsInteger("fontSize"));
	}
	
	@Test
	void nullValuesWithNullStringStyleRoundTrip() throws IOException {
		IniConfig nullStringConfig = new IniConfig(
			true, true, "", Set.of(';', '#'), '=', 1,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.NULL_STRING, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("name", "MyApp")
			.addGlobal("description", (String) null)
			.addGlobal("version", "1.0.0")
			.startSection("settings")
			.add("theme", "dark")
			.add("customCss", (String) null)
			.add("fontSize", 14)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), nullStringConfig)) {
			writer.writeIni(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("null"));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), nullStringConfig)) {
			result = reader.readIni();
		}
		
		assertEquals("MyApp", result.getGlobalAsString("name"));
		// Note: "null" is read as string value, not as IniNull
		assertEquals("null", result.getGlobalAsString("description"));
		assertEquals("1.0.0", result.getGlobalAsString("version"));
		
		IniSection settings = result.requireSection("settings");
		assertEquals("dark", settings.getAsString("theme"));
		assertEquals("null", settings.getAsString("customCss"));
		assertEquals(14, settings.getAsInteger("fontSize"));
	}
	
	@Test
	void nullValuesWithSkipStyleRoundTrip() throws IOException {
		IniConfig skipNullConfig = new IniConfig(
			true, true, "", Set.of(';', '#'), '=', 1,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.SKIP, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("name", "MyApp")
			.addGlobal("description", (String) null)
			.addGlobal("version", "1.0.0")
			.startSection("settings")
			.add("theme", "dark")
			.add("customCss", (String) null)
			.add("fontSize", 14)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), skipNullConfig)) {
			writer.writeIni(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertFalse(written.contains("description"));
		assertFalse(written.contains("customCss"));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), skipNullConfig)) {
			result = reader.readIni();
		}
		
		assertEquals("MyApp", result.getGlobalAsString("name"));
		assertFalse(result.containsGlobalKey("description"));
		assertEquals("1.0.0", result.getGlobalAsString("version"));
		
		IniSection settings = result.requireSection("settings");
		assertEquals("dark", settings.getAsString("theme"));
		assertFalse(settings.containsKey("customCss"));
		assertEquals(14, settings.getAsInteger("fontSize"));
	}
	
	@Test
	void unicodeValuesRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.addGlobal("greeting.english", "Hello, World!")
			.addGlobal("greeting.japanese", "こんにちは世界")
			.addGlobal("greeting.korean", "안녕하세요 세계")
			.addGlobal("greeting.chinese", "你好世界")
			.addGlobal("greeting.arabic", "مرحبا بالعالم")
			.addGlobal("greeting.russian", "Привет мир")
			.startSection("special")
			.add("emoji", "Hello 👋🌍")
			.add("math", "∑∏∫∂")
			.add("currency", "€£¥₹")
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals("Hello, World!", result.getGlobalAsString("greeting.english"));
		assertEquals("こんにちは世界", result.getGlobalAsString("greeting.japanese"));
		assertEquals("안녕하세요 세계", result.getGlobalAsString("greeting.korean"));
		assertEquals("你好世界", result.getGlobalAsString("greeting.chinese"));
		assertEquals("مرحبا بالعالم", result.getGlobalAsString("greeting.arabic"));
		assertEquals("Привет мир", result.getGlobalAsString("greeting.russian"));
		
		IniSection special = result.requireSection("special");
		assertEquals("Hello 👋🌍", special.getAsString("emoji"));
		assertEquals("∑∏∫∂", special.getAsString("math"));
		assertEquals("€£¥₹", special.getAsString("currency"));
	}
	
	@Test
	void commentsAreSkippedDuringRead() throws IOException {
		String content = """
			; This is a comment
			appName = MyApp
			# Another comment
			version = 1.0.0
			
			[database]
			; Database configuration
			host = localhost
			# Port setting
			port = 5432
			""";
		
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals(2, result.globalSize());
		assertEquals("MyApp", result.getGlobalAsString("appName"));
		assertEquals("1.0.0", result.getGlobalAsString("version"));
		
		IniSection database = result.requireSection("database");
		assertEquals(2, database.size());
		assertEquals("localhost", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
	}
	
	@Test
	void customSeparatorRoundTrip() throws IOException {
		IniConfig colonConfig = new IniConfig(
			true, true, "", Set.of(';', '#'), ':', 1,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("app", "TestApp")
			.startSection("server")
			.add("host", "localhost")
			.add("port", 8080)
			.add("ssl", true)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), colonConfig)) {
			writer.writeIni(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains(":"));
		assertFalse(written.contains("="));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), colonConfig)) {
			result = reader.readIni();
		}
		
		assertEquals("TestApp", result.getGlobalAsString("app"));
		
		IniSection server = result.requireSection("server");
		assertEquals("localhost", server.getAsString("host"));
		assertEquals(8080, server.getAsInteger("port"));
		assertTrue(server.getAsBoolean("ssl"));
	}
	
	@Test
	void largeIniFileRoundTrip() throws IOException {
		IniDocument config = new IniDocument();
		
		for (int i = 0; i < 10; i++) {
			config.addGlobal("global" + i, new IniValue("value" + i));
		}
		
		for (int s = 0; s < 20; s++) {
			IniSection section = config.createSection("section" + s);
			for (int k = 0; k < 50; k++) {
				section.add("key" + k, new IniValue("Section " + s + " Key " + k));
				section.add("number" + k, new IniValue(s * 100 + k));
				section.add("flag" + k, new IniValue(k % 2 == 0));
			}
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals(10, result.globalSize());
		assertEquals(20, result.sectionCount());
		
		for (int i = 0; i < 10; i++) {
			assertEquals("value" + i, result.getGlobalAsString("global" + i));
		}
		
		for (int s = 0; s < 20; s++) {
			IniSection section = result.requireSection("section" + s);
			assertEquals(150, section.size());
			
			for (int k = 0; k < 50; k++) {
				assertEquals("Section " + s + " Key " + k, section.getAsString("key" + k));
				assertEquals(s * 100 + k, section.getAsInteger("number" + k));
				assertEquals(k % 2 == 0, section.getAsBoolean("flag" + k));
			}
		}
	}
	
	@Test
	void emptySectionRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.addGlobal("app", "TestApp")
			.startSection("empty1")
			.endSection()
			.startSection("filled")
			.add("key", "value")
			.endSection()
			.startSection("empty2")
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals("TestApp", result.getGlobalAsString("app"));
		assertEquals(3, result.sectionCount());
		
		assertTrue(result.containsSection("empty1"));
		assertTrue(result.requireSection("empty1").isEmpty());
		
		IniSection filled = result.requireSection("filled");
		assertEquals(1, filled.size());
		assertEquals("value", filled.getAsString("key"));
		
		assertTrue(result.containsSection("empty2"));
		assertTrue(result.requireSection("empty2").isEmpty());
	}
	
	@Test
	void alignmentPreservationRoundTrip() throws IOException {
		IniConfig alignedConfig = new IniConfig(
			true, true, "", Set.of(';', '#'), '=', 4,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("short", "value")
			.addGlobal("medium.key", "value")
			.addGlobal("very.long.key.path", "value")
			.startSection("settings")
			.add("a", "1")
			.add("bb", "2")
			.add("ccc", "3")
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), alignedConfig)) {
			writer.writeIni(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("    =    "));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), alignedConfig)) {
			result = reader.readIni();
		}
		
		assertEquals(3, result.globalSize());
		assertEquals("value", result.getGlobalAsString("short"));
		assertEquals("value", result.getGlobalAsString("medium.key"));
		assertEquals("value", result.getGlobalAsString("very.long.key.path"));
		
		IniSection settings = result.requireSection("settings");
		assertEquals("1", settings.getAsString("a"));
		assertEquals("2", settings.getAsString("bb"));
		assertEquals("3", settings.getAsString("ccc"));
	}
	
	@Test
	void indentedSectionKeysRoundTrip() throws IOException {
		IniConfig indentedConfig = new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, true,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniDocument config = IniBuilder.document()
			.addGlobal("app", "TestApp")
			.startSection("database")
			.add("host", "localhost")
			.add("port", 5432)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output), indentedConfig)) {
			writer.writeIni(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("\thost"));
		assertTrue(written.contains("\tport"));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), indentedConfig)) {
			result = reader.readIni();
		}
		
		assertEquals("TestApp", result.getGlobalAsString("app"));
		
		IniSection database = result.requireSection("database");
		assertEquals("localhost", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
	}
	
	@Test
	void floatingPointValuesRoundTrip() throws IOException {
		IniDocument config = IniBuilder.document()
			.addGlobal("pi", 3.14159265359)
			.addGlobal("e", 2.71828182845)
			.startSection("physics")
			.add("speedOfLight", 299792458)
			.add("gravitationalConstant", 6.67430e-11)
			.add("planckConstant", 6.62607015e-34)
			.endSection()
			.startSection("prices")
			.add("item1", 19.99)
			.add("item2", 149.95)
			.add("discount", 0.15)
			.endSection()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		IniDocument result;
		try (IniReader reader = new IniReader(new InputProvider(input), PARSE_TYPES_CONFIG)) {
			result = reader.readIni();
		}
		
		assertEquals(3.14159265359, result.getGlobalAsDouble("pi"), 0.0000001);
		assertEquals(2.71828182845, result.getGlobalAsDouble("e"), 0.0000001);
		
		IniSection physics = result.requireSection("physics");
		assertEquals(299792458, physics.getAsLong("speedOfLight"));
		assertEquals(6.67430e-11, physics.getAsDouble("gravitationalConstant"), 1e-20);
		assertEquals(6.62607015e-34, physics.getAsDouble("planckConstant"), 1e-43);
		
		IniSection prices = result.requireSection("prices");
		assertEquals(19.99, prices.getAsDouble("item1"), 0.001);
		assertEquals(149.95, prices.getAsDouble("item2"), 0.001);
		assertEquals(0.15, prices.getAsDouble("discount"), 0.001);
	}
}
