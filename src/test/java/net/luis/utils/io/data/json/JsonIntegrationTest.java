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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JSON read/write operations with complex structures.<br>
 *
 * @author Luis-St
 */
class JsonIntegrationTest {

	@Test
	void applicationConfigRoundTrip() throws IOException {
		JsonObject config = new JsonObject();

		JsonObject database = new JsonObject();
		database.add("host", new JsonPrimitive("localhost"));
		database.add("port", new JsonPrimitive(5432));
		database.add("name", new JsonPrimitive("myapp_db"));
		database.add("username", new JsonPrimitive("admin"));
		database.add("password", new JsonPrimitive("secret123"));
		JsonObject pool = new JsonObject();
		pool.add("minSize", new JsonPrimitive(5));
		pool.add("maxSize", new JsonPrimitive(20));
		pool.add("timeout", new JsonPrimitive(30000));
		pool.add("idleTimeout", new JsonPrimitive(600000));
		database.add("pool", pool);
		config.add("database", database);

		JsonObject logging = new JsonObject();
		logging.add("level", new JsonPrimitive("INFO"));
		logging.add("format", new JsonPrimitive("[%d{yyyy-MM-dd HH:mm:ss}] [%level] %logger - %msg%n"));
		JsonArray appenders = new JsonArray();
		JsonObject consoleAppender = new JsonObject();
		consoleAppender.add("type", new JsonPrimitive("console"));
		consoleAppender.add("target", new JsonPrimitive("stdout"));
		appenders.add(consoleAppender);
		JsonObject fileAppender = new JsonObject();
		fileAppender.add("type", new JsonPrimitive("file"));
		fileAppender.add("path", new JsonPrimitive("/var/log/myapp/application.log"));
		fileAppender.add("maxSize", new JsonPrimitive("100MB"));
		fileAppender.add("maxHistory", new JsonPrimitive(30));
		appenders.add(fileAppender);
		logging.add("appenders", appenders);
		config.add("logging", logging);

		JsonObject features = new JsonObject();
		features.add("enableCache", new JsonPrimitive(true));
		features.add("enableMetrics", new JsonPrimitive(true));
		features.add("enableTracing", new JsonPrimitive(false));
		JsonObject experimental = new JsonObject();
		experimental.add("newUI", new JsonPrimitive(false));
		experimental.add("betaFeatures", new JsonPrimitive(true));
		features.add("experimental", experimental);
		config.add("features", features);

		JsonObject environments = new JsonObject();
		JsonObject dev = new JsonObject();
		dev.add("debug", new JsonPrimitive(true));
		dev.add("mockServices", new JsonPrimitive(true));
		environments.add("development", dev);
		JsonObject prod = new JsonObject();
		prod.add("debug", new JsonPrimitive(false));
		prod.add("mockServices", new JsonPrimitive(false));
		environments.add("production", prod);
		config.add("environments", environments);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(config);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		assertEquals("localhost", resultObj.getAsJsonObject("database").getAsString("host"));
		assertEquals(5432, resultObj.getAsJsonObject("database").getAsLong("port"));
		assertEquals("myapp_db", resultObj.getAsJsonObject("database").getAsString("name"));
		assertEquals(20, resultObj.getAsJsonObject("database").getAsJsonObject("pool").getAsLong("maxSize"));
		assertEquals(600000, resultObj.getAsJsonObject("database").getAsJsonObject("pool").getAsLong("idleTimeout"));

		assertEquals("INFO", resultObj.getAsJsonObject("logging").getAsString("level"));
		assertEquals(2, resultObj.getAsJsonObject("logging").getAsJsonArray("appenders").size());
		assertEquals("console", resultObj.getAsJsonObject("logging").getAsJsonArray("appenders").getAsJsonObject(0).getAsString("type"));

		assertTrue(resultObj.getAsJsonObject("features").getAsBoolean("enableCache"));
		assertFalse(resultObj.getAsJsonObject("features").getAsBoolean("enableTracing"));
		assertTrue(resultObj.getAsJsonObject("features").getAsJsonObject("experimental").getAsBoolean("betaFeatures"));

		assertTrue(resultObj.getAsJsonObject("environments").getAsJsonObject("development").getAsBoolean("debug"));
		assertFalse(resultObj.getAsJsonObject("environments").getAsJsonObject("production").getAsBoolean("debug"));
	}

