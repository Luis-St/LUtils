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

import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

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
		assertThrows(NullPointerException.class, () -> new JsonPrimitive((Object) null, true));
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(new Object(), true));
		assertDoesNotThrow(() -> new JsonPrimitive(true));
		assertDoesNotThrow(() -> new JsonPrimitive(1));
		assertDoesNotThrow(() -> new JsonPrimitive(1.0));
		assertDoesNotThrow(() -> new JsonPrimitive("test"));
		assertDoesNotThrow(() -> new JsonPrimitive((Object) "1", false));
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
		
		assertEquals("1.0", new JsonPrimitive("1.0f", true).getAsString());
		assertNotEquals("1.0", new JsonPrimitive("1.0f", false).getAsString());
	}
	
	@Test
	void getAsBoolean() {
		assertTrue(new JsonPrimitive(true).getAsBoolean());
		assertFalse(new JsonPrimitive(false).getAsBoolean());
		assertTrue(new JsonPrimitive(1).getAsBoolean());
		assertFalse(new JsonPrimitive(0).getAsBoolean());
		assertTrue(new JsonPrimitive(1.0).getAsBoolean());
		assertFalse(new JsonPrimitive(0.0).getAsBoolean());
		assertTrue(new JsonPrimitive("true").getAsBoolean());
		assertFalse(new JsonPrimitive("false").getAsBoolean());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsBoolean());
	}
	@Test
	void getAsBooleanStrict() {
		assertTrue(new JsonPrimitive(true).getAsBooleanStrict());
		assertFalse(new JsonPrimitive(false).getAsBooleanStrict());
		assertTrue(new JsonPrimitive("true").getAsBooleanStrict());
		assertFalse(new JsonPrimitive("false").getAsBooleanStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(1).getAsBooleanStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(0).getAsBooleanStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(1.0).getAsBooleanStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(0.0).getAsBooleanStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsBooleanStrict());

	}
	
	@Test
	void getAsNumber() {
		assertEquals(0, new JsonPrimitive(false).getAsNumber());
		assertEquals(1, new JsonPrimitive(true).getAsNumber());
		assertEquals((byte) 1, new JsonPrimitive((byte) 1).getAsNumber());
		assertEquals((short) 1, new JsonPrimitive((short) 1).getAsNumber());
		assertEquals(1, new JsonPrimitive(1).getAsNumber());
		assertEquals(1L, new JsonPrimitive(1L).getAsNumber());
		assertEquals(1.0F, new JsonPrimitive(1.0F).getAsNumber());
		assertEquals(1.0, new JsonPrimitive(1.0).getAsNumber());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsNumber());
		assertEquals(1.0F, new JsonPrimitive("1.0f").getAsNumber());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsNumber());
	}
	
	@Test
	void getAsByte() {
		assertEquals((byte) 0, new JsonPrimitive(false).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(true).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive((byte) 1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive((short) 1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1L).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1.0F).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive(1.0).getAsByte());
		assertEquals((byte) 1, new JsonPrimitive("1b").getAsByte());
	}
	
	@Test
	void getAsByteStrict() {
		assertEquals((byte) 1, new JsonPrimitive(1).getAsByteStrict());
		assertEquals((byte) 1, new JsonPrimitive(1L).getAsByteStrict());
		assertEquals((byte) 1, new JsonPrimitive(1.0F).getAsByteStrict());
		assertEquals((byte) 1, new JsonPrimitive(1.0).getAsByteStrict());
		assertEquals((byte) 1, new JsonPrimitive("1b").getAsByteStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsByteStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsByteStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsByteStrict());
	}
	
	@Test
	void getAsShort() {
		assertEquals((short) 0, new JsonPrimitive(false).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(true).getAsShort());
		assertEquals((short) 1, new JsonPrimitive((byte) 1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive((short) 1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1L).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1.0F).getAsShort());
		assertEquals((short) 1, new JsonPrimitive(1.0).getAsShort());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsShort());
	}
	
	@Test
	void getAsShortStrict() {
		assertEquals((short) 1, new JsonPrimitive(1).getAsShortStrict());
		assertEquals((short) 1, new JsonPrimitive(1L).getAsShortStrict());
		assertEquals((short) 1, new JsonPrimitive(1.0F).getAsShortStrict());
		assertEquals((short) 1, new JsonPrimitive(1.0).getAsShortStrict());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsShortStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsShortStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsShortStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsShortStrict());
	}
	
	@Test
	void getAsInteger() {
		assertEquals(0, new JsonPrimitive(false).getAsInteger());
		assertEquals(1, new JsonPrimitive(true).getAsInteger());
		assertEquals(1, new JsonPrimitive((byte) 1).getAsInteger());
		assertEquals(1, new JsonPrimitive((short) 1).getAsInteger());
		assertEquals(1, new JsonPrimitive(1).getAsInteger());
		assertEquals(1, new JsonPrimitive(1L).getAsInteger());
		assertEquals(1, new JsonPrimitive(1.0F).getAsInteger());
		assertEquals(1, new JsonPrimitive(1.0).getAsInteger());
		assertEquals(1, new JsonPrimitive("1i").getAsInteger());
	}
	
	@Test
	void getAsIntegerStrict() {
		assertEquals(1, new JsonPrimitive(1).getAsIntegerStrict());
		assertEquals(1, new JsonPrimitive(1L).getAsIntegerStrict());
		assertEquals(1, new JsonPrimitive(1.0F).getAsIntegerStrict());
		assertEquals(1, new JsonPrimitive(1.0).getAsIntegerStrict());
		assertEquals(1, new JsonPrimitive("1i").getAsIntegerStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsIntegerStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsIntegerStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsIntegerStrict());
	}
	
	@Test
	void getAsLong() {
		assertEquals(0L, new JsonPrimitive(false).getAsLong());
		assertEquals(1L, new JsonPrimitive(true).getAsLong());
		assertEquals(1L, new JsonPrimitive((byte) 1).getAsLong());
		assertEquals(1L, new JsonPrimitive((short) 1).getAsLong());
		assertEquals(1L, new JsonPrimitive(1).getAsLong());
		assertEquals(1L, new JsonPrimitive(1L).getAsLong());
		assertEquals(1L, new JsonPrimitive(1.0F).getAsLong());
		assertEquals(1L, new JsonPrimitive(1.0).getAsLong());
		assertEquals(1L, new JsonPrimitive("1l").getAsLong());
	}
	
	@Test
	void getAsLongStrict() {
		assertEquals(1L, new JsonPrimitive(1).getAsLongStrict());
		assertEquals(1L, new JsonPrimitive(1L).getAsLongStrict());
		assertEquals(1L, new JsonPrimitive(1.0F).getAsLongStrict());
		assertEquals(1L, new JsonPrimitive(1.0).getAsLongStrict());
		assertEquals(1L, new JsonPrimitive("1l").getAsLongStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsLongStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsLongStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsLongStrict());
	}
	
	@Test
	void getAsFloat() {
		assertEquals(0.0F, new JsonPrimitive(false).getAsFloat());
		assertEquals(1.0F, new JsonPrimitive(true).getAsFloat());
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
	void getAsFloatStrict() {
		assertEquals(1.0F, new JsonPrimitive(1).getAsFloatStrict());
		assertEquals(1.0F, new JsonPrimitive(1L).getAsFloatStrict());
		assertEquals(1.0F, new JsonPrimitive(1.0F).getAsFloatStrict());
		assertEquals(1.0F, new JsonPrimitive(1.0).getAsFloatStrict());
		assertEquals(1.0F, new JsonPrimitive("1f").getAsFloatStrict());
		assertEquals(1.0F, new JsonPrimitive("1.0f").getAsFloatStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsFloatStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsFloatStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsFloatStrict());
	}
	
	@Test
	void getAsDouble() {
		assertEquals(0.0, new JsonPrimitive(false).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(true).getAsDouble());
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
	void getAsDoubleStrict() {
		assertEquals(1.0, new JsonPrimitive(1).getAsDoubleStrict());
		assertEquals(1.0, new JsonPrimitive(1L).getAsDoubleStrict());
		assertEquals(1.0, new JsonPrimitive(1.0F).getAsDoubleStrict());
		assertEquals(1.0, new JsonPrimitive(1.0).getAsDoubleStrict());
		assertEquals(1.0, new JsonPrimitive("1d").getAsDoubleStrict());
		assertEquals(1.0, new JsonPrimitive("1.0d").getAsDoubleStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(true).getAsDoubleStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive(false).getAsDoubleStrict());
		assertThrows(IllegalStateException.class, () -> new JsonPrimitive("test").getAsDoubleStrict());
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
