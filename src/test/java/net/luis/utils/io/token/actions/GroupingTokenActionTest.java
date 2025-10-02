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

package net.luis.utils.io.token.actions;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingTokenAction}.<br/>
 *
 * @author Luis-St
 */
class GroupingTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void instanceIsSingleton() {
		GroupingTokenAction instance1 = GroupingTokenAction.INSTANCE;
		GroupingTokenAction instance2 = GroupingTokenAction.INSTANCE;
		
		assertSame(instance1, instance2);
	}
	
	@Test
	void applyWithNullMatch() {
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> GroupingTokenAction.INSTANCE.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> GroupingTokenAction.INSTANCE.apply(match, null));
	}
	
	@Test
	void applyWithEmptyTokens() {
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		assertThrows(IllegalArgumentException.class, () -> GroupingTokenAction.INSTANCE.apply(match, context));
	}
	
	@Test
	void applyWithSingleToken() {
		List<Token> tokens = List.of(createToken("single"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		assertThrows(IllegalArgumentException.class, () -> GroupingTokenAction.INSTANCE.apply(match, context));
	}
	
	@Test
	void applyWithMultipleTokens() {
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = GroupingTokenAction.INSTANCE.apply(match, context);
		assertEquals(1, result.size());
		
		
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals(2, group.tokens().size());
		assertEquals("hello", group.tokens().get(0).value());
		assertEquals("world", group.tokens().get(1).value());
	}
	
	@Test
	void applyReturnsImmutableList() {
		List<Token> tokens = List.of(createToken("test"), createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = GroupingTokenAction.INSTANCE.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyGroupsTokensInCorrectOrder() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = GroupingTokenAction.INSTANCE.apply(match, context);
		TokenGroup group = (TokenGroup) result.getFirst();
		
		assertEquals("first", group.tokens().get(0).value());
		assertEquals("second", group.tokens().get(1).value());
		assertEquals("third", group.tokens().get(2).value());
	}
}
