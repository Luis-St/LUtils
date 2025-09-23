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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AlwaysMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class AlwaysMatchTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void instanceIsSingleton() {
		AlwaysMatchTokenRule instance1 = AlwaysMatchTokenRule.INSTANCE;
		AlwaysMatchTokenRule instance2 = AlwaysMatchTokenRule.INSTANCE;
		
		assertSame(instance1, instance2);
	}
	
	@Test
	void matchWithNullStream() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithNoTokensAvailable() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNull(match);
	}
	
	@Test
	void matchWithTokensAvailable() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals("test", match.matchedTokens().getFirst().value());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithMultipleTokens() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, context);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals("first", match.matchedTokens().getFirst().value());
	}
	
	@Test
	void not() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRule negated = rule.not();
		
		assertEquals(NeverMatchTokenRule.INSTANCE, negated);
	}
}
