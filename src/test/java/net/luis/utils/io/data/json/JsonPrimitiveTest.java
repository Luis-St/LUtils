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

package net.luis.utils.io.data.json;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonPrimitive}.<br>
 *
 * @author Luis-St
 */
class JsonPrimitiveTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, true, "  ", true, 10, true, 2, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new JsonPrimitive((Number) null));
		assertThrows(NullPointerException.class, () -> new JsonPrimitive((String) null));
		assertDoesNotThrow(() -> new JsonPrimitive(true));
		assertDoesNotThrow(() -> new JsonPrimitive(1));
		assertDoesNotThrow(() -> new JsonPrimitive(1.0));
		assertDoesNotThrow(() -> new JsonPrimitive("test"));
	}
	
	@Test
	void isJsonNull() {
		assertFalse(new JsonPrimitive(true).isJsonNull());
	}
	
	@Test
	void isJsonObject() {
		assertFalse(new JsonPrimitive(true).isJsonObject());
	}
	
	@Test
	void isJsonArray() {
		assertFalse(new JsonPrimitive(true).isJsonArray());
	}
	
	@Test
	void isJsonPrimitive() {
		assertTrue(new JsonPrimitive(true).isJsonPrimitive());
	}
	
	@Test
	void getAsJsonObject() {
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(true).getAsJsonObject());
	}
	
	@Test
	void getAsJsonArray() {
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(true).getAsJsonArray());
	}
	
	@Test
	void getAsJsonPrimitive() {
		assertDoesNotThrow(() -> new JsonPrimitive(true).getAsJsonPrimitive());
	}
	
	@Test
	void getAsString() {
		assertEquals("true", new JsonPrimitive(true).getAsString());
		assertEquals("1", new JsonPrimitive(1).getAsString());
		assertEquals("1.0", new JsonPrimitive(1.0).getAsString());
		assertEquals("1.0f", new JsonPrimitive("1.0f").getAsString());
		assertEquals("test", new JsonPrimitive("test").getAsString());
	}
	
	@Test
	void getAsBoolean() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("test").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(0).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(0.0).getAsBoolean());
		assertTrue(new JsonPrimitive("true").getAsBoolean());
		assertFalse(new JsonPrimitive("false").getAsBoolean());
		assertTrue(new JsonPrimitive(true).getAsBoolean());
		assertFalse(new JsonPrimitive(false).getAsBoolean());
	}
	
	@Test
	void getAsNumber() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("test").getAsNumber());
		assertEquals(1L, new JsonPrimitive((byte) 1).getAsNumber());
		assertEquals(1L, new JsonPrimitive((short) 1).getAsNumber());
		assertEquals(1L, new JsonPrimitive(1).getAsNumber());
		assertEquals(1L, new JsonPrimitive(1L).getAsNumber());
		assertEquals(1.0, new JsonPrimitive(1.0F).getAsNumber());
		assertEquals(1.0, new JsonPrimitive(1.0).getAsNumber());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsNumber());
		assertEquals(1.0F, new JsonPrimitive("1.0f").getAsNumber());
	}
	
	@Test
	void getAsByte() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0F).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive((byte) 1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive((short) 1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1L).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive("1b").getAsByte());
	}
	
	@Test
	void getAsShort() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0F).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0).getAsShort());
		assertEquals((short) 1, new JsonPrimitive((byte) 1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive((short) 1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1L).getAsShort());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsShort());
	}
	
	@Test
	void getAsInteger() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0F).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0).getAsInteger());
		assertEquals(1, new JsonPrimitive((byte) 1).getAsInteger());
		assertEquals(1, new JsonPrimitive((short) 1).getAsInteger());
		assertEquals(1, new JsonPrimitive(1).getAsInteger());
		assertEquals(1, new JsonPrimitive(1L).getAsInteger());
		assertEquals(1, new JsonPrimitive("1i").getAsInteger());
	}
	
	@Test
	void getAsLong() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0F).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1.0).getAsLong());
		assertEquals(1L, new JsonPrimitive((byte) 1).getAsLong());
		assertEquals(1L, new JsonPrimitive((short) 1).getAsLong());
		assertEquals(1L, new JsonPrimitive(1).getAsLong());
		assertEquals(1L, new JsonPrimitive(1L).getAsLong());
		assertEquals(1L, new JsonPrimitive("1l").getAsLong());
	}
	
	@Test
	void getAsFloat() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsFloat());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive((byte) 1).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive((short) 1).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive(1).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive(1L).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive(1.0F).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive(1.0).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive("1f").getAsFloat());
		assertEquals(1.0F, new JsonPrimitive("1.0f").getAsFloat());
	}
	
	@Test
	void getAsDouble() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsDouble());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsDouble());
		assertEquals(1.0, new JsonPrimitive((byte) 1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive((short) 1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1L).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1.0F).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1.0).getAsDouble());
		assertEquals(1.0, new JsonPrimitive("1d").getAsDouble());
		assertEquals(1.0, new JsonPrimitive("1.0d").getAsDouble());
	}
	
	@Test
	void getAs() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new JsonPrimitive("[]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("()").getAs(parser));
		assertIterableEquals(List.of(true, false), new JsonPrimitive("[true, false]").getAs(parser));
	}
	
	@Test
	void toStringDefaultConfig() {
		assertEquals("false", new JsonPrimitive(false).toString());
		assertEquals("true", new JsonPrimitive(true).toString());
		assertEquals("1", new JsonPrimitive(1).toString());
		assertEquals("1.0", new JsonPrimitive(1.0).toString());
		assertEquals("\"test\"", new JsonPrimitive("test").toString());
	}
	
	@Test
	void toStringCustomConfig() {
		assertDoesNotThrow(() -> new JsonPrimitive(10).toString(null));
		assertEquals("false", new JsonPrimitive(false).toString(CUSTOM_CONFIG));
		assertEquals("true", new JsonPrimitive(true).toString(CUSTOM_CONFIG));
		assertEquals("1", new JsonPrimitive(1).toString(CUSTOM_CONFIG));
		assertEquals("1.0", new JsonPrimitive(1.0).toString(CUSTOM_CONFIG));
		assertEquals("\"test\"", new JsonPrimitive("test").toString(CUSTOM_CONFIG));
	}
}
