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

package net.luis.utils.grammar.parser.action;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.action.core.GroupingMode;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingTokenAction}.<br>
 *
 * @author Luis-St
 */
class GroupingTokenActionTest {
	
	private static final TokenRule RULE = (_, _) -> null;
	private static final TokenActionContext CTX = new TokenActionContext(TokenStream.EMPTY);
	
	@Test
	void constructWithMode() {
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		assertEquals("", action.label());
		assertEquals(GroupingMode.MATCHED, action.mode());
	}
	
	@Test
	void constructWithNullMode() {
		assertThrows(NullPointerException.class, () -> new GroupingTokenAction(null));
	}
	
	@Test
	void constructWithLabelAndMode() {
		GroupingTokenAction action = new GroupingTokenAction("expr", GroupingMode.ALL);
		assertEquals("expr", action.label());
		assertEquals(GroupingMode.ALL, action.mode());
	}
	
	@Test
	void constructWithNullLabel() {
		assertThrows(NullPointerException.class, () -> new GroupingTokenAction(null, GroupingMode.MATCHED));
	}
	
	@Test
	void constructWithNullModeAndLabel() {
		assertThrows(NullPointerException.class, () -> new GroupingTokenAction("label", null));
	}
	
	@Test
	void applyWithNullMatch() {
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		assertThrows(NullPointerException.class, () -> action.apply(null, CTX));
	}
	
	@Test
	void applyWithNullContext() {
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(SimpleToken.createUnpositioned("a")), RULE);
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithMatchedMode() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		TokenRuleMatch match = new TokenRuleMatch(0, 2, List.of(a, b), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(1, result.size());
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(List.of(a, b), group.tokens());
	}
	
	@Test
	void applyWithAllMode() {
		Token t0 = SimpleToken.createUnpositioned("0");
		Token t1 = SimpleToken.createUnpositioned("1");
		Token t2 = SimpleToken.createUnpositioned("2");
		Token t3 = SimpleToken.createUnpositioned("3");
		TokenStream stream = TokenStream.createImmutable(List.of(t0, t1, t2, t3));
		TokenActionContext ctx = new TokenActionContext(stream);
		TokenRuleMatch match = new TokenRuleMatch(1, 3, List.of(), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.ALL);
		
		List<Token> result = action.apply(match, ctx);
		
		assertEquals(1, result.size());
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(List.of(t1, t2), group.tokens());
	}
	
	@Test
	void applyProducesUnlabeledGroupWhenLabelEmpty() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(SimpleToken.createUnpositioned("a")), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		TokenGroup group = (TokenGroup) action.apply(match, CTX).getFirst();
		
		assertEquals("", group.label());
	}
	
	@Test
	void applyProducesLabeledGroup() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(SimpleToken.createUnpositioned("a")), RULE);
		GroupingTokenAction action = new GroupingTokenAction("stmt", GroupingMode.MATCHED);
		
		TokenGroup group = (TokenGroup) action.apply(match, CTX).getFirst();
		
		assertEquals("stmt", group.label());
	}
	
	@Test
	void applyWithSingleMatchedToken() {
		Token a = SimpleToken.createUnpositioned("a");
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(a), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(1, result.size());
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(List.of(a), group.tokens());
	}
	
	@Test
	void applyResultListIsUnmodifiable() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(SimpleToken.createUnpositioned("a")), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		List<Token> result = action.apply(match, CTX);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("x")));
	}
	
	@Test
	void applyWithMatchedModeAndEmptyMatchedTokensThrows() {
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertThrows(IllegalArgumentException.class, () -> action.apply(match, CTX));
	}
	
	@Test
	void applyWithAllModeAndEmptyRangeThrows() {
		TokenStream stream = TokenStream.createImmutable(List.of(
			SimpleToken.createUnpositioned("0"), SimpleToken.createUnpositioned("1"), SimpleToken.createUnpositioned("2")
		));
		TokenActionContext ctx = new TokenActionContext(stream);
		TokenRuleMatch match = new TokenRuleMatch(1, 1, List.of(), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.ALL);
		
		assertThrows(IllegalArgumentException.class, () -> action.apply(match, ctx));
	}
	
	@Test
	void applyWithAllModeSingleToken() {
		Token t0 = SimpleToken.createUnpositioned("0");
		Token t1 = SimpleToken.createUnpositioned("1");
		Token t2 = SimpleToken.createUnpositioned("2");
		TokenStream stream = TokenStream.createImmutable(List.of(t0, t1, t2));
		TokenActionContext ctx = new TokenActionContext(stream);
		TokenRuleMatch match = new TokenRuleMatch(1, 2, List.of(), RULE);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.ALL);
		
		List<Token> result = action.apply(match, ctx);
		
		assertEquals(1, result.size());
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(List.of(t1), group.tokens());
	}
	
	@Test
	void applyWithAllModeSpanningEntireStream() {
		Token t0 = SimpleToken.createUnpositioned("0");
		Token t1 = SimpleToken.createUnpositioned("1");
		Token t2 = SimpleToken.createUnpositioned("2");
		Token t3 = SimpleToken.createUnpositioned("3");
		Token t4 = SimpleToken.createUnpositioned("4");
		List<Token> tokens = List.of(t0, t1, t2, t3, t4);
		TokenStream stream = TokenStream.createImmutable(tokens);
		TokenActionContext ctx = new TokenActionContext(stream);
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), List.of(), RULE);
		GroupingTokenAction action = new GroupingTokenAction("expr", GroupingMode.ALL);
		
		List<Token> result = action.apply(match, ctx);
		
		assertEquals(1, result.size());
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(tokens, group.tokens());
		assertEquals("expr", group.label());
	}
	
	@Test
	void applyWithMatchedModeIgnoresContextStream() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		TokenRuleMatch match = new TokenRuleMatch(0, 2, List.of(a, b), RULE);
		TokenStream differentStream = TokenStream.createImmutable(List.of(
			SimpleToken.createUnpositioned("x"), SimpleToken.createUnpositioned("y"), SimpleToken.createUnpositioned("z")
		));
		TokenActionContext ctx = new TokenActionContext(differentStream);
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		List<Token> result = action.apply(match, ctx);
		
		TokenGroup group = (TokenGroup) result.getFirst();
		assertEquals(List.of(a, b), group.tokens());
	}
}
