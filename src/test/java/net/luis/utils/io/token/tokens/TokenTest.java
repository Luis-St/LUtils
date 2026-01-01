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

package net.luis.utils.io.token.tokens;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Token} interface default methods.<br>
 *
 * @author Luis-St
 */
class TokenTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void indexWithValidIndex() {
		Token token = createToken("test");
		
		Token indexed = token.index(5);
		
		IndexedToken indexedToken = assertInstanceOf(IndexedToken.class, indexed);
		assertEquals(token, indexedToken.token());
		assertEquals(5, indexedToken.index());
	}
	
	@Test
	void indexWithZero() {
		Token token = createToken("test");
		
		Token indexed = token.index(0);
		
		IndexedToken indexedToken = assertInstanceOf(IndexedToken.class, indexed);
		assertEquals(token, indexedToken.token());
		assertEquals(0, indexedToken.index());
		assertTrue(indexedToken.isFirst());
	}
	
	@Test
	void indexWithNegativeIndex() {
		Token token = createToken("test");
		
		assertThrows(IllegalArgumentException.class, () -> token.index(-1));
		assertThrows(IllegalArgumentException.class, () -> token.index(-5));
	}
	
	@Test
	void indexWithLargeIndex() {
		Token token = createToken("test");
		
		Token indexed = token.index(Integer.MAX_VALUE);
		
		assertInstanceOf(IndexedToken.class, indexed);
		IndexedToken indexedToken = (IndexedToken) indexed;
		assertEquals(Integer.MAX_VALUE, indexedToken.index());
	}
	
	@Test
	void annotateWithNullMap() {
		Token token = createToken("test");
		
		assertThrows(NullPointerException.class, () -> token.annotate(null));
	}
	
	@Test
	void annotateWithEmptyMap() {
		Token token = createToken("test");
		Map<String, Object> emptyMap = Map.of();
		
		Token annotated = token.annotate(emptyMap);
		
		AnnotatedToken annotatedToken = assertInstanceOf(AnnotatedToken.class, annotated);
		assertEquals(token, annotatedToken.token());
		assertTrue(annotatedToken.metadata().isEmpty());
	}
	
	@Test
	void annotateWithSingleEntry() {
		Token token = createToken("test");
		Map<String, Object> metadata = Map.of("type", "keyword");
		
		Token annotated = token.annotate(metadata);
		
		AnnotatedToken annotatedToken = assertInstanceOf(AnnotatedToken.class, annotated);
		assertEquals(token, annotatedToken.token());
		assertEquals(metadata, annotatedToken.metadata());
		assertEquals("keyword", annotatedToken.getMetadata("type"));
	}
	
	@Test
	void annotateWithMultipleEntries() {
		Token token = createToken("test");
		Map<String, Object> metadata = Map.of(
			"type", "identifier",
			"line", 10,
			"column", 5,
			"validated", true
		);
		
		Token annotated = token.annotate(metadata);
		
		AnnotatedToken annotatedToken = assertInstanceOf(AnnotatedToken.class, annotated);
		assertEquals(token, annotatedToken.token());
		assertEquals(metadata, annotatedToken.metadata());
		assertEquals("identifier", annotatedToken.getMetadata("type"));
		assertEquals(10, annotatedToken.getMetadata("line"));
		assertEquals(5, annotatedToken.getMetadata("column"));
		assertEquals(true, annotatedToken.getMetadata("validated"));
	}
	
	@Test
	void shadowCreatesCorrectToken() {
		Token token = createToken("test");
		
		Token shadowed = token.shadow();
		
		ShadowToken shadowToken = assertInstanceOf(ShadowToken.class, shadowed);
		assertEquals(token, shadowToken.token());
	}
	
	@Test
	void shadowPreservesTokenProperties() {
		Token token = createToken("test");
		
		Token shadowed = token.shadow();
		
		assertEquals(token.value(), shadowed.value());
		assertEquals(token.position(), shadowed.position());
	}
	
	@Test
	void unshadowOnNormalTokenReturnsSelf() {
		Token token = createToken("test");
		
		Token unshadowed = token.unshadow();
		
		assertSame(token, unshadowed);
	}
	
	@Test
	void unshadowOnShadowTokenReturnsOriginal() {
		Token originalToken = createToken("test");
		Token shadowToken = originalToken.shadow();
		
		Token unshadowed = shadowToken.unshadow();
		
		assertSame(originalToken, unshadowed);
	}
	
	@Test
	void chainedOperations() {
		Token baseToken = createToken("test");
		
		Token result = baseToken
			.index(5)
			.annotate(Map.of("type", "test"))
			.shadow();
		
		assertInstanceOf(ShadowToken.class, result);
		
		Token unshadowed = result.unshadow();
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, unshadowed);
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, annotated.token());
		assertEquals(baseToken, indexed.token());
		assertEquals(5, indexed.index());
		assertEquals("test", annotated.getMetadata("type"));
	}
	
	@Test
	void chainedOperationsDifferentOrder() {
		Token baseToken = createToken("test");
		
		Token result = baseToken
			.shadow()
			.annotate(Map.of("processed", true))
			.index(10);
		
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, result);
		assertEquals(10, indexed.index());
		
		AnnotatedToken annotated = assertInstanceOf(AnnotatedToken.class, indexed.token());
		assertEquals(true, annotated.getMetadata("processed"));
		
		ShadowToken shadow = assertInstanceOf(ShadowToken.class, annotated.token());
		assertEquals(baseToken, shadow.token());
	}
	
	@Test
	void multipleIndexOperations() {
		Token token = createToken("test");
		
		Token indexed1 = token.index(1);
		Token indexed2 = indexed1.index(2);
		Token indexed3 = indexed2.index(3);
		
		assertEquals(1, assertInstanceOf(IndexedToken.class, indexed1).index());
		assertEquals(2, assertInstanceOf(IndexedToken.class, indexed2).index());
		assertEquals(3, assertInstanceOf(IndexedToken.class, indexed3).index());
	}
	
	@Test
	void multipleAnnotationOperations() {
		Token token = createToken("test");
		
		Token annotated1 = token.annotate(Map.of("first", "value1"));
		Token annotated2 = annotated1.annotate(Map.of("second", "value2"));
		
		assertInstanceOf(AnnotatedToken.class, annotated1);
		assertInstanceOf(AnnotatedToken.class, annotated2);
		
		assertSame(annotated1, annotated2);
	}
	
	@Test
	void multipleShadowOperations() {
		Token token = createToken("test");
		
		Token shadow1 = token.shadow();
		Token shadow2 = shadow1.shadow();
		
		assertInstanceOf(ShadowToken.class, shadow1);
		assertInstanceOf(ShadowToken.class, shadow2);
		
		assertSame(shadow1, shadow2);
	}
	
	@Test
	void complexChaining() {
		Token baseToken = createToken("complex");
		
		Token result = baseToken
			.index(0)
			.shadow()
			.annotate(Map.of("complexity", "high"))
			.unshadow()
			.index(1)
			.shadow()
			.unshadow();
		
		IndexedToken finalIndexed = assertInstanceOf(IndexedToken.class, result);
		assertEquals(1, finalIndexed.index());
		
		AnnotatedToken finalAnnotated = assertInstanceOf(AnnotatedToken.class, finalIndexed.token());
		assertEquals("high", finalAnnotated.getMetadata("complexity"));
		
		ShadowToken finalShadow = assertInstanceOf(ShadowToken.class, finalAnnotated.token());
		
		IndexedToken originalIndexed = assertInstanceOf(IndexedToken.class, finalShadow.token());
		assertEquals(0, originalIndexed.index());
		assertEquals(baseToken, originalIndexed.token());
	}
}