	@Test
	void eCommerceProductCatalogRoundTrip() throws IOException {
		JsonObject catalog = new JsonObject();

		JsonArray products = new JsonArray();

		JsonObject product1 = new JsonObject();
		product1.add("id", new JsonPrimitive("PROD-001"));
		product1.add("name", new JsonPrimitive("Premium Wireless Headphones"));
		product1.add("description", new JsonPrimitive("High-quality wireless headphones with noise cancellation"));
		product1.add("price", new JsonPrimitive(299.99));
		product1.add("currency", new JsonPrimitive("USD"));
		product1.add("inStock", new JsonPrimitive(true));
		JsonArray categories1 = new JsonArray();
		categories1.add(new JsonPrimitive("Electronics"));
		categories1.add(new JsonPrimitive("Audio"));
		categories1.add(new JsonPrimitive("Headphones"));
		product1.add("categories", categories1);
		JsonArray variants1 = new JsonArray();
		JsonObject variant1a = new JsonObject();
		variant1a.add("sku", new JsonPrimitive("PROD-001-BLK"));
		variant1a.add("color", new JsonPrimitive("Black"));
		variant1a.add("stock", new JsonPrimitive(150));
		variants1.add(variant1a);
		JsonObject variant1b = new JsonObject();
		variant1b.add("sku", new JsonPrimitive("PROD-001-WHT"));
		variant1b.add("color", new JsonPrimitive("White"));
		variant1b.add("stock", new JsonPrimitive(75));
		variants1.add(variant1b);
		product1.add("variants", variants1);
		JsonArray reviews1 = new JsonArray();
		JsonObject review1a = new JsonObject();
		review1a.add("userId", new JsonPrimitive("USR-123"));
		review1a.add("rating", new JsonPrimitive(5));
		review1a.add("comment", new JsonPrimitive("Excellent sound quality!"));
		review1a.add("timestamp", new JsonPrimitive("2024-01-15T10:30:00Z"));
		reviews1.add(review1a);
		JsonObject review1b = new JsonObject();
		review1b.add("userId", new JsonPrimitive("USR-456"));
		review1b.add("rating", new JsonPrimitive(4));
		review1b.add("comment", new JsonPrimitive("Great headphones, but battery could be better"));
		review1b.add("timestamp", new JsonPrimitive("2024-01-20T14:15:00Z"));
		reviews1.add(review1b);
		product1.add("reviews", reviews1);
		products.add(product1);

		JsonObject product2 = new JsonObject();
		product2.add("id", new JsonPrimitive("PROD-002"));
		product2.add("name", new JsonPrimitive("Smart Watch Pro"));
		product2.add("description", new JsonPrimitive("Advanced smartwatch with health monitoring"));
		product2.add("price", new JsonPrimitive(449.99));
		product2.add("currency", new JsonPrimitive("USD"));
		product2.add("inStock", new JsonPrimitive(true));
		JsonArray categories2 = new JsonArray();
		categories2.add(new JsonPrimitive("Electronics"));
		categories2.add(new JsonPrimitive("Wearables"));
		categories2.add(new JsonPrimitive("Smartwatches"));
		product2.add("categories", categories2);
		JsonArray variants2 = new JsonArray();
		JsonObject variant2a = new JsonObject();
		variant2a.add("sku", new JsonPrimitive("PROD-002-S-SLV"));
		variant2a.add("size", new JsonPrimitive("Small"));
		variant2a.add("color", new JsonPrimitive("Silver"));
		variant2a.add("stock", new JsonPrimitive(50));
		variants2.add(variant2a);
		JsonObject variant2b = new JsonObject();
		variant2b.add("sku", new JsonPrimitive("PROD-002-L-GLD"));
		variant2b.add("size", new JsonPrimitive("Large"));
		variant2b.add("color", new JsonPrimitive("Gold"));
		variant2b.add("stock", new JsonPrimitive(30));
		variants2.add(variant2b);
		product2.add("variants", variants2);
		product2.add("reviews", new JsonArray());
		products.add(product2);

		catalog.add("products", products);

		JsonObject pagination = new JsonObject();
		pagination.add("page", new JsonPrimitive(1));
		pagination.add("limit", new JsonPrimitive(10));
		pagination.add("total", new JsonPrimitive(2));
		pagination.add("totalPages", new JsonPrimitive(1));
		catalog.add("pagination", pagination);

		JsonObject filters = new JsonObject();
		JsonArray priceRange = new JsonArray();
		priceRange.add(new JsonPrimitive(0));
		priceRange.add(new JsonPrimitive(1000));
		filters.add("priceRange", priceRange);
		JsonArray availableCategories = new JsonArray();
		availableCategories.add(new JsonPrimitive("Electronics"));
		availableCategories.add(new JsonPrimitive("Audio"));
		availableCategories.add(new JsonPrimitive("Wearables"));
		filters.add("categories", availableCategories);
		catalog.add("filters", filters);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(catalog);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		JsonArray resultProducts = resultObj.getAsJsonArray("products");
		assertEquals(2, resultProducts.size());
		assertEquals("PROD-001", resultProducts.getAsJsonObject(0).getAsString("id"));
		assertEquals("Premium Wireless Headphones", resultProducts.getAsJsonObject(0).getAsString("name"));
		assertEquals(299.99, resultProducts.getAsJsonObject(0).getAsDouble("price"), 0.001);
		assertTrue(resultProducts.getAsJsonObject(0).getAsBoolean("inStock"));
		assertEquals(3, resultProducts.getAsJsonObject(0).getAsJsonArray("categories").size());
		assertEquals(2, resultProducts.getAsJsonObject(0).getAsJsonArray("variants").size());
		assertEquals(150, resultProducts.getAsJsonObject(0).getAsJsonArray("variants").getAsJsonObject(0).getAsLong("stock"));

		assertEquals(1, resultObj.getAsJsonObject("pagination").getAsLong("page"));
		assertEquals(2, resultObj.getAsJsonObject("pagination").getAsLong("total"));
	}

