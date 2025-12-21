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

package net.luis.utils.io.token.tokens;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnnotatedToken}.<br>
 *
 * @author Luis-St
 */
class AnnotatedTokenTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullToken() {
		Map<String, Object> metadata = Map.of("key", "value");
		
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(null, metadata));
	}
	
	@Test
	void constructorWithNullMetadata() {
		Token token = createToken("test");
		
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(token, null));
	}
	
	@Test
	void constructorWithNullValue() {
		Token token = createToken("test");
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("nullable", null);
		
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(token, metadata));
	}
	
	@Test
	void constructorWithValidParameters() {
		Token token = createToken("test");
		Map<String, Object> metadata = Map.of("key", "value");
		
		assertDoesNotThrow(() -> new AnnotatedToken(token, metadata));
	}
	
	@Test
	void constructorWithEmptyMetadata() {
		Token token = createToken("test");
		Map<String, Object> emptyMetadata = Map.of();
		
		assertDoesNotThrow(() -> new AnnotatedToken(token, emptyMetadata));
	}
	
	@Test
	void constructorMakesImmutableCopyOfMetadata() {
		Token token = createToken("test");
		Map<String, Object> mutableMetadata = new HashMap<>();
		mutableMetadata.put("key", "value");
		
		AnnotatedToken annotatedToken = new AnnotatedToken(token, mutableMetadata);
		mutableMetadata.put("newKey", "newValue");
		
		assertFalse(annotatedToken.metadata().containsKey("newKey"));
		assertThrows(UnsupportedOperationException.class, () -> annotatedToken.metadata().put("test", "test"));
	}
	
	@Test
	void ofWithNullToken() {
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(null, "key", "value"));
	}
	
	@Test
	void ofWithNullKey() {
		Token token = createToken("test");
		
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(token, null, "value"));
	}
	
	@Test
	void ofWithValidParameters() {
		Token token = createToken("test");
		
		AnnotatedToken annotated = AnnotatedToken.of(token, "type", "identifier");
		
		assertEquals(token, annotated.token());
		assertEquals(1, annotated.metadata().size());
		assertEquals("identifier", annotated.metadata().get("type"));
	}
	
	@Test
	void ofWithNullValue() {
		Token token = createToken("test");
		
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(token, "nullable", null));
	}
	
	@Test
	void emptyWithNullToken() {
		assertThrows(NullPointerException.class, () -> AnnotatedToken.empty(null));
	}
	
	@Test
	void emptyWithValidToken() {
		Token token = createToken("test");
		
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertEquals(token, annotated.token());
		assertTrue(annotated.metadata().isEmpty());
	}
	
	@Test
	void tokenReturnsWrappedToken() {
		Token originalToken = createToken("original");
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotatedToken annotated = new AnnotatedToken(originalToken, metadata);
		
		assertSame(originalToken, annotated.token());
	}
	
	@Test
	void metadataReturnsCorrectMap() {
		Token token = createToken("test");
		Map<String, Object> originalMetadata = Map.of("type", "keyword", "line", 5);
		AnnotatedToken annotated = new AnnotatedToken(token, originalMetadata);
		
		assertEquals(originalMetadata, annotated.metadata());
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		Token token = createToken("testValue");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertEquals("testValue", annotated.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertEquals(token.position(), annotated.position());
	}
	
	@Test
	void getMetadataWithNullKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "key", "value");
		
		assertThrows(NullPointerException.class, () -> annotated.getMetadata(null));
	}
	
	@Test
	void getMetadataWithExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "type", "identifier");
		
		assertEquals("identifier", annotated.getMetadata("type"));
	}
	
	@Test
	void getMetadataWithNonExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "type", "identifier");
		
		assertNull(annotated.getMetadata("nonExisting"));
	}
	
	@Test
	void getMetadataWithDefaultValueAndNullKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertThrows(NullPointerException.class, () -> annotated.getMetadata(null, "default"));
	}
	
	@Test
	void getMetadataWithDefaultValueAndExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "count", 42);
		
		assertEquals(42, annotated.getMetadata("count", 0));
	}
	
	@Test
	void getMetadataWithDefaultValueAndNonExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertEquals("default", annotated.getMetadata("missing", "default"));
	}
	
	@Test
	void hasMetadataWithNullKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "key", "value");
		
		assertThrows(NullPointerException.class, () -> annotated.hasMetadata(null));
	}
	
	@Test
	void hasMetadataWithExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "exists", "yes");
		
		assertTrue(annotated.hasMetadata("exists"));
	}
	
	@Test
	void hasMetadataWithNonExistingKey() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertFalse(annotated.hasMetadata("doesNotExist"));
	}
	
	@Test
	void metadataWithComplexValues() {
		Token token = createToken("test");
		Map<String, Object> complexMetadata = Map.of(
			"string", "value",
			"number", 42,
			"boolean", true,
			"list", java.util.List.of("a", "b", "c"),
			"nested", Map.of("inner", "value")
		);
		
		AnnotatedToken annotated = new AnnotatedToken(token, complexMetadata);
		
		assertEquals("value", annotated.getMetadata("string"));
		assertEquals(42, annotated.getMetadata("number"));
		assertEquals(true, annotated.getMetadata("boolean"));
		assertEquals(List.of("a", "b", "c"), annotated.getMetadata("list"));
		assertEquals(Map.of("inner", "value"), annotated.getMetadata("nested"));
	}
	
	@Test
	void annotatedTokenImplementsTokenInterface() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.empty(token);
		
		assertInstanceOf(Token.class, annotated);
	}
	
	@Test
	void wrappingAnnotatedToken() {
		Token baseToken = createToken("base");
		AnnotatedToken firstLevel = AnnotatedToken.of(baseToken, "level", 1);
		AnnotatedToken secondLevel = AnnotatedToken.of(firstLevel, "level", 2);
		
		assertEquals(firstLevel, secondLevel.token());
		assertEquals(2, secondLevel.getMetadata("level"));
		assertEquals(baseToken, firstLevel.token());
		assertEquals(1, firstLevel.getMetadata("level"));
	}
	
	@Test
	void metadataTypeCasting() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "count", 42);
		
		Integer count = annotated.getMetadata("count", 0);
		assertEquals(42, count);
		
		String defaultString = annotated.getMetadata("missing", "default");
		assertEquals("default", defaultString);
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenReturnsSelf() {
		Token baseToken = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(baseToken, "type", "identifier");
		Map<String, Object> newMetadata = Map.of("newKey", "newValue");
		
		Token result = annotated.annotate(newMetadata);
		
		assertSame(annotated, result);
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenWithNullMap() {
		Token baseToken = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(baseToken, "type", "identifier");
		
		assertDoesNotThrow(() -> annotated.annotate(null));
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenWithEmptyMap() {
		Token baseToken = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(baseToken, "type", "identifier");
		Map<String, Object> emptyMap = Map.of();
		
		Token result = annotated.annotate(emptyMap);
		
		assertSame(annotated, result);
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenWithComplexMap() {
		Token baseToken = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(baseToken, "type", "identifier");
		Map<String, Object> complexMetadata = Map.of(
			"newType", "keyword",
			"line", 10,
			"validated", true,
			"properties", List.of("a", "b", "c")
		);
		
		Token result = annotated.annotate(complexMetadata);
		
		assertSame(annotated, result);
		assertEquals("identifier", annotated.getMetadata("type"));
		assertNull(annotated.getMetadata("newType"));
	}
	
	@Test
	void multipleAnnotateCallsOnSameToken() {
		Token baseToken = createToken("test");
		AnnotatedToken annotated1 = AnnotatedToken.of(baseToken, "first", "value1");
		
		Token annotated2 = annotated1.annotate(Map.of("second", "value2"));
		Token annotated3 = annotated2.annotate(Map.of("third", "value3"));
		Token annotated4 = annotated3.annotate(Map.of("fourth", "value4"));
		
		assertSame(annotated1, annotated2);
		assertSame(annotated1, annotated3);
		assertSame(annotated1, annotated4);
		assertSame(annotated2, annotated3);
		assertSame(annotated2, annotated4);
		assertSame(annotated3, annotated4);
		
		assertEquals("value1", annotated1.getMetadata("first"));
		assertNull(annotated1.getMetadata("second"));
		assertNull(annotated1.getMetadata("third"));
		assertNull(annotated1.getMetadata("fourth"));
	}
	
	@Test
	void annotateNestedAnnotatedTokens() {
		Token baseToken = createToken("test");
		AnnotatedToken level1 = AnnotatedToken.of(baseToken, "level", 1);
		AnnotatedToken level2 = AnnotatedToken.of(level1, "level", 2);
		AnnotatedToken level3 = AnnotatedToken.of(level2, "level", 3);
		
		assertEquals(level1, level2.token());
		assertEquals(level2, level3.token());
		
		Token result1 = level1.annotate(Map.of("new", "value"));
		Token result2 = level2.annotate(Map.of("new", "value"));
		Token result3 = level3.annotate(Map.of("new", "value"));
		
		assertSame(level1, result1);
		assertSame(level2, result2);
		assertSame(level3, result3);
	}
	
	@Test
	void toStringContainsTokenInfo() {
		Token token = createToken("test");
		AnnotatedToken annotated = AnnotatedToken.of(token, "type", "test");
		String tokenString = annotated.toString();
		
		assertTrue(tokenString.contains("AnnotatedToken"));
		assertTrue(tokenString.contains("token"));
		assertTrue(tokenString.contains("metadata"));
	}
	
	@Test
	void equalAnnotatedTokensHaveSameHashCode() {
		Token token = createToken("test");
		Map<String, Object> metadata = Map.of("key", "value");
		AnnotatedToken annotated1 = new AnnotatedToken(token, metadata);
		AnnotatedToken annotated2 = new AnnotatedToken(token, metadata);
		
		assertEquals(annotated1.hashCode(), annotated2.hashCode());
	}
}
