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

import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonWriter}.<br>
 *
 * @author Luis-St
 */
class JsonWriterTest {
	
	@Test
	void constructor() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertThrows(NullPointerException.class, () -> new JsonWriter(null));
		assertDoesNotThrow(() -> new JsonWriter(provider));
		
		assertThrows(NullPointerException.class, () -> new JsonWriter(null, JsonConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new JsonWriter(provider, null));
		assertDoesNotThrow(() -> new JsonWriter(provider, JsonConfig.DEFAULT));
	}
	
	@Test
	void writeJsonArray() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		JsonArray array = new JsonArray();
		
		writer.writeJson(array);
		assertEquals("[]", stream.toString());
		stream.reset();
		
		array.add("test");
		writer.writeJson(array);
		assertEquals("[\"test\"]", stream.toString());
		stream.reset();
	}
	
	@Test
	void writeJsonNull() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(JsonNull.INSTANCE);
		assertEquals("null", stream.toString());
	}
	
	@Test
	void writeJsonObject() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		JsonObject object = new JsonObject();
		
		writer.writeJson(object);
		assertEquals("{}", stream.toString());
		stream.reset();
		
		object.add("key", "value");
		writer.writeJson(object);
		assertEquals("{ \"key\": \"value\" }", stream.toString());
		stream.reset();
	}
	
	@Test
	void writeJsonPrimitive() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(new JsonPrimitive(true));
		assertEquals("true", stream.toString());
		stream.reset();
		
		writer.writeJson(new JsonPrimitive(42));
		assertEquals("42", stream.toString());
		stream.reset();
		
		writer.writeJson(new JsonPrimitive("test"));
		assertEquals("\"test\"", stream.toString());
		stream.reset();
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new JsonWriter(new OutputProvider(OutputStream.nullOutputStream())).close());
	}
	
	//region Internal classes
	@MockObject(OutputStream.class)
	private static class StringOutputStream extends OutputStream {
		
		private final StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(int b) {
			this.builder.append((char) b);
		}
		
		public void reset() {
			this.builder.setLength(0);
		}
		
		@Override
		public String toString() {
			return this.builder.toString();
		}
	}
	//endregion
}