	@Test
	void userProfileWithActivityRoundTrip() throws IOException {
		JsonObject user = new JsonObject();

		JsonObject profile = new JsonObject();
		profile.add("id", new JsonPrimitive("USR-789"));
		profile.add("username", new JsonPrimitive("johndoe"));
		profile.add("email", new JsonPrimitive("john.doe@example.com"));
		profile.add("displayName", new JsonPrimitive("John Doe"));
		profile.add("avatar", new JsonPrimitive("https://cdn.example.com/avatars/johndoe.png"));
		profile.add("bio", new JsonPrimitive("Software developer passionate about clean code"));
		profile.add("createdAt", new JsonPrimitive("2023-06-15T08:00:00Z"));
		profile.add("lastLogin", new JsonPrimitive("2024-01-25T16:45:00Z"));
		profile.add("verified", new JsonPrimitive(true));
		JsonObject preferences = new JsonObject();
		preferences.add("theme", new JsonPrimitive("dark"));
		preferences.add("language", new JsonPrimitive("en-US"));
		preferences.add("timezone", new JsonPrimitive("America/New_York"));
		JsonObject notifications = new JsonObject();
		notifications.add("email", new JsonPrimitive(true));
		notifications.add("push", new JsonPrimitive(true));
		notifications.add("sms", new JsonPrimitive(false));
		preferences.add("notifications", notifications);
		profile.add("preferences", preferences);
		user.add("profile", profile);

		JsonArray activity = new JsonArray();
		JsonObject action1 = new JsonObject();
		action1.add("type", new JsonPrimitive("login"));
		action1.add("timestamp", new JsonPrimitive("2024-01-25T16:45:00Z"));
		action1.add("ip", new JsonPrimitive("192.168.1.100"));
		action1.add("device", new JsonPrimitive("Chrome on Windows"));
		activity.add(action1);
		JsonObject action2 = new JsonObject();
		action2.add("type", new JsonPrimitive("purchase"));
		action2.add("timestamp", new JsonPrimitive("2024-01-24T10:30:00Z"));
		JsonObject details2 = new JsonObject();
		details2.add("orderId", new JsonPrimitive("ORD-12345"));
		details2.add("amount", new JsonPrimitive(149.99));
		action2.add("details", details2);
		activity.add(action2);
		JsonObject action3 = new JsonObject();
		action3.add("type", new JsonPrimitive("profile_update"));
		action3.add("timestamp", new JsonPrimitive("2024-01-20T09:15:00Z"));
		JsonArray changes = new JsonArray();
		changes.add(new JsonPrimitive("avatar"));
		changes.add(new JsonPrimitive("bio"));
		action3.add("fields", changes);
		activity.add(action3);
		user.add("activity", activity);

		JsonObject permissions = new JsonObject();
		JsonArray roles = new JsonArray();
		roles.add(new JsonPrimitive("user"));
		roles.add(new JsonPrimitive("premium"));
		permissions.add("roles", roles);
		JsonObject access = new JsonObject();
		access.add("dashboard", new JsonPrimitive(true));
		access.add("analytics", new JsonPrimitive(true));
		access.add("admin", new JsonPrimitive(false));
		JsonObject features = new JsonObject();
		features.add("advancedSearch", new JsonPrimitive(true));
		features.add("exportData", new JsonPrimitive(true));
		features.add("apiAccess", new JsonPrimitive(false));
		access.add("features", features);
		permissions.add("access", access);
		user.add("permissions", permissions);

		JsonArray connections = new JsonArray();
		JsonObject conn1 = new JsonObject();
		conn1.add("userId", new JsonPrimitive("USR-111"));
		conn1.add("username", new JsonPrimitive("janedoe"));
		conn1.add("type", new JsonPrimitive("friend"));
		conn1.add("since", new JsonPrimitive("2023-08-10T12:00:00Z"));
		connections.add(conn1);
		JsonObject conn2 = new JsonObject();
		conn2.add("userId", new JsonPrimitive("USR-222"));
		conn2.add("username", new JsonPrimitive("bobsmith"));
		conn2.add("type", new JsonPrimitive("colleague"));
		conn2.add("since", new JsonPrimitive("2023-09-05T14:30:00Z"));
		connections.add(conn2);
		user.add("connections", connections);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(user);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		assertEquals("johndoe", resultObj.getAsJsonObject("profile").getAsString("username"));
		assertEquals("john.doe@example.com", resultObj.getAsJsonObject("profile").getAsString("email"));
		assertTrue(resultObj.getAsJsonObject("profile").getAsBoolean("verified"));
		assertEquals("dark", resultObj.getAsJsonObject("profile").getAsJsonObject("preferences").getAsString("theme"));
		assertTrue(resultObj.getAsJsonObject("profile").getAsJsonObject("preferences").getAsJsonObject("notifications").getAsBoolean("email"));

		assertEquals(3, resultObj.getAsJsonArray("activity").size());
		assertEquals("login", resultObj.getAsJsonArray("activity").getAsJsonObject(0).getAsString("type"));

		assertEquals(2, resultObj.getAsJsonObject("permissions").getAsJsonArray("roles").size());
		assertTrue(resultObj.getAsJsonObject("permissions").getAsJsonObject("access").getAsBoolean("dashboard"));

		assertEquals(2, resultObj.getAsJsonArray("connections").size());
		assertEquals("janedoe", resultObj.getAsJsonArray("connections").getAsJsonObject(0).getAsString("username"));
	}

