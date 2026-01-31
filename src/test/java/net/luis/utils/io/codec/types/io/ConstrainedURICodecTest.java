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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), expected);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("https://example.com/path"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertTrue(result.isSuccess());
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		URI different = URI.create("https://other.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), different);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI expected = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.equalTo(expected);
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://other.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI excluded = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		URI excluded = URI.create("https://example.com/path");
		Codec<URI> codec = Codecs.URI.notEqualTo(excluded);
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> allowed = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.in(allowed);
		URI notAllowed = URI.create("https://other.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> allowed = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.in(allowed);
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://other.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> excluded = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.notIn(excluded);
		URI excludedValue = URI.create("https://example.com/path1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excludedValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		List<URI> excluded = List.of(URI.create("https://example.com/path1"), URI.create("https://example.com/path2"));
		Codec<URI> codec = Codecs.URI.notIn(excluded);
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSchemeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		URI httpUri = URI.create("http://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), httpUri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartSchemeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("http://example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSchemeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.scheme(builder -> builder.equalTo("https"));
		URI httpsUri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), httpsUri);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		URI wrongPort = URI.create("https://example.com:8080/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), wrongPort);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com:8080/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.port(builder -> builder.equalTo(443));
		URI correctPort = URI.create("https://example.com:443/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), correctPort);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithoutPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		URI withPort = URI.create("https://example.com:8080/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withPort);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithoutPortConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com:8080/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithoutPortSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutPort();
		URI withoutPort = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withoutPort);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		URI wrongFragment = URI.create("https://example.com/path#section2");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), wrongFragment);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path#section2"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFragmentSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.fragment(builder -> builder.equalTo("section1"));
		URI correctFragment = URI.create("https://example.com/path#section1");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), correctFragment);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithoutFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		URI withFragment = URI.create("https://example.com/path#section");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withFragment);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithoutFragmentConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path#section"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithoutFragmentSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutFragment();
		URI withoutFragment = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withoutFragment);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		URI relativeUri = URI.create("/path/to/resource");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), relativeUri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAbsoluteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("/path/to/resource"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAbsoluteSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.absolute();
		URI absoluteUri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), absoluteUri);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		URI absoluteUri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), absoluteUri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartRelativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartRelativeSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.relative();
		URI relativeUri = URI.create("/path/to/resource");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), relativeUri);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOpaqueConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		URI hierarchicalUri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), hierarchicalUri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOpaqueConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOpaqueSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.opaque();
		URI opaqueUri = URI.create("mailto:user@example.com");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), opaqueUri);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartHierarchicalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		URI opaqueUri = URI.create("mailto:user@example.com");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), opaqueUri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartHierarchicalConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("mailto:user@example.com"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHierarchicalSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.hierarchical();
		URI hierarchicalUri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), hierarchicalUri);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithoutQueryConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		URI withQuery = URI.create("https://example.com/path?query=value");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withQuery);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithoutQueryConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path?query=value"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithoutQuerySuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutQuery();
		URI withoutQuery = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withoutQuery);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithoutUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		URI withUserInfo = URI.create("https://user:password@example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withUserInfo);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithoutUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://user:password@example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithoutUserInfoSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.withoutUserInfo();
		URI withoutUserInfo = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), withoutUserInfo);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		URI wrongUserInfo = URI.create("https://user@example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), wrongUserInfo);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUserInfoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://user@example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartUserInfoSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.userInfo(builder -> builder.equalTo("admin"));
		URI correctUserInfo = URI.create("https://admin@example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), correctUserInfo);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> Result.error("Custom validation failed"));
		URI uri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> Result.error("Custom validation failed"));
		
		Result<URI> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("https://example.com/path"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<URI> codec = Codecs.URI.custom(value -> Result.success());
		URI uri = URI.create("https://example.com/path");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), uri);
		assertTrue(result.isSuccess());
	}
}
