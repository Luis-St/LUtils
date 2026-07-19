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
import net.luis.utils.grammar.token.Token;
import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TypeTokenRule}.<br>
 *
 * @author Luis-St
 */
class TypeTokenRuleTest {
	
	private static Token tokenWithTypes(Set<StandardTokenType> types) {
		Token token = SimpleToken.createUnpositioned("value");
		token.types().addAll(types);
		return token;
	}
	
	@Test
	void constructWithSingleType() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		
		assertEquals(Set.of(StandardTokenType.IDENTIFIER), rule.tokenTypes());
	}
	
	@Test
	void constructWithMultipleTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		assertEquals(2, rule.tokenTypes().size());
	}
	
	@Test
	void constructWithNullTokenTypes() {
		assertThrows(NullPointerException.class, () -> new TypeTokenRule(null));
	}
	
	@Test
	void constructWithEmptyTokenTypes() {
		assertThrows(IllegalArgumentException.class, () -> new TypeTokenRule(Set.of()));
	}
	
	@Test
	void matchWithNullToken() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchTokenContainingAllRequiredTypesReturnsTrue() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		Token token = tokenWithTypes(Set.of(StandardTokenType.IDENTIFIER));
		
		assertTrue(rule.match(token));
	}
	
	@Test
	void matchTokenMissingRequiredTypeReturnsFalse() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = tokenWithTypes(Set.of(StandardTokenType.IDENTIFIER));
		
		assertFalse(rule.match(token));
	}
	
	@Test
	void matchTokenWithExtraTypesStillMatches() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		Token token = tokenWithTypes(Set.of(StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		assertTrue(rule.match(token));
	}
	
	@Test
	void matchTokenWithNoTypesAgainstNonEmptyRule() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		Token token = tokenWithTypes(Set.of());
		
		assertFalse(rule.match(token));
	}
	
	@Test
	void matchViaTokenStreamConsumesMatchingToken() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		Token token = tokenWithTypes(Set.of(StandardTokenType.IDENTIFIER));
		TokenStream stream = TokenStream.createMutable(List.of(token));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesTypeMatchResult() {
		TokenRule notRule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER)).not();
		Token numberToken = tokenWithTypes(Set.of(StandardTokenType.NUMBER));
		Token identifierToken = tokenWithTypes(Set.of(StandardTokenType.IDENTIFIER));
		
		assertNotNull(notRule.match(TokenStream.createMutable(List.of(numberToken)), TokenRuleContext.empty()));
		assertNull(notRule.match(TokenStream.createMutable(List.of(identifierToken)), TokenRuleContext.empty()));
	}
}
