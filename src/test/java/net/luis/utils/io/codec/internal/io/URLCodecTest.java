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

package net.luis.utils.io.codec.internal.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URLCodec}.<br>
 *
 * @author Luis-St
 */
class URLCodecTest {
	
	@Test
	void encodeStartNullChecks() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		URL url = URI.create("https://example.com").toURL();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), url));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, url));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as URL"));
	}
	
	@Test
	void encodeStartWithValidURL() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		URL url = URI.create("https://example.com").toURL();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), url);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithURLWithPath() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		URL url = URI.create("https://example.com/path/to/resource").toURL();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), url);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com/path/to/resource"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithURLWithQuery() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		URL url = URI.create("https://example.com?key=value").toURL();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), url);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com?key=value"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFileURL() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		URL url = URI.create("file:///tmp/test").toURL();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), url);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("file:/tmp/test"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("https://example.com")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as URL"));
	}
	
	@Test
	void decodeStartWithValidString() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive("https://example.com"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com").toURL(), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithURLWithPath() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive("https://example.com/path/to/resource"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com/path/to/resource").toURL(), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithURLWithQuery() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive("https://example.com?key=value"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com?key=value").toURL(), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithFileURL() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive("file:///tmp/test"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("file:///tmp/test").toURL(), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidURL() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive("ht tp://invalid url"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URL> codec = new URLCodec();
		
		Result<URL> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		URLCodec codec = new URLCodec();
		assertEquals("URLCodec", codec.toString());
	}
}
