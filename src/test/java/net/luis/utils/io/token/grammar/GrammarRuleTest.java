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

package net.luis.utils.io.token.grammar;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GrammarRule}.<br>
 *
 * @author Luis-St
 */
class GrammarRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				if (!stream.hasMoreTokens()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	@Test
	void constructorThrowsExceptionForNullRule() {
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> new GrammarRule(null, action));
	}
	
	@Test
	void constructorThrowsExceptionForNullAction() {
		TokenRule rule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> new GrammarRule(rule, null));
	}
	
	@Test
	void constructorThrowsExceptionForNullRuleAndAction() {
		assertThrows(NullPointerException.class, () -> new GrammarRule(null, null));
	}
	
	@Test
	void constructorCreatesInstanceForValidParameters() {
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		GrammarRule grammarRule = new GrammarRule(rule, action);
		
		assertNotNull(grammarRule);
		assertEquals(rule, grammarRule.rule());
		assertEquals(action, grammarRule.action());
	}
	
	@Test
	void ruleReturnsCorrectRule() {
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		GrammarRule grammarRule = new GrammarRule(rule, action);
		
		assertEquals(rule, grammarRule.rule());
	}
	
	@Test
	void actionReturnsCorrectAction() {
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		GrammarRule grammarRule = new GrammarRule(rule, action);
		
		assertEquals(action, grammarRule.action());
	}
	
	@Test
	void equalsAndHashCodeWorkCorrectly() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		TokenAction action1 = TokenAction.identity();
		TokenAction action2 = TokenAction.identity();
		
		GrammarRule grammarRule1 = new GrammarRule(rule1, action1);
		GrammarRule grammarRule2 = new GrammarRule(rule1, action1);
		GrammarRule grammarRule3 = new GrammarRule(rule2, action2);
		
		assertEquals(grammarRule1, grammarRule2);
		assertEquals(grammarRule1.hashCode(), grammarRule2.hashCode());
		assertNotEquals(grammarRule1, grammarRule3);
	}
	
	@Test
	void toStringReturnsNonNullString() {
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		GrammarRule grammarRule = new GrammarRule(rule, action);
		
		assertNotNull(grammarRule.toString());
		assertFalse(grammarRule.toString().isEmpty());
	}
}
