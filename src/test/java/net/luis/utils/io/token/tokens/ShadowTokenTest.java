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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShadowToken}.<br>
 *
 * @author Luis-St
 */
class ShadowTokenTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullToken() {
		assertThrows(NullPointerException.class, () -> new ShadowToken(null));
	}
	
	@Test
	void constructorWithValidToken() {
		Token token = createToken("test");
		
		assertDoesNotThrow(() -> new ShadowToken(token));
	}
	
	@Test
	void tokenReturnsWrappedToken() {
		Token originalToken = createToken("original");
		ShadowToken shadow = new ShadowToken(originalToken);
		
		assertSame(originalToken, shadow.token());
	}
	
	@Test
	void definitionDelegatesToWrappedToken() {
		Token token = createToken("test");
		ShadowToken shadow = new ShadowToken(token);
		
		assertEquals(token.definition(), shadow.definition());
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		Token token = createToken("testValue");
		ShadowToken shadow = new ShadowToken(token);
		
		assertEquals("testValue", shadow.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		Token token = createToken("test");
		ShadowToken shadow = new ShadowToken(token);
		
		assertEquals(token.position(), shadow.position());
	}
	
	@Test
	void shadowReturnsSelf() {
		Token token = createToken("test");
		ShadowToken shadow = new ShadowToken(token);
		
		Token doubleShadow = shadow.shadow();
		
		assertSame(shadow, doubleShadow);
	}
	
	@Test
	void unshadowReturnsWrappedToken() {
		Token originalToken = createToken("test");
		ShadowToken shadow = new ShadowToken(originalToken);
		
		Token unshadowed = shadow.unshadow();
		
		assertSame(originalToken, unshadowed);
	}
	
	@Test
	void shadowTokenImplementsTokenInterface() {
		Token token = createToken("test");
		ShadowToken shadow = new ShadowToken(token);
		
		assertInstanceOf(Token.class, shadow);
	}
	
	@Test
	void wrappingShadowToken() {
		Token baseToken = createToken("base");
		ShadowToken firstLevel = new ShadowToken(baseToken);
		ShadowToken secondLevel = new ShadowToken(firstLevel);
		
		assertEquals(firstLevel, secondLevel.token());
		assertEquals(baseToken, firstLevel.token());
		
		assertEquals(firstLevel, secondLevel.unshadow());
		assertEquals(baseToken, firstLevel.unshadow());
	}
	
	@Test
	void shadowTokenWithDifferentTokenTypes() {
		Token simpleToken = createToken("simple");
		IndexedToken indexedToken = new IndexedToken(createToken("indexed"), 5);
		AnnotatedToken annotatedToken = AnnotatedToken.of(createToken("annotated"), "type", "test");
		
		ShadowToken shadowSimple = new ShadowToken(simpleToken);
		ShadowToken shadowIndexed = new ShadowToken(indexedToken);
		ShadowToken shadowAnnotated = new ShadowToken(annotatedToken);
		
		assertEquals(simpleToken, shadowSimple.token());
		assertEquals(indexedToken, shadowIndexed.token());
		assertEquals(annotatedToken, shadowAnnotated.token());
		
		assertEquals("simple", shadowSimple.value());
		assertEquals("indexed", shadowIndexed.value());
		assertEquals("annotated", shadowAnnotated.value());
	}
	
	@Test
	void shadowTokenPreservesTokenProperties() {
		Token originalToken = createToken("original");
		ShadowToken shadow = new ShadowToken(originalToken);
		
		assertEquals(originalToken.definition(), shadow.definition());
		assertEquals(originalToken.value(), shadow.value());
		assertEquals(originalToken.position(), shadow.position());
	}
	
	@Test
	void shadowTokenWithSpecialCharacters() {
		Token spaceToken = createToken(" ");
		Token tabToken = createToken("\t");
		Token newlineToken = createToken("\n");
		Token emptyToken = createToken("");
		
		ShadowToken shadowSpace = new ShadowToken(spaceToken);
		ShadowToken shadowTab = new ShadowToken(tabToken);
		ShadowToken shadowNewline = new ShadowToken(newlineToken);
		ShadowToken shadowEmpty = new ShadowToken(emptyToken);
		
		assertEquals(" ", shadowSpace.value());
		assertEquals("\t", shadowTab.value());
		assertEquals("\n", shadowNewline.value());
		assertEquals("", shadowEmpty.value());
		
		assertEquals(spaceToken, shadowSpace.unshadow());
		assertEquals(tabToken, shadowTab.unshadow());
		assertEquals(newlineToken, shadowNewline.unshadow());
		assertEquals(emptyToken, shadowEmpty.unshadow());
	}
	
	@Test
	void nestedShadowTokens() {
		Token baseToken = createToken("nested");
		ShadowToken level1 = new ShadowToken(baseToken);
		ShadowToken level2 = new ShadowToken(level1);
		ShadowToken level3 = new ShadowToken(level2);
		
		assertEquals(level2, level3.token());
		assertEquals(level1, level2.token());
		assertEquals(baseToken, level1.token());
		
		assertEquals(level2, level3.unshadow());
		assertEquals(level1, level2.unshadow());
		assertEquals(baseToken, level1.unshadow());
		
		assertEquals(baseToken.value(), level1.value());
		assertEquals(baseToken.value(), level2.value());
		assertEquals(baseToken.value(), level3.value());
		
		assertEquals(baseToken.definition(), level1.definition());
		assertEquals(baseToken.definition(), level2.definition());
		assertEquals(baseToken.definition(), level3.definition());
	}
	
	@Test
	void shadowTokenWithIndexing() {
		Token baseToken = createToken("test");
		ShadowToken shadow = new ShadowToken(baseToken);
		
		Token indexed = shadow.index(5);
		
		IndexedToken indexedToken = assertInstanceOf(IndexedToken.class, indexed);
		assertEquals(shadow, indexedToken.token());
		assertEquals(5, indexedToken.index());
	}
	
	@Test
	void shadowTokenWithAnnotation() {
		Token baseToken = createToken("test");
		ShadowToken shadow = new ShadowToken(baseToken);
		Map<String, Object> metadata = Map.of("shadowed", true);
		
		Token annotated = shadow.annotate(metadata);
		
		AnnotatedToken annotatedToken = assertInstanceOf(AnnotatedToken.class, annotated);
		assertEquals(shadow, annotatedToken.token());
		assertEquals(metadata, annotatedToken.metadata());
		assertEquals(true, annotatedToken.getMetadata("shadowed"));
	}
	
	@Test
	void complexWrappingWithShadow() {
		Token baseToken = createToken("complex");
		
		Token indexed = baseToken.index(10);
		Token annotated = indexed.annotate(Map.of("type", "complex"));
		
		Token shadow = annotated.shadow();
		ShadowToken shadowToken = assertInstanceOf(ShadowToken.class, shadow);
		
		Token unshadowed = shadowToken.unshadow();
		AnnotatedToken annotatedToken = assertInstanceOf(AnnotatedToken.class, unshadowed);
		assertEquals("complex", annotatedToken.getMetadata("type"));
		
		IndexedToken indexedToken = assertInstanceOf(IndexedToken.class, annotatedToken.token());
		assertEquals(10, indexedToken.index());
		assertEquals(baseToken, indexedToken.token());
	}
	
	@Test
	void shadowTokenEquality() {
		Token token1 = createToken("test");
		Token token2 = createToken("test");
		
		ShadowToken shadow1 = new ShadowToken(token1);
		ShadowToken shadow2 = new ShadowToken(token1);
		ShadowToken shadow3 = new ShadowToken(token2);
		
		assertEquals(shadow1, shadow2);
		assertEquals(shadow1.hashCode(), shadow2.hashCode());
	}
	
	@Test
	void toStringContainsTokenInfo() {
		Token token = createToken("test");
		ShadowToken shadow = new ShadowToken(token);
		String shadowString = shadow.toString();
		
		assertTrue(shadowString.contains("ShadowToken"));
		assertTrue(shadowString.contains("token"));
	}
	
	@Test
	void multipleShadowOperationsOnSameToken() {
		Token baseToken = createToken("base");
		
		ShadowToken shadow1 = new ShadowToken(baseToken);
		ShadowToken shadow2 = assertInstanceOf(ShadowToken.class, shadow1.shadow());
		ShadowToken shadow3 = assertInstanceOf(ShadowToken.class, shadow2.shadow());
		
		assertSame(shadow1, shadow2);
		assertSame(shadow1, shadow3);
		assertSame(shadow2, shadow3);
	}
	
	@Test
	void unshadowChain() {
		Token baseToken = createToken("chain");
		ShadowToken level1 = new ShadowToken(baseToken);
		ShadowToken level2 = new ShadowToken(level1);
		ShadowToken level3 = new ShadowToken(level2);
		
		Token unshadow1 = level3.unshadow();
		Token unshadow2 = unshadow1.unshadow();
		Token unshadow3 = unshadow2.unshadow();
		
		assertEquals(level2, unshadow1);
		assertEquals(level1, unshadow2);
		assertEquals(baseToken, unshadow3);
		
		Token unshadow4 = unshadow3.unshadow();
		assertSame(baseToken, unshadow4);
	}
}