	@Test
	void apiResponseWithPaginationRoundTrip() throws IOException {
		JsonObject response = new JsonObject();

		JsonArray data = new JsonArray();
		for (int i = 1; i <= 15; i++) {
			JsonObject item = new JsonObject();
			item.add("id", new JsonPrimitive(i));
			item.add("title", new JsonPrimitive("Item " + i));
			item.add("status", new JsonPrimitive(i % 2 == 0 ? "active" : "pending"));
			JsonObject metadata = new JsonObject();
			metadata.add("createdBy", new JsonPrimitive("user_" + (i % 5)));
			metadata.add("version", new JsonPrimitive(i * 10));
			item.add("metadata", metadata);
			data.add(item);
		}
		response.add("data", data);

		JsonObject pagination = new JsonObject();
		pagination.add("page", new JsonPrimitive(2));
		pagination.add("limit", new JsonPrimitive(15));
		pagination.add("total", new JsonPrimitive(47));
		pagination.add("totalPages", new JsonPrimitive(4));
		pagination.add("hasNext", new JsonPrimitive(true));
		pagination.add("hasPrev", new JsonPrimitive(true));
		response.add("pagination", pagination);

		JsonObject links = new JsonObject();
		links.add("self", new JsonPrimitive("/api/items?page=2&limit=15"));
		links.add("first", new JsonPrimitive("/api/items?page=1&limit=15"));
		links.add("prev", new JsonPrimitive("/api/items?page=1&limit=15"));
		links.add("next", new JsonPrimitive("/api/items?page=3&limit=15"));
		links.add("last", new JsonPrimitive("/api/items?page=4&limit=15"));
		response.add("links", links);

		JsonObject meta = new JsonObject();
		meta.add("requestId", new JsonPrimitive("req-abc123"));
		meta.add("timestamp", new JsonPrimitive("2024-01-25T17:00:00Z"));
		meta.add("processingTime", new JsonPrimitive(45));
		meta.add("apiVersion", new JsonPrimitive("v2"));
		response.add("meta", meta);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(response);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		assertEquals(15, resultObj.getAsJsonArray("data").size());
		assertEquals(1, resultObj.getAsJsonArray("data").getAsJsonObject(0).getAsLong("id"));
		assertEquals("Item 1", resultObj.getAsJsonArray("data").getAsJsonObject(0).getAsString("title"));
		assertEquals("pending", resultObj.getAsJsonArray("data").getAsJsonObject(0).getAsString("status"));
		assertEquals(10, resultObj.getAsJsonArray("data").getAsJsonObject(0).getAsJsonObject("metadata").getAsLong("version"));

		assertEquals(47, resultObj.getAsJsonObject("pagination").getAsLong("total"));
		assertEquals(4, resultObj.getAsJsonObject("pagination").getAsLong("totalPages"));
		assertTrue(resultObj.getAsJsonObject("pagination").getAsBoolean("hasNext"));
		assertTrue(resultObj.getAsJsonObject("pagination").getAsBoolean("hasPrev"));

		assertEquals("/api/items?page=2&limit=15", resultObj.getAsJsonObject("links").getAsString("self"));
		assertEquals("req-abc123", resultObj.getAsJsonObject("meta").getAsString("requestId"));
	}

