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

package net.luis.utils.grammar.parser.rule.matchers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PatternTokenRule}.<br>
 *
 * @author Luis-St
 */
class PatternTokenRuleTest {
	
	@Test
	void constructWithPattern() {
		PatternTokenRule rule = new PatternTokenRule(Pattern.compile("[a-z]+"));
		assertEquals("[a-z]+", rule.pattern().pattern());
	}
	
	@Test
	void constructWithNullPattern() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((Pattern) null));
	}
	
	@Test
	void constructWithRegexString() {
		PatternTokenRule rule = new PatternTokenRule("[0-9]+");
		assertEquals("[0-9]+", rule.pattern().pattern());
	}
	
	@Test
	void constructWithNullRegexString() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((String) null));
	}
	
	@Test
	void matchWithNullToken() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchTokenMatchingPatternReturnsTrue() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchTokenNotMatchingPatternReturnsFalse() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		assertFalse(rule.match(SimpleToken.createUnpositioned("ABC123")));
	}
	
	@Test
	void matchWithDigitPattern() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		assertTrue(rule.match(SimpleToken.createUnpositioned("42")));
	}
	
	@Test
	void matchRequiresFullTokenMatch() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		assertFalse(rule.match(SimpleToken.createUnpositioned("abc123")));
	}
	
	@Test
	void matchViaTokenStreamConsumesMatchingToken() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesPatternMatchResult() {
		PatternTokenRule rule = new PatternTokenRule("[a-z]+");
		TokenRule negated = rule.not();
		
		TokenStream nonMatchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("ABC")));
		TokenStream matchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		
		assertNotNull(negated.match(nonMatchingStream, TokenRuleContext.empty()));
		assertNull(negated.match(matchingStream, TokenRuleContext.empty()));
	}
}
