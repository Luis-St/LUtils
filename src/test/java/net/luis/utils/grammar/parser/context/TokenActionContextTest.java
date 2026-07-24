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

package net.luis.utils.grammar.parser.context;

import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenActionContext}.<br>
 *
 * @author Luis-St
 */
class TokenActionContextTest {
	
	@Test
	void constructWithImmutableStream() {
		TokenStream stream = TokenStream.createImmutable(List.of());
		TokenActionContext context = new TokenActionContext(stream);
		assertEquals(stream, context.stream());
	}
	
	@Test
	void constructWithNullStream() {
		assertThrows(NullPointerException.class, () -> new TokenActionContext(null));
	}
	
	@Test
	void constructWithMutableStreamThrows() {
		TokenStream stream = TokenStream.createMutable(List.of());
		assertThrows(IllegalArgumentException.class, () -> new TokenActionContext(stream));
	}
	
	@Test
	void constructWithImmutableStreamSucceeds() {
		TokenActionContext context = assertDoesNotThrow(() -> new TokenActionContext(TokenStream.EMPTY));
		assertEquals(TokenStream.EMPTY, context.stream());
	}
	
	@Test
	void constructWithNonEmptyImmutableStream() {
		Token token1 = new SimpleToken("a", new TokenPosition(0, 0, 0), Set.of());
		Token token2 = new SimpleToken("b", new TokenPosition(0, 1, 1), Set.of());
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(token1, token2)));
		assertTrue(context.stream().getAllTokens().contains(token1));
		assertTrue(context.stream().getAllTokens().contains(token2));
	}
}
