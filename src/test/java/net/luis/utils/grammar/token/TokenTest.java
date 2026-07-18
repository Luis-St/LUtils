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

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Token}.<br>
 *
 * @author Luis-St
 */
class TokenTest {
	
	@Test
	void indexWithNegativeIndexThrows() {
		Token token = SimpleToken.createUnpositioned("value");
		assertThrows(IllegalArgumentException.class, () -> token.index(-1));
	}
	
	@Test
	void annotateWithNullAnnotationsThrows() {
		Token token = SimpleToken.createUnpositioned("value");
		assertThrows(NullPointerException.class, () -> token.annotate(null));
	}
	
	@Test
	void indexWrapsTokenInIndexedToken() {
		Token token = SimpleToken.createUnpositioned("value");
		Token indexed = token.index(3);
		
		assertInstanceOf(IndexedToken.class, indexed);
		assertEquals(3, ((IndexedToken) indexed).index());
		assertSame(token, ((IndexedToken) indexed).token());
	}
	
	@Test
	void annotateWrapsTokenInAnnotatedToken() {
		Token token = SimpleToken.createUnpositioned("value");
		Token annotated = token.annotate(Map.of("k", "v"));
		
		assertInstanceOf(AnnotatedToken.class, annotated);
		assertEquals("v", ((AnnotatedToken) annotated).getMetadata("k"));
	}
	
	@Test
	void shadowWrapsTokenInShadowToken() {
		Token token = SimpleToken.createUnpositioned("value");
		Token shadowed = token.shadow();
		
		assertInstanceOf(ShadowToken.class, shadowed);
		assertSame(token, ((ShadowToken) shadowed).token());
	}
	
	@Test
	void unshadowOnNonShadowTokenReturnsSelf() {
		Token token = SimpleToken.createUnpositioned("value");
		
		assertSame(token, token.unshadow());
	}
	
	@Test
	void indexWithZeroIndexSucceeds() {
		Token token = SimpleToken.createUnpositioned("value");
		Token result = token.index(0);
		
		assertInstanceOf(IndexedToken.class, result);
		assertEquals(0, ((IndexedToken) result).index());
	}
	
	@Test
	void indexOnIndexedTokenComparedToDirectConstruction() {
		Token token = SimpleToken.createUnpositioned("v");
		IndexedToken viaDefault = (IndexedToken) token.index(2);
		IndexedToken viaConstructor = new IndexedToken(token, 2);
		
		assertEquals(viaConstructor, viaDefault);
	}
	
	@Test
	void chainedIndexAnnotateShadowProducesNestedWrappers() {
		Token base = SimpleToken.createUnpositioned("v");
		Token result = base.index(1).annotate(Map.of("a", 1)).shadow();
		
		assertInstanceOf(ShadowToken.class, result);
		Token unwrapped = ((ShadowToken) result).token();
		assertInstanceOf(AnnotatedToken.class, unwrapped);
		Token indexed = ((AnnotatedToken) unwrapped).token();
		assertInstanceOf(IndexedToken.class, indexed);
		assertEquals(1, ((IndexedToken) indexed).index());
		assertSame(base, ((IndexedToken) indexed).token());
	}
}
