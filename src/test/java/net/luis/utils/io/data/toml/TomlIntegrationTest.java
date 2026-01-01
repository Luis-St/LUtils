/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TOML read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class TomlIntegrationTest {
	
	@Test
	void applicationConfigRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "My Application")
			.add("version", "1.0.0")
			.add("debug", false)
			.startTable("database")
			.add("host", "localhost")
			.add("port", 5432)
			.add("name", "myapp_db")
			.add("username", "admin")
			.add("maxConnections", 20)
			.add("timeout", 30000)
			.endTable()
			.startTable("logging")
			.add("level", "INFO")
			.add("file", "/var/log/myapp/application.log")
			.add("console", true)
			.add("maxFileSize", "100MB")
			.add("maxHistory", 30)
			.endTable()
			.startTable("features")
			.add("enableCache", true)
			.add("enableMetrics", true)
			.add("enableTracing", false)
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		assertEquals("My Application", result.getAsString("title"));
		assertEquals("1.0.0", result.getAsString("version"));
		assertFalse(result.getAsBoolean("debug"));
		
		TomlTable database = result.getTomlTable("database");
		assertEquals("localhost", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
		assertEquals("myapp_db", database.getAsString("name"));
		assertEquals(20, database.getAsInteger("maxConnections"));
		
		TomlTable logging = result.getTomlTable("logging");
		assertEquals("INFO", logging.getAsString("level"));
		assertTrue(logging.getAsBoolean("console"));
		
		TomlTable features = result.getTomlTable("features");
		assertTrue(features.getAsBoolean("enableCache"));
		assertFalse(features.getAsBoolean("enableTracing"));
	}
	
	@Test
	void serverConfigWithArraysRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.startTable("server")
			.add("host", "0.0.0.0")
			.add("port", 8080)
			.add("ssl", true)
			.add("allowedOrigins", TomlBuilder.array()
				.add("https://example.com")
				.add("https://api.example.com")
				.add("https://admin.example.com")
				.build())
			.endTable()
			.startTable("rateLimit")
			.add("enabled", true)
			.add("requestsPerMinute", 100)
			.add("whitelist", TomlBuilder.array()
				.add("192.168.1.0/24")
				.add("10.0.0.0/8")
				.build())
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		TomlTable server = result.getTomlTable("server");
		assertEquals("0.0.0.0", server.getAsString("host"));
		assertEquals(8080, server.getAsInteger("port"));
		assertTrue(server.getAsBoolean("ssl"));
		
		TomlArray origins = server.getTomlArray("allowedOrigins");
		assertEquals(3, origins.size());
		assertEquals("https://example.com", origins.getAsString(0));
		assertEquals("https://admin.example.com", origins.getAsString(2));
		
		TomlTable rateLimit = result.getTomlTable("rateLimit");
		assertTrue(rateLimit.getAsBoolean("enabled"));
		assertEquals(100, rateLimit.getAsInteger("requestsPerMinute"));
		
		TomlArray whitelist = rateLimit.getTomlArray("whitelist");
		assertEquals(2, whitelist.size());
	}
	
	@Test
	void dateTimeConfigRoundTrip() throws IOException {
		LocalDate releaseDate = LocalDate.of(2024, 6, 15);
		LocalTime maintenanceTime = LocalTime.of(3, 30, 45);
		LocalDateTime lastUpdate = LocalDateTime.of(2024, 1, 25, 14, 30, 15);
		OffsetDateTime createdAt = OffsetDateTime.of(2023, 1, 1, 12, 0, 30, 0, ZoneOffset.UTC);
		
		TomlTable config = TomlBuilder.table()
			.add("releaseDate", releaseDate)
			.add("maintenanceTime", maintenanceTime)
			.add("lastUpdate", lastUpdate)
			.add("createdAt", createdAt)
			.startTable("schedule")
			.add("dailyBackup", LocalTime.of(2, 15, 30))
			.add("weeklyMaintenance", LocalDate.of(2024, 1, 7))
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		assertEquals(releaseDate, result.getAsLocalDate("releaseDate"));
		assertEquals(maintenanceTime, result.getAsLocalTime("maintenanceTime"));
		assertEquals(lastUpdate, result.getAsLocalDateTime("lastUpdate"));
		assertEquals(createdAt, result.getAsOffsetDateTime("createdAt"));
		
		TomlTable schedule = result.getTomlTable("schedule");
		assertEquals(LocalTime.of(2, 15, 30), schedule.getAsLocalTime("dailyBackup"));
	}
	
	@Test
	void nestedTablesRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.startTable("level1")
			.add("name", "Level 1")
			.startTable("level2")
			.add("name", "Level 2")
			.startTable("level3")
			.add("name", "Level 3")
			.add("value", "deep value")
			.add("number", 12345)
			.endTable()
			.endTable()
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		TomlTable level1 = result.getTomlTable("level1");
		assertEquals("Level 1", level1.getAsString("name"));
		
		TomlTable level2 = level1.getTomlTable("level2");
		assertEquals("Level 2", level2.getAsString("name"));
		
		TomlTable level3 = level2.getTomlTable("level3");
		assertEquals("Level 3", level3.getAsString("name"));
		assertEquals("deep value", level3.getAsString("value"));
		assertEquals(12345, level3.getAsInteger("number"));
	}
	
	@Test
	void inlineTablesRoundTrip() throws IOException {
		TomlTable point1 = new TomlTable();
		point1.setInline(true);
		point1.add("x", new TomlValue(10));
		point1.add("y", new TomlValue(20));
		
		TomlTable point2 = new TomlTable();
		point2.setInline(true);
		point2.add("x", new TomlValue(30));
		point2.add("y", new TomlValue(40));
		
		TomlTable config = new TomlTable();
		config.add("origin", point1);
		config.add("destination", point2);
		config.add("distance", new TomlValue(28.28));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		TomlTable origin = result.getTomlTable("origin");
		assertEquals(10, origin.getAsInteger("x"));
		assertEquals(20, origin.getAsInteger("y"));
		
		TomlTable destination = result.getTomlTable("destination");
		assertEquals(30, destination.getAsInteger("x"));
		assertEquals(40, destination.getAsInteger("y"));
		
		assertEquals(28.28, result.getAsDouble("distance"), 0.01);
	}
	
	@Test
	void mixedArraysRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("integers", TomlBuilder.array()
				.add(1)
				.add(2)
				.add(3)
				.add(4)
				.add(5)
				.build())
			.add("floats", TomlBuilder.array()
				.add(1.1)
				.add(2.2)
				.add(3.3)
				.build())
			.add("strings", TomlBuilder.array()
				.add("one")
				.add("two")
				.add("three")
				.build())
			.add("booleans", TomlBuilder.array()
				.add(true)
				.add(false)
				.add(true)
				.build())
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		TomlArray integers = result.getTomlArray("integers");
		assertEquals(5, integers.size());
		assertEquals(1, integers.getAsInteger(0));
		assertEquals(5, integers.getAsInteger(4));
		
		TomlArray floats = result.getTomlArray("floats");
		assertEquals(3, floats.size());
		assertEquals(1.1, floats.getAsDouble(0), 0.01);
		
		TomlArray strings = result.getTomlArray("strings");
		assertEquals(3, strings.size());
		assertEquals("one", strings.getAsString(0));
		assertEquals("three", strings.getAsString(2));
		
		TomlArray booleans = result.getTomlArray("booleans");
		assertEquals(3, booleans.size());
		assertTrue(booleans.getAsBoolean(0));
		assertFalse(booleans.getAsBoolean(1));
	}
	
	@Test
	void databaseConfigRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "Database Configuration")
			.startTable("primary")
			.add("driver", "postgresql")
			.add("host", "db-primary.internal")
			.add("port", 5432)
			.add("database", "production")
			.add("username", "app_user")
			.startTable("pool")
			.add("min", 5)
			.add("max", 50)
			.add("idleTimeout", 600)
			.endTable()
			.endTable()
			.startTable("replica")
			.add("driver", "postgresql")
			.add("host", "db-replica.internal")
			.add("port", 5432)
			.add("database", "production")
			.add("username", "readonly_user")
			.add("readOnly", true)
			.endTable()
			.startTable("cache")
			.add("driver", "redis")
			.add("host", "redis.internal")
			.add("port", 6379)
			.add("database", 0)
			.add("ttl", 300)
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		assertEquals("Database Configuration", result.getAsString("title"));
		
		TomlTable primary = result.getTomlTable("primary");
		assertEquals("postgresql", primary.getAsString("driver"));
		assertEquals("db-primary.internal", primary.getAsString("host"));
		
		TomlTable pool = primary.getTomlTable("pool");
		assertEquals(5, pool.getAsInteger("min"));
		assertEquals(50, pool.getAsInteger("max"));
		assertEquals(600, pool.getAsInteger("idleTimeout"));
		
		TomlTable replica = result.getTomlTable("replica");
		assertTrue(replica.getAsBoolean("readOnly"));
		
		TomlTable cache = result.getTomlTable("cache");
		assertEquals("redis", cache.getAsString("driver"));
		assertEquals(6379, cache.getAsInteger("port"));
	}
	
	@Test
	void gameConfigRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "Game Settings")
			.startTable("graphics")
			.add("resolution", "1920x1080")
			.add("fullscreen", true)
			.add("vsync", true)
			.add("fpsLimit", 60)
			.add("quality", "high")
			.add("antialiasing", 4)
			.endTable()
			.startTable("audio")
			.add("masterVolume", 80)
			.add("musicVolume", 70)
			.add("sfxVolume", 90)
			.add("muteOnFocusLoss", true)
			.endTable()
			.startTable("controls")
			.add("mouseSensitivity", 50.5)
			.add("invertY", false)
			.startTable("keybindings")
			.add("forward", "W")
			.add("backward", "S")
			.add("left", "A")
			.add("right", "D")
			.add("jump", "SPACE")
			.endTable()
			.endTable()
			.build();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}
		
		assertEquals("Game Settings", result.getAsString("title"));
		
		TomlTable graphics = result.getTomlTable("graphics");
		assertEquals("1920x1080", graphics.getAsString("resolution"));
		assertTrue(graphics.getAsBoolean("fullscreen"));
		assertEquals(60, graphics.getAsInteger("fpsLimit"));
		
		TomlTable audio = result.getTomlTable("audio");
		assertEquals(80, audio.getAsInteger("masterVolume"));
		assertTrue(audio.getAsBoolean("muteOnFocusLoss"));
		
		TomlTable controls = result.getTomlTable("controls");
		assertEquals(50.5, controls.getAsDouble("mouseSensitivity"), 0.01);
		assertFalse(controls.getAsBoolean("invertY"));
		
		TomlTable keybindings = controls.getTomlTable("keybindings");
		assertEquals("W", keybindings.getAsString("forward"));
		assertEquals("SPACE", keybindings.getAsString("jump"));
	}
	
	@Test
	void builderGeneratedMatchesManual() throws IOException {
		TomlTable builderTable = TomlBuilder.table()
			.add("name", "Test")
			.add("version", 1)
			.startTable("section1")
			.add("key1", "value1")
			.add("key2", 42)
			.endTable()
			.build();
		
		TomlTable manualTable = new TomlTable();
		manualTable.add("name", new TomlValue("Test"));
		manualTable.add("version", new TomlValue(1));
		TomlTable section1 = new TomlTable();
		section1.add("key1", new TomlValue("value1"));
		section1.add("key2", new TomlValue(42));
		manualTable.add("section1", section1);
		
		ByteArrayOutputStream builderOutput = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(builderOutput))) {
			writer.writeToml(builderTable);
		}
		
		ByteArrayOutputStream manualOutput = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(manualOutput))) {
			writer.writeToml(manualTable);
		}
		
		assertEquals(
			builderOutput.toString(StandardCharsets.UTF_8),
			manualOutput.toString(StandardCharsets.UTF_8)
		);
	}

	@Test
	void largeDataSetRoundTrip() throws IOException {
		TomlTable data = new TomlTable();

		TomlArray ids = new TomlArray();
		TomlArray names = new TomlArray();
		TomlArray prices = new TomlArray();
		TomlArray actives = new TomlArray();

		for (int i = 0; i < 100; i++) {
			ids.add(new TomlValue(i));
			names.add(new TomlValue("Item Number " + i));
			prices.add(new TomlValue(i * 10.5));
			actives.add(new TomlValue(i % 3 != 0));
		}

		TomlTable items = new TomlTable();
		items.add("ids", ids);
		items.add("names", names);
		items.add("prices", prices);
		items.add("actives", actives);
		data.add("items", items);

		TomlTable summary = new TomlTable();
		summary.add("count", new TomlValue(100));
		summary.add("activeCount", new TomlValue(67));
		summary.add("totalValue", new TomlValue(49725.0));
		data.add("summary", summary);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(data);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		TomlTable resultItems = result.getTomlTable("items");
		TomlArray resultIds = resultItems.getTomlArray("ids");
		TomlArray resultNames = resultItems.getTomlArray("names");
		TomlArray resultPrices = resultItems.getTomlArray("prices");
		TomlArray resultActives = resultItems.getTomlArray("actives");

		assertEquals(100, resultIds.size());
		assertEquals(100, resultNames.size());
		assertEquals(100, resultPrices.size());
		assertEquals(100, resultActives.size());

		for (int i = 0; i < 100; i++) {
			assertEquals(i, resultIds.getAsInteger(i));
			assertEquals("Item Number " + i, resultNames.getAsString(i));
			assertEquals(i * 10.5, resultPrices.getAsDouble(i), 0.001);
			assertEquals(i % 3 != 0, resultActives.getAsBoolean(i));
		}

		TomlTable resultSummary = result.getTomlTable("summary");
		assertEquals(100, resultSummary.getAsInteger("count"));
		assertEquals(67, resultSummary.getAsInteger("activeCount"));
		assertEquals(49725.0, resultSummary.getAsDouble("totalValue"), 0.001);
	}

	@Test
	void deeplyNestedConfigurationRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.startTable("level1")
			.add("name", "Level 1")
			.add("extra", "level1 data")
			.startTable("level2")
			.add("name", "Level 2")
			.add("extra", "level2 data")
			.startTable("level3")
			.add("name", "Level 3")
			.add("extra", "level3 data")
			.startTable("level4")
			.add("name", "Level 4")
			.add("extra", "level4 data")
			.startTable("level5")
			.add("name", "Level 5")
			.add("extra", "level5 data")
			.startTable("level6")
			.add("name", "Level 6")
			.add("value", "deepest value")
			.add("number", 12345)
			.add("flag", true)
			.add("items", TomlBuilder.array()
				.add("a")
				.add("b")
				.add("c")
				.build())
			.endTable()
			.endTable()
			.endTable()
			.endTable()
			.endTable()
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("level1 data", result.getTomlTable("level1").getAsString("extra"));

		TomlTable deepest = result
			.getTomlTable("level1")
			.getTomlTable("level2")
			.getTomlTable("level3")
			.getTomlTable("level4")
			.getTomlTable("level5")
			.getTomlTable("level6");

		assertEquals("Level 6", deepest.getAsString("name"));
		assertEquals("deepest value", deepest.getAsString("value"));
		assertEquals(12345, deepest.getAsInteger("number"));
		assertTrue(deepest.getAsBoolean("flag"));
		assertEquals(3, deepest.getTomlArray("items").size());
		assertEquals("a", deepest.getTomlArray("items").getAsString(0));
	}

	@Test
	void unicodeValuesRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("english", "Hello, World!")
			.add("japanese", "こんにちは世界")
			.add("korean", "안녕하세요 세계")
			.add("chinese", "你好世界")
			.add("arabic", "مرحبا بالعالم")
			.add("russian", "Привет мир")
			.add("emoji", "Hello 👋🌍")
			.startTable("special")
			.add("math", "∑∏∫∂")
			.add("currency", "€£¥₹")
			.add("quotes", "He said \"Hello\"")
			.add("backslash", "C:\\Users\\name")
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("Hello, World!", result.getAsString("english"));
		assertEquals("こんにちは世界", result.getAsString("japanese"));
		assertEquals("안녕하세요 세계", result.getAsString("korean"));
		assertEquals("你好世界", result.getAsString("chinese"));
		assertEquals("مرحبا بالعالم", result.getAsString("arabic"));
		assertEquals("Привет мир", result.getAsString("russian"));
		assertEquals("Hello 👋🌍", result.getAsString("emoji"));

		TomlTable special = result.getTomlTable("special");
		assertEquals("∑∏∫∂", special.getAsString("math"));
		assertEquals("€£¥₹", special.getAsString("currency"));
		assertEquals("He said \"Hello\"", special.getAsString("quotes"));
		assertEquals("C:\\Users\\name", special.getAsString("backslash"));
	}

	@Test
	void floatingPointValuesRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("pi", 3.14159265359)
			.add("e", 2.71828182845)
			.add("negativeFloat", -123.456)
			.add("largeFloat", 1.23e10)
			.add("smallFloat", 1.23e-10)
			.startTable("physics")
			.add("speedOfLight", 299792458)
			.add("gravitationalConstant", 6.67430e-11)
			.add("planckConstant", 6.62607015e-34)
			.endTable()
			.startTable("prices")
			.add("item1", 19.99)
			.add("item2", 149.95)
			.add("discount", 0.15)
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals(3.14159265359, result.getAsDouble("pi"), 0.0000001);
		assertEquals(2.71828182845, result.getAsDouble("e"), 0.0000001);
		assertEquals(-123.456, result.getAsDouble("negativeFloat"), 0.001);
		assertEquals(1.23e10, result.getAsDouble("largeFloat"), 1e5);
		assertEquals(1.23e-10, result.getAsDouble("smallFloat"), 1e-15);

		TomlTable physics = result.getTomlTable("physics");
		assertEquals(299792458, physics.getAsLong("speedOfLight"));
		assertEquals(6.67430e-11, physics.getAsDouble("gravitationalConstant"), 1e-20);
		assertEquals(6.62607015e-34, physics.getAsDouble("planckConstant"), 1e-43);

		TomlTable prices = result.getTomlTable("prices");
		assertEquals(19.99, prices.getAsDouble("item1"), 0.001);
		assertEquals(149.95, prices.getAsDouble("item2"), 0.001);
		assertEquals(0.15, prices.getAsDouble("discount"), 0.001);
	}

	@Test
	void emptyStringValuesRoundTrip() throws IOException {
		TomlTable config = new TomlTable();
		config.add("name", new TomlValue("MyApp"));
		config.add("description", new TomlValue(""));
		config.add("version", new TomlValue("1.0.0"));
		config.add("author", new TomlValue(""));
		config.add("license", new TomlValue("MIT"));

		TomlTable settings = new TomlTable();
		settings.add("theme", new TomlValue("dark"));
		settings.add("customCss", new TomlValue(""));
		settings.add("fontSize", new TomlValue(14));
		config.add("settings", settings);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("MyApp", result.getAsString("name"));
		assertEquals("", result.getAsString("description"));
		assertEquals("1.0.0", result.getAsString("version"));
		assertEquals("", result.getAsString("author"));
		assertEquals("MIT", result.getAsString("license"));

		TomlTable resultSettings = result.getTomlTable("settings");
		assertEquals("dark", resultSettings.getAsString("theme"));
		assertEquals("", resultSettings.getAsString("customCss"));
		assertEquals(14, resultSettings.getAsInteger("fontSize"));
	}

	@Test
	void productCatalogRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "Product Catalog")
			.startTable("product1")
			.add("name", "Widget A")
			.add("price", 29.99)
			.add("stock", 100)
			.add("tags", TomlBuilder.array().add("electronics").add("gadget").build())
			.endTable()
			.startTable("product2")
			.add("name", "Widget B")
			.add("price", 49.99)
			.add("stock", 50)
			.add("tags", TomlBuilder.array().add("electronics").add("premium").build())
			.endTable()
			.startTable("product3")
			.add("name", "Widget C")
			.add("price", 9.99)
			.add("stock", 500)
			.add("tags", TomlBuilder.array().add("accessories").build())
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("Product Catalog", result.getAsString("title"));

		TomlTable resultProduct1 = result.getTomlTable("product1");
		assertEquals("Widget A", resultProduct1.getAsString("name"));
		assertEquals(29.99, resultProduct1.getAsDouble("price"), 0.01);
		assertEquals(100, resultProduct1.getAsInteger("stock"));
		assertEquals(2, resultProduct1.getTomlArray("tags").size());

		TomlTable resultProduct2 = result.getTomlTable("product2");
		assertEquals("Widget B", resultProduct2.getAsString("name"));
		assertEquals(49.99, resultProduct2.getAsDouble("price"), 0.01);

		TomlTable resultProduct3 = result.getTomlTable("product3");
		assertEquals("Widget C", resultProduct3.getAsString("name"));
		assertEquals(500, resultProduct3.getAsInteger("stock"));
	}

	@Test
	void compactConfigRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "Compact Test")
			.startTable("server")
			.add("host", "localhost")
			.add("port", 8080)
			.add("ssl", true)
			.endTable()
			.startTable("database")
			.add("host", "db.example.com")
			.add("port", 5432)
			.add("name", "mydb")
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), TomlConfig.COMPACT)) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("Compact Test", result.getAsString("title"));

		TomlTable server = result.getTomlTable("server");
		assertEquals("localhost", server.getAsString("host"));
		assertEquals(8080, server.getAsInteger("port"));
		assertTrue(server.getAsBoolean("ssl"));

		TomlTable database = result.getTomlTable("database");
		assertEquals("db.example.com", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
		assertEquals("mydb", database.getAsString("name"));
	}

	@Test
	void multiLineStringRoundTrip() throws IOException {
		String longText = """
			This is a very long string that spans multiple lines.
			It contains several sentences and should be formatted
			as a multi-line string when the appropriate configuration
			is used. This helps improve readability of the TOML file.""";

		TomlTable config = new TomlTable();
		config.add("description", new TomlValue(longText));
		config.add("shortText", new TomlValue("Short"));

		TomlConfig multiLineConfig = new TomlConfig(
			true, true, "  ", false, 3, true, 10,
			true, 50, true, TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), multiLineConfig)) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals(longText, result.getAsString("description"));
		assertEquals("Short", result.getAsString("shortText"));
	}

	@Test
	void emptyTableRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("title", "Test")
			.startTable("empty1")
			.endTable()
			.startTable("filled")
			.add("key", "value")
			.endTable()
			.startTable("empty2")
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("Test", result.getAsString("title"));

		assertTrue(result.containsKey("empty1"));
		assertTrue(result.getTomlTable("empty1").isEmpty());

		TomlTable filled = result.getTomlTable("filled");
		assertEquals(1, filled.size());
		assertEquals("value", filled.getAsString("key"));

		assertTrue(result.containsKey("empty2"));
		assertTrue(result.getTomlTable("empty2").isEmpty());
	}

	@Test
	void userProfileWithActivityRoundTrip() throws IOException {
		TomlTable user = TomlBuilder.table()
			.startTable("profile")
			.add("id", "USR-789")
			.add("username", "johndoe")
			.add("email", "john.doe@example.com")
			.add("displayName", "John Doe")
			.add("verified", true)
			.add("createdAt", OffsetDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC))
			.startTable("preferences")
			.add("theme", "dark")
			.add("language", "en-US")
			.add("timezone", "America/New_York")
			.startTable("notifications")
			.add("email", true)
			.add("push", true)
			.add("sms", false)
			.endTable()
			.endTable()
			.endTable()
			.startTable("activity1")
			.add("type", "login")
			.add("timestamp", OffsetDateTime.of(2024, 1, 25, 16, 45, 0, 0, ZoneOffset.UTC))
			.add("ip", "192.168.1.100")
			.add("device", "Chrome on Windows")
			.endTable()
			.startTable("activity2")
			.add("type", "purchase")
			.add("timestamp", OffsetDateTime.of(2024, 1, 24, 10, 30, 0, 0, ZoneOffset.UTC))
			.add("orderId", "ORD-12345")
			.add("amount", 149.99)
			.endTable()
			.add("roles", TomlBuilder.array().add("user").add("premium").build())
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(user);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		TomlTable profile = result.getTomlTable("profile");
		assertEquals("johndoe", profile.getAsString("username"));
		assertEquals("john.doe@example.com", profile.getAsString("email"));
		assertTrue(profile.getAsBoolean("verified"));

		TomlTable preferences = profile.getTomlTable("preferences");
		assertEquals("dark", preferences.getAsString("theme"));
		assertEquals("en-US", preferences.getAsString("language"));

		TomlTable notifications = preferences.getTomlTable("notifications");
		assertTrue(notifications.getAsBoolean("email"));
		assertTrue(notifications.getAsBoolean("push"));
		assertFalse(notifications.getAsBoolean("sms"));

		TomlTable activity1 = result.getTomlTable("activity1");
		assertEquals("login", activity1.getAsString("type"));
		assertEquals("192.168.1.100", activity1.getAsString("ip"));

		TomlTable activity2 = result.getTomlTable("activity2");
		assertEquals("purchase", activity2.getAsString("type"));
		assertEquals("ORD-12345", activity2.getAsString("orderId"));
		assertEquals(149.99, activity2.getAsDouble("amount"), 0.01);

		TomlArray resultRoles = result.getTomlArray("roles");
		assertEquals(2, resultRoles.size());
		assertEquals("user", resultRoles.getAsString(0));
		assertEquals("premium", resultRoles.getAsString(1));
	}

	@Test
	void dateTimeStylesRoundTrip() throws IOException {
		LocalDate date = LocalDate.of(2024, 6, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime localDateTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 6, 15, 14, 30, 45, 0, ZoneOffset.ofHours(2));

		TomlTable config = TomlBuilder.table()
			.add("date", date)
			.add("time", time)
			.add("localDateTime", localDateTime)
			.add("offsetDateTime", offsetDateTime)
			.build();

		// RFC_3339 is the standard TOML format that preserves all date/time types
		TomlConfig testConfig = new TomlConfig(
			true, true, "  ", false, 3, true, 10,
			false, 80, true, TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), testConfig)) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals(date, result.getAsLocalDate("date"));
		assertEquals(time, result.getAsLocalTime("time"));
		assertEquals(localDateTime, result.getAsLocalDateTime("localDateTime"));
		assertEquals(offsetDateTime, result.getAsOffsetDateTime("offsetDateTime"));
	}

	@Test
	void organizationStructureRoundTrip() throws IOException {
		TomlTable config = TomlBuilder.table()
			.add("name", "Organization")
			.startTable("engineering")
			.add("name", "Engineering")
			.startTable("backend")
			.add("name", "Backend")
			.add("size", 5)
			.add("members", TomlBuilder.array().add("Alice").add("Bob").add("Charlie").add("David").add("Eve").build())
			.endTable()
			.startTable("frontend")
			.add("name", "Frontend")
			.add("size", 4)
			.add("members", TomlBuilder.array().add("Frank").add("Grace").add("Henry").add("Ivy").build())
			.endTable()
			.endTable()
			.startTable("marketing")
			.add("name", "Marketing")
			.startTable("digital")
			.add("name", "Digital")
			.add("size", 3)
			.add("members", TomlBuilder.array().add("Jack").add("Kate").add("Leo").build())
			.endTable()
			.endTable()
			.build();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		TomlTable result;
		try (TomlReader reader = new TomlReader(new InputProvider(input))) {
			result = reader.readToml();
		}

		assertEquals("Organization", result.getAsString("name"));

		TomlTable engineering = result.getTomlTable("engineering");
		assertEquals("Engineering", engineering.getAsString("name"));

		TomlTable backend = engineering.getTomlTable("backend");
		assertEquals("Backend", backend.getAsString("name"));
		assertEquals(5, backend.getAsInteger("size"));
		assertEquals(5, backend.getTomlArray("members").size());
		assertEquals("Alice", backend.getTomlArray("members").getAsString(0));

		TomlTable frontend = engineering.getTomlTable("frontend");
		assertEquals("Frontend", frontend.getAsString("name"));
		assertEquals(4, frontend.getAsInteger("size"));

		TomlTable marketing = result.getTomlTable("marketing");
		assertEquals("Marketing", marketing.getAsString("name"));

		TomlTable digital = marketing.getTomlTable("digital");
		assertEquals("Digital", digital.getAsString("name"));
		assertEquals(3, digital.getAsInteger("size"));
		assertEquals(3, digital.getTomlArray("members").size());
	}
}
