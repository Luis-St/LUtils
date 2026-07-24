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

package net.luis.utils.grammar.parser.rule;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CustomTokeRule}.<br>
 *
 * @author Luis-St
 */
class CustomTokeRuleTest {
	
	@Test
	void constructWithValidCondition() {
		Predicate<Token> condition = t -> t.value().startsWith("a");
		CustomTokeRule rule = new CustomTokeRule(condition);
		assertSame(condition, rule.condition());
	}
	
	@Test
	void constructWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new CustomTokeRule(null));
	}
	
	@Test
	void matchWithNullTokenThrows() {
		CustomTokeRule rule = new CustomTokeRule(_ -> true);
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchReturnsTrueWhenConditionMatches() {
		CustomTokeRule rule = new CustomTokeRule(t -> "abc".equals(t.value()));
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchReturnsFalseWhenConditionDoesNotMatch() {
		CustomTokeRule rule = new CustomTokeRule(t -> "abc".equals(t.value()));
		assertFalse(rule.match(SimpleToken.createUnpositioned("xyz")));
	}
	
	@Test
	void matchWithConditionOnTokenLength() {
		CustomTokeRule rule = new CustomTokeRule(t -> t.value().length() > 3);
		assertTrue(rule.match(SimpleToken.createUnpositioned("abcd")));
	}
	
	@Test
	void matchWithAlwaysFalseCondition() {
		CustomTokeRule rule = new CustomTokeRule(_ -> false);
		assertFalse(rule.match(SimpleToken.createUnpositioned("anything")));
	}
	
	@Test
	void matchWithAlwaysTrueCondition() {
		CustomTokeRule rule = new CustomTokeRule(_ -> true);
		assertTrue(rule.match(SimpleToken.createUnpositioned("anything")));
	}
	
	@Test
	void matchViaTokenStreamConsumesMatchingToken() {
		CustomTokeRule rule = new CustomTokeRule(t -> "abc".equals(t.value()));
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesCustomMatchResult() {
		TokenRule negated = new CustomTokeRule(t -> "abc".equals(t.value())).not();
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenStream nonMatchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("xyz")));
		assertNotNull(negated.match(nonMatchingStream, ctx));
		
		TokenStream matchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		assertNull(negated.match(matchingStream, ctx));
	}
	
	@Test
	void matchWithStatefulConditionReflectsPredicateSideEffects() {
		AtomicInteger calls = new AtomicInteger(0);
		CustomTokeRule rule = new CustomTokeRule(_ -> {
			calls.incrementAndGet();
			return true;
		});
		Token token = SimpleToken.createUnpositioned("abc");
		
		assertTrue(rule.match(token));
		assertTrue(rule.match(token));
		assertEquals(2, calls.get());
	}
}
