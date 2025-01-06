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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonNull}.<br>
 *
 * @author Luis-St
 */
class JsonNullTest {
	
	@Test
	void isJsonNull() {
		assertTrue(JsonNull.INSTANCE.isJsonNull());
	}
	
	@Test
	void isJsonObject() {
		assertFalse(JsonNull.INSTANCE.isJsonObject());
	}
	
	@Test
	void isJsonArray() {
		assertFalse(JsonNull.INSTANCE.isJsonArray());
	}
	
	@Test
	void isJsonPrimitive() {
		assertFalse(JsonNull.INSTANCE.isJsonPrimitive());
	}
	
	@Test
	void getAsJsonObject() {
		assertThrows(JsonTypeException.class, JsonNull.INSTANCE::getAsJsonObject);
	}
	
	@Test
	void getAsJsonArray() {
		assertThrows(JsonTypeException.class, JsonNull.INSTANCE::getAsJsonArray);
	}
	
	@Test
	void getAsJsonPrimitive() {
		assertThrows(JsonTypeException.class, JsonNull.INSTANCE::getAsJsonPrimitive);
	}
	
	@Test
	void toStringDefaultConfig() {
		assertEquals("null", JsonNull.INSTANCE.toString());
	}
	
	@Test
	void toStringCustomConfig() {
		assertDoesNotThrow(() -> JsonNull.INSTANCE.toString(null));
		assertEquals("null", JsonNull.INSTANCE.toString(null));
	}
}