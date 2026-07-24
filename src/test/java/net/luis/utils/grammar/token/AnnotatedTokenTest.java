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

package net.luis.utils.grammar.token;

import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnnotatedToken}.<br>
 *
 * @author Luis-St
 */
class AnnotatedTokenTest {
	
	@Test
	void constructWithTokenAndMetadata() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of("k", "v"));
		assertEquals(base, token.token());
		assertEquals(Map.of("k", "v"), token.metadata());
	}
	
	@Test
	void constructWithNullTokenThrows() {
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(null, Map.of()));
	}
	
	@Test
	void constructWithNullMetadataThrows() {
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(SimpleToken.createUnpositioned("v"), null));
	}
	
	@Test
	void ofWithNullTokenThrows() {
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(null, "k", "v"));
	}
	
	@Test
	void ofWithNullKeyThrows() {
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(SimpleToken.createUnpositioned("v"), null, "v"));
	}
	
	@Test
	void emptyWithNullTokenThrows() {
		assertThrows(NullPointerException.class, () -> AnnotatedToken.empty(null));
	}
	
	@Test
	void getMetadataWithNullKeyThrows() {
		AnnotatedToken token = AnnotatedToken.empty(SimpleToken.createUnpositioned("v"));
		assertThrows(NullPointerException.class, () -> token.getMetadata(null));
	}
	
	@Test
	void getMetadataWithDefaultAndNullKeyThrows() {
		AnnotatedToken token = AnnotatedToken.empty(SimpleToken.createUnpositioned("v"));
		assertThrows(NullPointerException.class, () -> token.getMetadata(null, "default"));
	}
	
	@Test
	void hasMetadataWithNullKeyThrows() {
		AnnotatedToken token = AnnotatedToken.empty(SimpleToken.createUnpositioned("v"));
		assertThrows(NullPointerException.class, () -> token.hasMetadata(null));
	}
	
	@Test
	void constructWithMetadataContainingNullValueThrows() {
		Map<String, Object> withNullValue = new HashMap<>();
		withNullValue.put("k", null);
		assertThrows(NullPointerException.class, () -> new AnnotatedToken(SimpleToken.createUnpositioned("v"), withNullValue));
	}
	
	@Test
	void getMetadataReturnsValueWhenKeyPresent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of("k", "v"));
		assertEquals("v", token.getMetadata("k"));
	}
	
	@Test
	void getMetadataReturnsNullWhenKeyAbsent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of());
		assertNull(token.getMetadata("missing"));
	}
	
	@Test
	void getMetadataWithDefaultReturnsValueWhenKeyPresent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of("k", "v"));
		assertEquals("v", token.getMetadata("k", "fallback"));
	}
	
	@Test
	void getMetadataWithDefaultReturnsDefaultWhenKeyAbsent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of());
		assertEquals("fallback", token.getMetadata("missing", "fallback"));
	}
	
	@Test
	void hasMetadataReturnsTrueWhenKeyPresent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of("k", "v"));
		assertTrue(token.hasMetadata("k"));
	}
	
	@Test
	void hasMetadataReturnsFalseWhenKeyAbsent() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of());
		assertFalse(token.hasMetadata("missing"));
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenReturnsSelf() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken annotated = new AnnotatedToken(base, Map.of());
		assertSame(annotated, annotated.annotate(Map.of("new", "value")));
	}
	
	@Test
	void annotateOnAlreadyAnnotatedTokenWithNullArgumentReturnsSelf() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken annotated = new AnnotatedToken(base, Map.of());
		assertSame(annotated, annotated.annotate(null));
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		AnnotatedToken token = new AnnotatedToken(SimpleToken.createUnpositioned("hello"), Map.of());
		assertEquals("hello", token.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		TokenPosition position = new TokenPosition(1, 2, 3);
		Token base = new SimpleToken("hello", position);
		AnnotatedToken token = new AnnotatedToken(base, Map.of());
		assertEquals(position, token.position());
	}
	
	@Test
	void typesDelegatesToWrappedToken() {
		Token base = new SimpleToken("hello", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.KEYWORD));
		AnnotatedToken token = new AnnotatedToken(base, Map.of());
		assertEquals(Set.of(StandardTokenType.KEYWORD), token.types());
	}
	
	@Test
	void ofCreatesTokenWithSingleMetadataEntry() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = AnnotatedToken.of(base, "key", 42);
		assertTrue(token.hasMetadata("key"));
		assertEquals(42, token.getMetadata("key"));
	}
	
	@Test
	void emptyCreatesTokenWithNoMetadata() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = AnnotatedToken.empty(base);
		assertTrue(token.metadata().isEmpty());
	}
	
	@Test
	void constructorDefensivelyCopiesMetadataMap() {
		Token base = SimpleToken.createUnpositioned("v");
		Map<String, Object> mutable = new HashMap<>();
		mutable.put("k", "v");
		AnnotatedToken token = new AnnotatedToken(base, mutable);
		mutable.put("k2", "v2");
		assertEquals(Map.of("k", "v"), token.metadata());
		assertFalse(token.hasMetadata("k2"));
	}
	
	@Test
	void ofWithNullValueThrows() {
		Token base = SimpleToken.createUnpositioned("v");
		assertThrows(NullPointerException.class, () -> AnnotatedToken.of(base, "key", null));
	}
	
	@Test
	void metadataAccessorReturnsImmutableMap() {
		Token base = SimpleToken.createUnpositioned("v");
		AnnotatedToken token = new AnnotatedToken(base, Map.of("k", "v"));
		Map<String, Object> metadata = token.metadata();
		assertThrows(UnsupportedOperationException.class, () -> metadata.put("k2", "v2"));
	}
}
