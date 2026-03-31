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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link URICodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedURICodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), expected);
		assertEquals(new JsonPrimitive("https://example.com/path"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		URI result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertEquals(expected, result);
	}
	
	@Test
	void toStringWithConstraints() {
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		URI different = URI.create("https://other.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), different));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://other.com/path")));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI excluded = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI excluded = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path")));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> allowed = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.in(allowed);
		URI notAllowed = URI.create("https://other.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> allowed = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://other.com/path")));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> excluded = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.notIn(excluded);
		URI excludedValue = URI.create("https://example.com/path1");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excludedValue));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> excluded = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path1")));
	}
	
	@Test
	void encodeSchemeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		URI httpUri = URI.create("http://example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), httpUri));
	}
	
	@Test
	void decodeSchemeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("http://example.com/path")));
	}
	
	@Test
	void encodeSchemeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		URI httpsUri = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), httpsUri));
	}
	
	@Test
	void encodePortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		URI wrongPort = URI.create("https://example.com:8080/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), wrongPort));
	}
	
	@Test
	void decodePortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com:8080/path")));
	}
	
	@Test
	void encodePortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		URI correctPort = URI.create("https://example.com:443/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), correctPort));
	}
	
	@Test
	void encodeWithoutPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		URI withPort = URI.create("https://example.com:8080/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), withPort));
	}
	
	@Test
	void decodeWithoutPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com:8080/path")));
	}
	
	@Test
	void encodeWithoutPortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		URI withoutPort = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), withoutPort));
	}
	
	@Test
	void encodeFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		URI wrongFragment = URI.create("https://example.com/path#section2");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), wrongFragment));
	}
	
	@Test
	void decodeFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path#section2")));
	}
	
	@Test
	void encodeFragmentSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		URI correctFragment = URI.create("https://example.com/path#section1");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), correctFragment));
	}
	
	@Test
	void encodeWithoutFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		URI withFragment = URI.create("https://example.com/path#section");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), withFragment));
	}
	
	@Test
	void decodeWithoutFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path#section")));
	}
	
	@Test
	void encodeWithoutFragmentSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		URI withoutFragment = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), withoutFragment));
	}
	
	@Test
	void encodeAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		URI relativeUri = URI.create("/path/to/resource");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), relativeUri));
	}
	
	@Test
	void decodeAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("/path/to/resource")));
	}
	
	@Test
	void encodeAbsoluteSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		URI absoluteUri = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), absoluteUri));
	}
	
	@Test
	void encodeRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		URI absoluteUri = URI.create("https://example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), absoluteUri));
	}
	
	@Test
	void decodeRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path")));
	}
	
	@Test
	void encodeRelativeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		URI relativeUri = URI.create("/path/to/resource");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), relativeUri));
	}
	
	@Test
	void encodeOpaqueConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		URI hierarchicalUri = URI.create("https://example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), hierarchicalUri));
	}
	
	@Test
	void decodeOpaqueConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path")));
	}
	
	@Test
	void encodeOpaqueSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		URI opaqueUri = URI.create("mailto:user@example.com");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), opaqueUri));
	}
	
	@Test
	void encodeHierarchicalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		URI opaqueUri = URI.create("mailto:user@example.com");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), opaqueUri));
	}
	
	@Test
	void decodeHierarchicalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("mailto:user@example.com")));
	}
	
	@Test
	void encodeHierarchicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		URI hierarchicalUri = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), hierarchicalUri));
	}
	
	@Test
	void encodeWithoutQueryConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		URI withQuery = URI.create("https://example.com/path?query=value");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), withQuery));
	}
	
	@Test
	void decodeWithoutQueryConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path?query=value")));
	}
	
	@Test
	void encodeWithoutQuerySuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		URI withoutQuery = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), withoutQuery));
	}
	
	@Test
	void encodeWithoutUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		URI withUserInfo = URI.create("https://user:password@example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), withUserInfo));
	}
	
	@Test
	void decodeWithoutUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://user:password@example.com/path")));
	}
	
	@Test
	void encodeWithoutUserInfoSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		URI withoutUserInfo = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), withoutUserInfo));
	}
	
	@Test
	void encodeUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		URI wrongUserInfo = URI.create("https://user@example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), wrongUserInfo));
	}
	
	@Test
	void decodeUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://user@example.com/path")));
	}
	
	@Test
	void encodeUserInfoSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		URI correctUserInfo = URI.create("https://admin@example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), correctUserInfo));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		URI uri = URI.create("https://example.com/path");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), uri));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> {
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Custom validation failed");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path")));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> {});
		URI uri = URI.create("https://example.com/path");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), uri));
	}
}
