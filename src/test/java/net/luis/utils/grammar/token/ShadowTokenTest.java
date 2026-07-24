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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShadowToken}.<br>
 *
 * @author Luis-St
 */
class ShadowTokenTest {
	
	@Test
	void constructWithToken() {
		Token base = SimpleToken.createUnpositioned("v");
		ShadowToken shadow = new ShadowToken(base);
		assertEquals(base, shadow.token());
	}
	
	@Test
	void constructWithNullTokenThrows() {
		assertThrows(NullPointerException.class, () -> new ShadowToken(null));
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		ShadowToken shadow = new ShadowToken(SimpleToken.createUnpositioned("hello"));
		assertEquals("hello", shadow.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		Token base = new SimpleToken("v", new TokenPosition(0, 0, 0), Set.of());
		ShadowToken shadow = new ShadowToken(base);
		assertEquals(base.position(), shadow.position());
	}
	
	@Test
	void typesDelegatesToWrappedToken() {
		Token base = new SimpleToken("v", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		ShadowToken shadow = new ShadowToken(base);
		assertEquals(base.types(), shadow.types());
	}
	
	@Test
	void shadowOnShadowTokenReturnsSelf() {
		ShadowToken shadow = new ShadowToken(SimpleToken.createUnpositioned("v"));
		assertSame(shadow, shadow.shadow());
	}
	
	@Test
	void unshadowReturnsWrappedToken() {
		Token base = SimpleToken.createUnpositioned("v");
		ShadowToken shadow = new ShadowToken(base);
		assertSame(base, shadow.unshadow());
	}
	
	@Test
	void doubleShadowingThenUnshadowingUnwrapsOnlyOneLevel() {
		Token base = SimpleToken.createUnpositioned("v");
		Token inner = new ShadowToken(base);
		Token outer = new ShadowToken(inner);
		
		assertSame(inner, outer.unshadow());
		assertSame(base, inner.unshadow());
	}
}