	@Test
	void deeplyNestedConfigurationRoundTrip() throws IOException {
		JsonObject level1 = new JsonObject();

		JsonObject level2 = new JsonObject();
		level2.add("name", new JsonPrimitive("level2"));

		JsonObject level3 = new JsonObject();
		level3.add("name", new JsonPrimitive("level3"));

		JsonObject level4 = new JsonObject();
		level4.add("name", new JsonPrimitive("level4"));

		JsonObject level5 = new JsonObject();
		level5.add("name", new JsonPrimitive("level5"));

		JsonObject level6 = new JsonObject();
		level6.add("name", new JsonPrimitive("level6"));
		level6.add("value", new JsonPrimitive("deepest value"));
		level6.add("number", new JsonPrimitive(12345));
		level6.add("flag", new JsonPrimitive(true));
		JsonArray deepArray = new JsonArray();
		deepArray.add(new JsonPrimitive("a"));
		deepArray.add(new JsonPrimitive("b"));
		deepArray.add(new JsonPrimitive("c"));
		level6.add("items", deepArray);

		level5.add("nested", level6);
		level5.add("extra", new JsonPrimitive("level5 data"));

		level4.add("nested", level5);
		level4.add("extra", new JsonPrimitive("level4 data"));

		level3.add("nested", level4);
		level3.add("extra", new JsonPrimitive("level3 data"));

		level2.add("nested", level3);
		level2.add("extra", new JsonPrimitive("level2 data"));

		level1.add("nested", level2);
		level1.add("extra", new JsonPrimitive("level1 data"));

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(level1);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		assertEquals("level1 data", resultObj.getAsString("extra"));

		JsonObject deepest = resultObj
			.getAsJsonObject("nested")
			.getAsJsonObject("nested")
			.getAsJsonObject("nested")
			.getAsJsonObject("nested")
			.getAsJsonObject("nested");
		assertEquals("level6", deepest.getAsString("name"));
		assertEquals("deepest value", deepest.getAsString("value"));
		assertEquals(12345, deepest.getAsLong("number"));
		assertTrue(deepest.getAsBoolean("flag"));
		assertEquals(3, deepest.getAsJsonArray("items").size());
		assertEquals("a", deepest.getAsJsonArray("items").getAsString(0));
	}

