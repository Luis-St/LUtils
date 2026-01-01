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

package net.luis.utils.io.token.actions;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenAction}.<br>
 *
 * @author Luis-St
 */
class TokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void applyWithNullMatch() {
		TokenAction action = (match, ctx) -> List.of(createToken("result"));
		
		assertDoesNotThrow(() -> action.apply(null, new TokenActionContext(TokenStream.createImmutable(List.of()))));
	}
	
	@Test
	void applyWithNullContext() {
		TokenAction action = (match, ctx) -> List.of(createToken("result"));
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertDoesNotThrow(() -> action.apply(match, null));
	}
	
	@Test
	void identityWithEmptyTokens() {
		TokenAction identity = TokenAction.identity();
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = identity.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void identityWithSingleToken() {
		TokenAction identity = TokenAction.identity();
		
		List<Token> tokens = List.of(createToken("single"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = identity.apply(match, context);
		
		assertEquals(1, result.size());
		assertEquals("single", result.getFirst().value());
	}
	
	@Test
	void identityWithValidTokens() {
		TokenAction identity = TokenAction.identity();
		
		List<Token> tokens = List.of(createToken("test"), createToken("value"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = identity.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("test", result.get(0).value());
		assertEquals("value", result.get(1).value());
		assertSame(tokens, result);
	}
	
	@Test
	void identityReturnsImmutableList() {
		TokenAction identity = TokenAction.identity();
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = identity.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
}
