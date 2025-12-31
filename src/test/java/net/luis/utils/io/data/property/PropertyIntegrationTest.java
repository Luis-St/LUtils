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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for property read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class PropertyIntegrationTest {
	
	private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9._${}:\\[\\]|-]+$");
	private static final Pattern VALUE_PATTERN = Pattern.compile(".*");
	
	private static final PropertyConfig DEFAULT_CONFIG = PropertyConfig.DEFAULT;
	private static final PropertyConfig ADVANCED_CONFIG = PropertyConfig.ADVANCED;
	private static final PropertyConfig COMPACTION_CONFIG = new PropertyConfig(
		'=', 1, Set.of('#'),
		KEY_PATTERN, VALUE_PATTERN,
		true, StandardCharsets.UTF_8,
		true, "\t",
		'[', ']', ',',
		true, true,
		PropertyConfig.NullStyle.EMPTY,
		true, 2,
		':', ":-",
		null
	);
	
	@Test
	void applicationConfigRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("database.host", new PropertyValue("localhost"));
		config.add("database.port", new PropertyValue(5432));
		config.add("database.name", new PropertyValue("myapp_db"));
		config.add("database.username", new PropertyValue("admin"));
		config.add("database.password", new PropertyValue("secret123"));
		config.add("database.pool.minSize", new PropertyValue(5));
		config.add("database.pool.maxSize", new PropertyValue(20));
		config.add("database.pool.timeout", new PropertyValue(30000));
		config.add("database.pool.idleTimeout", new PropertyValue(600000));
		
		config.add("logging.level", new PropertyValue("INFO"));
		config.add("logging.format", new PropertyValue("[%d{yyyy-MM-dd HH:mm:ss}] [%level] %logger - %msg%n"));
		config.add("logging.file.path", new PropertyValue("/var/log/myapp/application.log"));
		config.add("logging.file.maxSize", new PropertyValue("100MB"));
		config.add("logging.file.maxHistory", new PropertyValue(30));
		
		config.add("features.enableCache", new PropertyValue(true));
		config.add("features.enableMetrics", new PropertyValue(true));
		config.add("features.enableTracing", new PropertyValue(false));
		config.add("features.experimental.newUI", new PropertyValue(false));
		config.add("features.experimental.betaFeatures", new PropertyValue(true));
		
		config.add("server.port", new PropertyValue(8080));
		config.add("server.contextPath", new PropertyValue("/api"));
		config.add("server.compression.enabled", new PropertyValue(true));
		config.add("server.compression.minResponseSize", new PropertyValue(1024));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("localhost", result.get("database.host").getAsPropertyValue().getAsString());
		assertEquals(5432, result.get("database.port").getAsPropertyValue().getAsNumber().intValue());
		assertEquals("myapp_db", result.get("database.name").getAsPropertyValue().getAsString());
		assertEquals(20, result.get("database.pool.maxSize").getAsPropertyValue().getAsNumber().intValue());
		assertEquals(600000, result.get("database.pool.idleTimeout").getAsPropertyValue().getAsNumber().intValue());
		
		assertEquals("INFO", result.get("logging.level").getAsPropertyValue().getAsString());
		assertEquals(30, result.get("logging.file.maxHistory").getAsPropertyValue().getAsNumber().intValue());
		
		assertTrue(result.get("features.enableCache").getAsPropertyValue().getAsBoolean());
		assertFalse(result.get("features.enableTracing").getAsPropertyValue().getAsBoolean());
		assertTrue(result.get("features.experimental.betaFeatures").getAsPropertyValue().getAsBoolean());
		
		assertEquals(8080, result.get("server.port").getAsPropertyValue().getAsNumber().intValue());
	}
	
	@Test
	void environmentConfigRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("development.database.host", new PropertyValue("localhost"));
		config.add("development.database.port", new PropertyValue(5432));
		config.add("development.debug", new PropertyValue(true));
		config.add("development.mockServices", new PropertyValue(true));
		
		config.add("staging.database.host", new PropertyValue("staging-db.example.com"));
		config.add("staging.database.port", new PropertyValue(5432));
		config.add("staging.debug", new PropertyValue(true));
		config.add("staging.mockServices", new PropertyValue(false));
		
		config.add("production.database.host", new PropertyValue("prod-db.example.com"));
		config.add("production.database.port", new PropertyValue(5432));
		config.add("production.debug", new PropertyValue(false));
		config.add("production.mockServices", new PropertyValue(false));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("localhost", result.get("development.database.host").getAsPropertyValue().getAsString());
		assertTrue(result.get("development.debug").getAsPropertyValue().getAsBoolean());
		
		assertEquals("staging-db.example.com", result.get("staging.database.host").getAsPropertyValue().getAsString());
		assertFalse(result.get("staging.mockServices").getAsPropertyValue().getAsBoolean());
		
		assertEquals("prod-db.example.com", result.get("production.database.host").getAsPropertyValue().getAsString());
		assertFalse(result.get("production.debug").getAsPropertyValue().getAsBoolean());
	}
	
	@Test
	void hierarchicalPropertiesRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("spring.application.name", new PropertyValue("my-microservice"));
		config.add("spring.application.version", new PropertyValue("1.0.0"));
		config.add("spring.profiles.active", new PropertyValue("default"));
		
		config.add("spring.datasource.url", new PropertyValue("jdbc:postgresql://localhost:5432/myapp"));
		config.add("spring.datasource.username", new PropertyValue("myapp"));
		config.add("spring.datasource.driver-class-name", new PropertyValue("org.postgresql.Driver"));
		config.add("spring.datasource.hikari.minimum-idle", new PropertyValue(5));
		config.add("spring.datasource.hikari.maximum-pool-size", new PropertyValue(20));
		config.add("spring.datasource.hikari.idle-timeout", new PropertyValue(30000));
		config.add("spring.datasource.hikari.connection-timeout", new PropertyValue(30000));
		config.add("spring.datasource.hikari.max-lifetime", new PropertyValue(1800000));
		
		config.add("spring.jpa.open-in-view", new PropertyValue(false));
		config.add("spring.jpa.show-sql", new PropertyValue(false));
		config.add("spring.jpa.hibernate.ddl-auto", new PropertyValue("validate"));
		config.add("spring.jpa.hibernate.naming.physical-strategy", new PropertyValue("org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));
		config.add("spring.jpa.properties.hibernate.dialect", new PropertyValue("org.hibernate.dialect.PostgreSQLDialect"));
		config.add("spring.jpa.properties.hibernate.format_sql", new PropertyValue(true));
		
		config.add("management.endpoints.web.exposure.include", new PropertyValue("health,info,metrics,prometheus"));
		config.add("management.metrics.export.prometheus.enabled", new PropertyValue(true));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("my-microservice", result.get("spring.application.name").getAsPropertyValue().getAsString());
		assertEquals("1.0.0", result.get("spring.application.version").getAsPropertyValue().getAsString());
		assertEquals(20, result.get("spring.datasource.hikari.maximum-pool-size").getAsPropertyValue().getAsNumber().intValue());
		assertFalse(result.get("spring.jpa.open-in-view").getAsPropertyValue().getAsBoolean());
		assertTrue(result.get("management.metrics.export.prometheus.enabled").getAsPropertyValue().getAsBoolean());
	}
	
	@Test
	void inlineArraysRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		PropertyArray ports = new PropertyArray();
		ports.add(new PropertyValue(8080));
		ports.add(new PropertyValue(8443));
		ports.add(new PropertyValue(9000));
		config.add("server.ports", ports);
		
		PropertyArray hosts = new PropertyArray();
		hosts.add(new PropertyValue("localhost"));
		hosts.add(new PropertyValue("192.168.1.1"));
		hosts.add(new PropertyValue("10.0.0.1"));
		config.add("server.allowedHosts", hosts);
		
		PropertyArray features = new PropertyArray();
		features.add(new PropertyValue("cache"));
		features.add(new PropertyValue("metrics"));
		features.add(new PropertyValue("tracing"));
		features.add(new PropertyValue("logging"));
		config.add("app.enabledFeatures", features);
		
		PropertyArray mixedArray = new PropertyArray();
		mixedArray.add(new PropertyValue("string"));
		mixedArray.add(new PropertyValue(123));
		mixedArray.add(new PropertyValue(true));
		mixedArray.add(new PropertyValue(45.67));
		config.add("app.mixedValues", mixedArray);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		PropertyArray resultPorts = result.get("server.ports").getAsPropertyArray();
		assertEquals(3, resultPorts.size());
		assertEquals(8080, resultPorts.get(0).getAsPropertyValue().getAsNumber().intValue());
		assertEquals(8443, resultPorts.get(1).getAsPropertyValue().getAsNumber().intValue());
		assertEquals(9000, resultPorts.get(2).getAsPropertyValue().getAsNumber().intValue());
		
		PropertyArray resultHosts = result.get("server.allowedHosts").getAsPropertyArray();
		assertEquals(3, resultHosts.size());
		assertEquals("localhost", resultHosts.get(0).getAsPropertyValue().getAsString());
		
		PropertyArray resultFeatures = result.get("app.enabledFeatures").getAsPropertyArray();
		assertEquals(4, resultFeatures.size());
		
		PropertyArray resultMixed = result.get("app.mixedValues").getAsPropertyArray();
		assertEquals(4, resultMixed.size());
		assertEquals("string", resultMixed.get(0).getAsPropertyValue().getAsString());
		assertEquals(123, resultMixed.get(1).getAsPropertyValue().getAsNumber().intValue());
		assertTrue(resultMixed.get(2).getAsPropertyValue().getAsBoolean());
	}
	
	@Test
	void multiLineArraysRoundTrip() throws IOException {
		PropertyConfig multiLineConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			true, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyArray servers = new PropertyArray();
		servers.add(new PropertyValue("server1.example.com"));
		servers.add(new PropertyValue("server2.example.com"));
		servers.add(new PropertyValue("server3.example.com"));
		servers.add(new PropertyValue("server4.example.com"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), multiLineConfig)) {
			writer.writeMultiLineArray("cluster.servers", servers);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), multiLineConfig)) {
			result = reader.readProperties();
		}
		
		PropertyArray resultServers = result.get("cluster.servers").getAsPropertyArray();
		assertEquals(4, resultServers.size());
		assertEquals("server1.example.com", resultServers.get(0).getAsPropertyValue().getAsString());
		assertEquals("server2.example.com", resultServers.get(1).getAsPropertyValue().getAsString());
		assertEquals("server3.example.com", resultServers.get(2).getAsPropertyValue().getAsString());
		assertEquals("server4.example.com", resultServers.get(3).getAsPropertyValue().getAsString());
	}
	
	@Test
	void compactedKeysRoundTrip() throws IOException {
		String content = """
			app.[dev|prod].url = http://localhost
			app.[dev|prod].timeout = 30000
			db.[master|slave].host = localhost
			db.[master|slave].port = 5432
			""";
		
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		PropertyObject props;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			props = reader.readProperties();
		}
		
		assertEquals(8, props.size());
		
		assertEquals("http://localhost", props.get("app.dev.url").getAsPropertyValue().getAsString());
		assertEquals("http://localhost", props.get("app.prod.url").getAsPropertyValue().getAsString());
		assertEquals(30000, props.get("app.dev.timeout").getAsPropertyValue().getAsNumber().intValue());
		assertEquals(30000, props.get("app.prod.timeout").getAsPropertyValue().getAsNumber().intValue());
		
		assertEquals("localhost", props.get("db.master.host").getAsPropertyValue().getAsString());
		assertEquals("localhost", props.get("db.slave.host").getAsPropertyValue().getAsString());
		assertEquals(5432, props.get("db.master.port").getAsPropertyValue().getAsNumber().intValue());
		assertEquals(5432, props.get("db.slave.port").getAsPropertyValue().getAsNumber().intValue());
	}
	
	@Test
	void writeWithCompactionRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		config.add("app.dev.url", new PropertyValue("http://localhost"));
		config.add("app.prod.url", new PropertyValue("http://localhost"));
		config.add("app.dev.timeout", new PropertyValue(30000));
		config.add("app.prod.timeout", new PropertyValue(30000));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), COMPACTION_CONFIG)) {
			writer.write(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), COMPACTION_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("http://localhost", result.get("app.dev.url").getAsPropertyValue().getAsString());
		assertEquals("http://localhost", result.get("app.prod.url").getAsPropertyValue().getAsString());
	}
	
	@Test
	void largePropertySetRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		for (int i = 0; i < 100; i++) {
			String prefix = "item" + i;
			config.add(prefix + ".id", new PropertyValue(i));
			config.add(prefix + ".name", new PropertyValue("Item Number " + i));
			config.add(prefix + ".description", new PropertyValue("This is the description for item " + i));
			config.add(prefix + ".price", new PropertyValue(i * 10.5));
			config.add(prefix + ".quantity", new PropertyValue(i * 2));
			config.add(prefix + ".active", new PropertyValue(i % 3 != 0));
			config.add(prefix + ".category", new PropertyValue("category-" + (i % 5)));
			config.add(prefix + ".tags.primary", new PropertyValue("tag-" + (i % 10)));
			config.add(prefix + ".tags.secondary", new PropertyValue("group-" + (i % 3)));
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals(config.size(), result.size());
		
		for (int i = 0; i < 100; i++) {
			String prefix = "item" + i;
			assertEquals(i, result.get(prefix + ".id").getAsPropertyValue().getAsNumber().intValue());
			assertEquals("Item Number " + i, result.get(prefix + ".name").getAsPropertyValue().getAsString());
			assertEquals(i * 10.5, result.get(prefix + ".price").getAsPropertyValue().getAsNumber().doubleValue(), 0.001);
			assertEquals(i * 2, result.get(prefix + ".quantity").getAsPropertyValue().getAsNumber().intValue());
			assertEquals(i % 3 != 0, result.get(prefix + ".active").getAsPropertyValue().getAsBoolean());
		}
	}
	
	@Test
	void deeplyNestedGroupsRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("level1.name", new PropertyValue("level1"));
		config.add("level1.extra", new PropertyValue("level1 data"));
		config.add("level1.nested.name", new PropertyValue("level2"));
		config.add("level1.nested.extra", new PropertyValue("level2 data"));
		config.add("level1.nested.nested.name", new PropertyValue("level3"));
		config.add("level1.nested.nested.extra", new PropertyValue("level3 data"));
		config.add("level1.nested.nested.nested.name", new PropertyValue("level4"));
		config.add("level1.nested.nested.nested.extra", new PropertyValue("level4 data"));
		config.add("level1.nested.nested.nested.nested.name", new PropertyValue("level5"));
		config.add("level1.nested.nested.nested.nested.extra", new PropertyValue("level5 data"));
		config.add("level1.nested.nested.nested.nested.nested.name", new PropertyValue("level6"));
		config.add("level1.nested.nested.nested.nested.nested.value", new PropertyValue("deepest value"));
		config.add("level1.nested.nested.nested.nested.nested.number", new PropertyValue(12345));
		config.add("level1.nested.nested.nested.nested.nested.flag", new PropertyValue(true));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("level1 data", result.get("level1.extra").getAsPropertyValue().getAsString());
		assertEquals("level6", result.get("level1.nested.nested.nested.nested.nested.name").getAsPropertyValue().getAsString());
		assertEquals("deepest value", result.get("level1.nested.nested.nested.nested.nested.value").getAsPropertyValue().getAsString());
		assertEquals(12345, result.get("level1.nested.nested.nested.nested.nested.number").getAsPropertyValue().getAsNumber().intValue());
		assertTrue(result.get("level1.nested.nested.nested.nested.nested.flag").getAsPropertyValue().getAsBoolean());
	}
	
	@Test
	void nullValuesRoundTrip() throws IOException {
		PropertyConfig nullStringConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.NULL_STRING,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyObject config = new PropertyObject();
		config.add("app.name", new PropertyValue("MyApp"));
		config.add("app.description", PropertyNull.INSTANCE);
		config.add("app.version", new PropertyValue("1.0.0"));
		config.add("app.author", PropertyNull.INSTANCE);
		config.add("app.license", new PropertyValue("MIT"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), nullStringConfig)) {
			writer.write(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("null"));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), nullStringConfig)) {
			result = reader.readProperties();
		}
		
		assertEquals("MyApp", result.get("app.name").getAsPropertyValue().getAsString());
		assertTrue(result.get("app.description").isPropertyNull());
		assertEquals("1.0.0", result.get("app.version").getAsPropertyValue().getAsString());
		assertTrue(result.get("app.author").isPropertyNull());
		assertEquals("MIT", result.get("app.license").getAsPropertyValue().getAsString());
	}
	
	@Test
	void tildeNullStyleRoundTrip() throws IOException {
		PropertyConfig tildeConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.TILDE,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyObject config = new PropertyObject();
		config.add("key1", new PropertyValue("value1"));
		config.add("key2", PropertyNull.INSTANCE);
		config.add("key3", new PropertyValue("value3"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), tildeConfig)) {
			writer.write(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("~"));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), tildeConfig)) {
			result = reader.readProperties();
		}
		
		assertEquals("value1", result.get("key1").getAsPropertyValue().getAsString());
		assertTrue(result.get("key2").isPropertyNull());
		assertEquals("value3", result.get("key3").getAsPropertyValue().getAsString());
	}
	
	@Test
	void unicodeValuesRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("greeting.english", new PropertyValue("Hello, World!"));
		config.add("greeting.japanese", new PropertyValue("こんにちは世界"));
		config.add("greeting.korean", new PropertyValue("안녕하세요 세계"));
		config.add("greeting.chinese", new PropertyValue("你好世界"));
		config.add("greeting.arabic", new PropertyValue("مرحبا بالعالم"));
		config.add("greeting.russian", new PropertyValue("Привет мир"));
		config.add("greeting.emoji", new PropertyValue("Hello 👋🌍"));
		
		config.add("special.quotes", new PropertyValue("He said \"Hello\""));
		config.add("special.backslash", new PropertyValue("C:\\Users\\name"));
		config.add("special.newline", new PropertyValue("Line1\\nLine2"));
		config.add("special.tab", new PropertyValue("Col1\\tCol2"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("Hello, World!", result.get("greeting.english").getAsPropertyValue().getAsString());
		assertEquals("こんにちは世界", result.get("greeting.japanese").getAsPropertyValue().getAsString());
		assertEquals("안녕하세요 세계", result.get("greeting.korean").getAsPropertyValue().getAsString());
		assertEquals("你好世界", result.get("greeting.chinese").getAsPropertyValue().getAsString());
		assertEquals("مرحبا بالعالم", result.get("greeting.arabic").getAsPropertyValue().getAsString());
		assertEquals("Привет мир", result.get("greeting.russian").getAsPropertyValue().getAsString());
		assertEquals("Hello 👋🌍", result.get("greeting.emoji").getAsPropertyValue().getAsString());
	}
	
	@Test
	void commentsAreSkippedDuringRead() throws IOException {
		String content = """
			# This is a comment
			app.name = MyApp
			# Another comment
			app.version = 1.0.0
			# Hash comment
			app.author = Luis
			""";
		
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), DEFAULT_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals(3, result.size());
		assertEquals("MyApp", result.get("app.name").getAsPropertyValue().getAsString());
		assertEquals("1.0.0", result.get("app.version").getAsPropertyValue().getAsString());
		assertEquals("Luis", result.get("app.author").getAsPropertyValue().getAsString());
	}
	
	@Test
	void customSeparatorRoundTrip() throws IOException {
		PropertyConfig colonConfig = new PropertyConfig(
			':', 1, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			';', ":-",
			null
		);
		
		PropertyObject config = new PropertyObject();
		config.add("server.host", new PropertyValue("localhost"));
		config.add("server.port", new PropertyValue(8080));
		config.add("server.ssl.enabled", new PropertyValue(true));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), colonConfig)) {
			writer.write(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains(":"));
		assertFalse(written.contains("="));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), colonConfig)) {
			result = reader.readProperties();
		}
		
		assertEquals("localhost", result.get("server.host").getAsPropertyValue().getAsString());
		assertEquals(8080, result.get("server.port").getAsPropertyValue().getAsNumber().intValue());
		assertTrue(result.get("server.ssl.enabled").getAsPropertyValue().getAsBoolean());
	}
	
	@Test
	void variableKeysResolution() throws IOException {
		Map<String, String> customVars = new HashMap<>();
		customVars.put("env", "production");
		customVars.put("tier", "premium");
		
		PropertyConfig customVarsConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			true, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			customVars
		);
		
		// Variable resolution works in keys, not in values
		String content = """
			base.url = http://localhost
			app.${prop:env:-default}.url = http://production-server
			app.${prop:tier:-basic}.limit = 1000
			""";
		
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), customVarsConfig)) {
			result = reader.readProperties();
		}
		
		assertEquals("http://localhost", result.get("base.url").getAsPropertyValue().getAsString());
		// Variable in key ${prop:env} resolves to "production" using custom variable
		assertEquals("http://production-server", result.get("app.production.url").getAsPropertyValue().getAsString());
		// Variable in key ${prop:tier} resolves to "premium" using custom variable
		assertEquals(1000, result.get("app.premium.limit").getAsPropertyValue().getAsNumber().intValue());
	}
	
	@Test
	void tripleCompactedKeysResolution() throws IOException {
		String content = """
			config.[dev|staging|prod].server.host = localhost
			config.[dev|staging|prod].server.port = 8080
			""";
		
		ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals(6, result.size());
		
		assertEquals("localhost", result.get("config.dev.server.host").getAsPropertyValue().getAsString());
		assertEquals("localhost", result.get("config.staging.server.host").getAsPropertyValue().getAsString());
		assertEquals("localhost", result.get("config.prod.server.host").getAsPropertyValue().getAsString());
		
		assertEquals(8080, result.get("config.dev.server.port").getAsPropertyValue().getAsNumber().intValue());
		assertEquals(8080, result.get("config.staging.server.port").getAsPropertyValue().getAsNumber().intValue());
		assertEquals(8080, result.get("config.prod.server.port").getAsPropertyValue().getAsNumber().intValue());
	}
	
	@Test
	void realWorldKubernetesConfigRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("kubernetes.namespace", new PropertyValue("production"));
		config.add("kubernetes.deployment.replicas", new PropertyValue(3));
		config.add("kubernetes.deployment.image", new PropertyValue("my-registry.io/my-app:v1.0.0"));
		config.add("kubernetes.deployment.imagePullPolicy", new PropertyValue("Always"));
		
		PropertyArray containerPorts = new PropertyArray();
		containerPorts.add(new PropertyValue(8080));
		containerPorts.add(new PropertyValue(8443));
		config.add("kubernetes.deployment.ports", containerPorts);
		
		config.add("kubernetes.resources.requests.cpu", new PropertyValue("250m"));
		config.add("kubernetes.resources.requests.memory", new PropertyValue("256Mi"));
		config.add("kubernetes.resources.limits.cpu", new PropertyValue("500m"));
		config.add("kubernetes.resources.limits.memory", new PropertyValue("512Mi"));
		
		config.add("kubernetes.probes.liveness.path", new PropertyValue("/health/live"));
		config.add("kubernetes.probes.liveness.port", new PropertyValue(8080));
		config.add("kubernetes.probes.liveness.initialDelaySeconds", new PropertyValue(30));
		config.add("kubernetes.probes.readiness.path", new PropertyValue("/health/ready"));
		config.add("kubernetes.probes.readiness.port", new PropertyValue(8080));
		config.add("kubernetes.probes.readiness.initialDelaySeconds", new PropertyValue(10));
		
		config.add("kubernetes.env.JAVA_OPTS", new PropertyValue("-Xmx512m -Xms256m"));
		config.add("kubernetes.env.SPRING_PROFILES_ACTIVE", new PropertyValue("production"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("production", result.get("kubernetes.namespace").getAsPropertyValue().getAsString());
		assertEquals(3, result.get("kubernetes.deployment.replicas").getAsPropertyValue().getAsNumber().intValue());
		assertEquals("my-registry.io/my-app:v1.0.0", result.get("kubernetes.deployment.image").getAsPropertyValue().getAsString());
		
		PropertyArray resultPorts = result.get("kubernetes.deployment.ports").getAsPropertyArray();
		assertEquals(2, resultPorts.size());
		
		assertEquals("250m", result.get("kubernetes.resources.requests.cpu").getAsPropertyValue().getAsString());
		assertEquals("512Mi", result.get("kubernetes.resources.limits.memory").getAsPropertyValue().getAsString());
		
		assertEquals("/health/live", result.get("kubernetes.probes.liveness.path").getAsPropertyValue().getAsString());
		assertEquals(30, result.get("kubernetes.probes.liveness.initialDelaySeconds").getAsPropertyValue().getAsNumber().intValue());
	}
	
	@Test
	void realWorldDockerComposeConfigRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		
		config.add("compose.version", new PropertyValue("3.8"));
		
		config.add("services.web.image", new PropertyValue("nginx:latest"));
		config.add("services.web.container_name", new PropertyValue("web-server"));
		config.add("services.web.restart", new PropertyValue("unless-stopped"));
		
		PropertyArray webPorts = new PropertyArray();
		webPorts.add(new PropertyValue("80:80"));
		webPorts.add(new PropertyValue("443:443"));
		config.add("services.web.ports", webPorts);
		
		PropertyArray webVolumes = new PropertyArray();
		webVolumes.add(new PropertyValue("./nginx.conf:/etc/nginx/nginx.conf:ro"));
		webVolumes.add(new PropertyValue("./html:/usr/share/nginx/html:ro"));
		config.add("services.web.volumes", webVolumes);
		
		config.add("services.app.build", new PropertyValue("."));
		config.add("services.app.container_name", new PropertyValue("application"));
		config.add("services.app.restart", new PropertyValue("unless-stopped"));
		config.add("services.app.environment.SPRING_PROFILES_ACTIVE", new PropertyValue("docker"));
		config.add("services.app.environment.DATABASE_HOST", new PropertyValue("db"));
		config.add("services.app.environment.DATABASE_PORT", new PropertyValue("5432"));
		
		config.add("services.db.image", new PropertyValue("postgres:15"));
		config.add("services.db.container_name", new PropertyValue("database"));
		config.add("services.db.restart", new PropertyValue("unless-stopped"));
		config.add("services.db.environment.POSTGRES_USER", new PropertyValue("myapp"));
		config.add("services.db.environment.POSTGRES_PASSWORD", new PropertyValue("secret"));
		config.add("services.db.environment.POSTGRES_DB", new PropertyValue("myapp_db"));
		
		config.add("networks.frontend.driver", new PropertyValue("bridge"));
		config.add("networks.backend.driver", new PropertyValue("bridge"));
		
		config.add("volumes.postgres_data.driver", new PropertyValue("local"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertEquals("3.8", result.get("compose.version").getAsPropertyValue().getAsString());
		assertEquals("nginx:latest", result.get("services.web.image").getAsPropertyValue().getAsString());
		assertEquals("unless-stopped", result.get("services.web.restart").getAsPropertyValue().getAsString());
		
		PropertyArray resultPorts = result.get("services.web.ports").getAsPropertyArray();
		assertEquals(2, resultPorts.size());
		assertEquals("80:80", resultPorts.get(0).getAsPropertyValue().getAsString());
		
		assertEquals("postgres:15", result.get("services.db.image").getAsPropertyValue().getAsString());
		assertEquals("myapp_db", result.get("services.db.environment.POSTGRES_DB").getAsPropertyValue().getAsString());
	}
	
	@Test
	void emptyArrayRoundTrip() throws IOException {
		PropertyObject config = new PropertyObject();
		config.add("emptyArray", new PropertyArray());
		config.add("filledKey", new PropertyValue("value"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), DEFAULT_CONFIG)) {
			writer.write(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), ADVANCED_CONFIG)) {
			result = reader.readProperties();
		}
		
		assertTrue(result.containsKey("emptyArray"));
		PropertyArray emptyResult = result.get("emptyArray").getAsPropertyArray();
		assertTrue(emptyResult.isEmpty());
		assertEquals("value", result.get("filledKey").getAsPropertyValue().getAsString());
	}
	
	@Test
	void alignmentPreservationRoundTrip() throws IOException {
		PropertyConfig alignedConfig = new PropertyConfig(
			'=', 4, Set.of('#'),
			KEY_PATTERN, VALUE_PATTERN,
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyObject config = new PropertyObject();
		config.add("short", new PropertyValue("value"));
		config.add("medium.key", new PropertyValue("value"));
		config.add("very.long.nested.key.path", new PropertyValue("value"));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(output), alignedConfig)) {
			writer.write(config);
		}
		
		String written = output.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("    =    "));
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		PropertyObject result;
		try (PropertyReader reader = new PropertyReader(new InputProvider(input), alignedConfig)) {
			result = reader.readProperties();
		}
		
		assertEquals(3, result.size());
		assertEquals("value", result.get("short").getAsPropertyValue().getAsString());
		assertEquals("value", result.get("medium.key").getAsPropertyValue().getAsString());
		assertEquals("value", result.get("very.long.nested.key.path").getAsPropertyValue().getAsString());
	}
}
