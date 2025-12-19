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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URICodec}.<br>
 *
 * @author Luis-St
 */
class URICodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), uri));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, uri));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as URI"));
	}
	
	@Test
	void encodeStartWithValidURI() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithURIWithPath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com/path/to/resource");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com/path/to/resource"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithURIWithQuery() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com?key=value");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com?key=value"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFileURI() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("file:///tmp/test");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("file:///tmp/test"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("https://example.com")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as URI"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithURIWithPath() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path/to/resource"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com/path/to/resource"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithURIWithQuery() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com?key=value"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("https://example.com?key=value"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithFileURI() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("file:///tmp/test"));
		assertTrue(result.isSuccess());
		assertEquals(URI.create("file:///tmp/test"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidURI() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("ht tp://invalid uri"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode URI"));
		assertTrue(result.errorOrThrow().contains("Unable to parse URI"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		URICodec codec = new URICodec();
		assertEquals("URICodec", codec.toString());
	}
}