	@Test
	void largeArrayDataRoundTrip() throws IOException {
		JsonObject data = new JsonObject();

		JsonArray items = new JsonArray();
		for (int i = 0; i < 100; i++) {
			JsonObject item = new JsonObject();
			item.add("id", new JsonPrimitive(i));
			item.add("uuid", new JsonPrimitive("uuid-" + String.format("%08d", i)));
			item.add("name", new JsonPrimitive("Item Number " + i));
			item.add("description", new JsonPrimitive("This is the description for item " + i + " with some additional text to make it longer"));
			item.add("price", new JsonPrimitive(i * 10.5));
			item.add("quantity", new JsonPrimitive(i * 2));
			item.add("active", new JsonPrimitive(i % 3 != 0));

			JsonArray tags = new JsonArray();
			tags.add(new JsonPrimitive("tag-" + (i % 10)));
			tags.add(new JsonPrimitive("category-" + (i % 5)));
			tags.add(new JsonPrimitive("group-" + (i % 3)));
			item.add("tags", tags);

			JsonObject attributes = new JsonObject();
			attributes.add("weight", new JsonPrimitive(i * 0.5));
			attributes.add("dimensions", new JsonPrimitive(i + "x" + (i + 1) + "x" + (i + 2)));
			attributes.add("color", new JsonPrimitive(i % 2 == 0 ? "red" : "blue"));
			item.add("attributes", attributes);

			items.add(item);
		}
		data.add("items", items);

		JsonObject summary = new JsonObject();
		summary.add("count", new JsonPrimitive(100));
		summary.add("activeCount", new JsonPrimitive(67));
		summary.add("totalValue", new JsonPrimitive(49725.0));
		data.add("summary", summary);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (JsonWriter writer = new JsonWriter(new OutputProvider(output))) {
			writer.writeJson(data);
		}

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		JsonElement result;
		try (JsonReader reader = new JsonReader(new InputProvider(input))) {
			result = reader.readJson();
		}

		assertTrue(result.isJsonObject());
		JsonObject resultObj = result.getAsJsonObject();

		JsonArray resultItems = resultObj.getAsJsonArray("items");
		assertEquals(100, resultItems.size());

		for (int i = 0; i < 100; i++) {
			JsonObject item = resultItems.getAsJsonObject(i);
			assertEquals(i, item.getAsLong("id"));
			assertEquals("uuid-" + String.format("%08d", i), item.getAsString("uuid"));
			assertEquals("Item Number " + i, item.getAsString("name"));
			assertEquals(3, item.getAsJsonArray("tags").size());
		}

		assertEquals(100, resultObj.getAsJsonObject("summary").getAsLong("count"));
		assertEquals(67, resultObj.getAsJsonObject("summary").getAsLong("activeCount"));
		assertEquals(49725.0, resultObj.getAsJsonObject("summary").getAsDouble("totalValue"), 0.001);
	}
}
