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

package net.luis.utils.grammar.parser.rule.assertions;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.*;
import net.luis.utils.grammar.parser.rule.core.LookMatchMode;
import net.luis.utils.grammar.parser.rule.matchers.PatternTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LookaheadTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookaheadTokenRuleTest {
	
	private static Token token(@org.jspecify.annotations.NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		return TokenStream.createMutable(List.of(java.util.Arrays.stream(values).map(LookaheadTokenRuleTest::token).toArray(Token[]::new)));
	}
	
	@Test
	void constructWithValidTokenRuleAndMode() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertNotNull(rule);
		assertEquals(AlwaysMatchTokenRule.INSTANCE, rule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, rule.mode());
	}
	
	@Test
	void constructWithNullTokenRuleThrowsException() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(null, LookMatchMode.POSITIVE));
	}
	
	@Test
	void constructWithNullModeThrowsException() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, null));
	}
	
	@Test
	void matchWithNullStreamThrowsException() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContextThrowsException() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertThrows(NullPointerException.class, () -> rule.match(streamOf("a"), null));
	}
	
	@Test
	void matchWithPositiveModeAndMatchingInnerRuleReturnsMatch() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithPositiveModeAndNonMatchingInnerRuleReturnsNull() {
		LookaheadTokenRule rule = new LookaheadTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertNull(rule.match(streamOf("a"), TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNegativeModeAndNonMatchingInnerRuleReturnsMatch() {
		LookaheadTokenRule rule = new LookaheadTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchWithNegativeModeAndMatchingInnerRuleReturnsNull() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		assertNull(rule.match(streamOf("a"), TokenRuleContext.empty()));
	}
	
	@Test
	void matchDoesNotConsumeTokensFromOriginalStream() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notFromPositiveModeReturnsNegativeMode() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(LookaheadTokenRule.class, notRule);
		LookaheadTokenRule casted = (LookaheadTokenRule) notRule;
		assertEquals(LookMatchMode.NEGATIVE, casted.mode());
		assertSame(AlwaysMatchTokenRule.INSTANCE, casted.tokenRule());
	}
	
	@Test
	void notFromNegativeModeReturnsPositiveMode() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(LookaheadTokenRule.class, notRule);
		LookaheadTokenRule casted = (LookaheadTokenRule) notRule;
		assertEquals(LookMatchMode.POSITIVE, casted.mode());
		assertSame(AlwaysMatchTokenRule.INSTANCE, casted.tokenRule());
	}
	
	@Test
	void matchOnEmptyStreamWithPositiveModeAndFailingInnerRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertNull(rule.match(TokenStream.EMPTY, TokenRuleContext.empty()));
	}
	
	@Test
	void matchOnEmptyStreamWithNegativeModeAndFailingInnerRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		TokenRuleMatch match = rule.match(TokenStream.EMPTY, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchAtNonZeroCurrentIndexReturnsMatchAtThatIndex() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c")), 2);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void tokenRuleAccessorReturnsConstructedValue() {
		TokenRule innerRule = NeverMatchTokenRule.INSTANCE;
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		assertSame(innerRule, rule.tokenRule());
	}
	
	@Test
	void modeAccessorReturnsConstructedValue() {
		LookaheadTokenRule rule = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		assertEquals(LookMatchMode.NEGATIVE, rule.mode());
	}
	
	@Test
	void matchWithNestedLookaheadRuleComposition() {
		LookaheadTokenRule inner = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		LookaheadTokenRule outer = new LookaheadTokenRule(inner, LookMatchMode.POSITIVE);
		TokenStream stream = streamOf("a", "b");
		
		TokenRuleMatch match = outer.match(stream, TokenRuleContext.empty());
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notTwiceReturnsEquivalentToOriginalMode() {
		LookaheadTokenRule original = new LookaheadTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenRule flipped = original.not();
		TokenRule restored = flipped.not();
		
		assertInstanceOf(LookaheadTokenRule.class, restored);
		LookaheadTokenRule castedRestored = (LookaheadTokenRule) restored;
		assertEquals(LookMatchMode.POSITIVE, castedRestored.mode());
		assertSame(original.tokenRule(), castedRestored.tokenRule());
		assertEquals(original, restored);
	}
	
	@Test
	void matchWithLargeStreamAndMatchingSubsequenceRule() {
		TokenRule innerRule = new PatternTokenRule("c");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c"), token("d"), token("e")), 2);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
	}
}
