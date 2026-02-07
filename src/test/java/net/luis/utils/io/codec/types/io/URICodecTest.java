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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), uri));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, uri));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as URI"));
	}
	
	@Test
	void encodeWithValidURI() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uri);
		assertEquals(new JsonPrimitive("https://example.com"), result);
	}
	
	@Test
	void encodeWithURIWithPath() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com/path/to/resource");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uri);
		assertEquals(new JsonPrimitive("https://example.com/path/to/resource"), result);
	}
	
	@Test
	void encodeWithURIWithQuery() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("https://example.com?key=value");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uri);
		assertEquals(new JsonPrimitive("https://example.com?key=value"), result);
	}
	
	@Test
	void encodeWithFileURI() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		URI uri = URI.create("file:///tmp/test");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), uri);
		assertEquals(new JsonPrimitive("file:///tmp/test"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("https://example.com")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as URI"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		URI result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com"));
		assertEquals(URI.create("https://example.com"), result);
	}
	
	@Test
	void decodeWithURIWithPath() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		URI result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path/to/resource"));
		assertEquals(URI.create("https://example.com/path/to/resource"), result);
	}
	
	@Test
	void decodeWithURIWithQuery() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		URI result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com?key=value"));
		assertEquals(URI.create("https://example.com?key=value"), result);
	}
	
	@Test
	void decodeWithFileURI() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		URI result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("file:///tmp/test"));
		assertEquals(URI.create("file:///tmp/test"), result);
	}
	
	@Test
	void decodeWithInvalidURI() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ht tp://invalid uri")));
		assertTrue(exception.getMessage().contains("Unable to decode URI"));
		assertTrue(exception.getMessage().contains("Unable to parse URI"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = new URICodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		URICodec codec = new URICodec();
		assertEquals("URICodec", codec.toString());
	}
}
