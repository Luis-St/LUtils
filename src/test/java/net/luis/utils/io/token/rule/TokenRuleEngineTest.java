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

package net.luis.utils.io.token.rule;

import com.google.common.collect.Sets;
import net.luis.utils.io.token.TokenReader;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.rule.rules.*;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleEngine}.<br>
 *
 * @author Luis-St
 */
class TokenRuleEngineTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	
	private static final TokenDefinition OPEN_PAREN = TokenDefinition.of('(');
	private static final TokenDefinition CLOSE_PAREN = TokenDefinition.of(')');
	
	private static final TokenDefinition ZERO = TokenDefinition.of('0');
	private static final TokenDefinition ONE = TokenDefinition.of('1');
	private static final TokenDefinition TWO = TokenDefinition.of('2');
	private static final TokenDefinition THREE = TokenDefinition.of('3');
	private static final TokenDefinition FOUR = TokenDefinition.of('4');
	private static final TokenDefinition FIVE = TokenDefinition.of('5');
	private static final TokenDefinition SIX = TokenDefinition.of('6');
	private static final TokenDefinition SEVEN = TokenDefinition.of('7');
	private static final TokenDefinition EIGHT = TokenDefinition.of('8');
	private static final TokenDefinition NINE = TokenDefinition.of('9');
	
	private static final Set<TokenDefinition> DEFINITIONS = Set.of(
		OPEN_PAREN, CLOSE_PAREN, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
	);
	private static final Set<Character> ALLOWED_CHARS = Set.of(
		'(', ')', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	);
	private static final Set<Character> SEPARATORS = Sets.union(
		Set.of(' '),
		ALLOWED_CHARS
	);
	
	@Test
	void addRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		assertThrows(NullPointerException.class, () -> engine.addRule(null));
		assertDoesNotThrow(() -> engine.addRule(TokenRules.alwaysMatch()));
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null, TokenAction.identity()));
		assertThrows(NullPointerException.class, () -> engine.addRule(TokenRules.alwaysMatch(), null));
		assertDoesNotThrow(() -> engine.addRule(TokenRules.alwaysMatch(), TokenAction.identity()));
	}
	
	@Test
	void process() {
		TokenRuleEngine baseMatchEngine = new TokenRuleEngine();
		assertThrows(NullPointerException.class, () -> baseMatchEngine.process(null));
		
		List<Token> emptyList = List.of();
		assertDoesNotThrow(() -> baseMatchEngine.process(emptyList));
		assertEquals(emptyList, baseMatchEngine.process(emptyList));
		
		
		TokenReader reader = new TokenReader(DEFINITIONS, ALLOWED_CHARS, SEPARATORS);
		
		TokenRuleEngine alwaysMatchEngine = new TokenRuleEngine();
		alwaysMatchEngine.addRule(TokenRules.alwaysMatch(), TokenAction.identity());
		List<Token> parenTokens = reader.readTokens("(()())");
		List<Token> processedTokens = alwaysMatchEngine.process(parenTokens);
		assertEquals(parenTokens.size(), processedTokens.size());
		assertEquals(parenTokens, processedTokens);
		
		TokenRuleEngine patternEngine = new TokenRuleEngine();
		patternEngine.addRule(new PatternTokenRule("\\("), TokenAction.identity());
		processedTokens = patternEngine.process(parenTokens);
		assertEquals(parenTokens.size(), processedTokens.size());
		assertEquals(parenTokens, processedTokens);
		
		TokenRuleEngine optionalEngine = new TokenRuleEngine();
		optionalEngine.addRule(new OptionalTokenRule(new PatternTokenRule("\\(")), TokenAction.identity());
		processedTokens = optionalEngine.process(parenTokens);
		assertEquals(parenTokens.size(), processedTokens.size());
		assertEquals(parenTokens, processedTokens);
		
		TokenRuleEngine anyOfEngine = new TokenRuleEngine();
		anyOfEngine.addRule(new AnyOfTokenRule(Set.of(
			new PatternTokenRule("\\("),
			new PatternTokenRule("\\)")
		)), TokenAction.identity());
		processedTokens = anyOfEngine.process(parenTokens);
		assertEquals(parenTokens.size(), processedTokens.size());
		assertEquals(parenTokens, processedTokens);
		
		TokenRuleEngine sequenceEngine = new TokenRuleEngine();
		sequenceEngine.addRule(new SequenceTokenRule(List.of(
			new PatternTokenRule("\\("),
			new PatternTokenRule("\\(")
		)), TokenAction.identity());
		List<Token> sequenceTestTokens = reader.readTokens("(()");
		processedTokens = sequenceEngine.process(sequenceTestTokens);
		assertEquals(sequenceTestTokens.size(), processedTokens.size());
		assertEquals(sequenceTestTokens, processedTokens);
		
		TokenRuleEngine repeatedEngine = new TokenRuleEngine();
		repeatedEngine.addRule(new RepeatedTokenRule(
			new PatternTokenRule("\\("), 1, 3
		), TokenAction.identity());
		List<Token> repeatedTestTokens = reader.readTokens("(((");
		processedTokens = repeatedEngine.process(repeatedTestTokens);
		assertEquals(repeatedTestTokens.size(), processedTokens.size());
		assertEquals(repeatedTestTokens, processedTokens);
		
		TokenRuleEngine boundaryEngine = new TokenRuleEngine();
		boundaryEngine.addRule(new BoundaryTokenRule(
			new PatternTokenRule("\\("),
			TokenRules.alwaysMatch(),
			new PatternTokenRule("\\)")
		), TokenAction.identity());
		List<Token> boundaryTestTokens = reader.readTokens("(123)");
		processedTokens = boundaryEngine.process(boundaryTestTokens);
		assertEquals(boundaryTestTokens.size(), processedTokens.size());
		assertEquals(boundaryTestTokens, processedTokens);
		
		TokenRuleEngine complexEngine = new TokenRuleEngine();
		complexEngine.addRule(new BoundaryTokenRule(
			new PatternTokenRule("\\("),
			new AnyOfTokenRule(Set.of(
				new PatternTokenRule("1"),
				new PatternTokenRule("2"),
				new PatternTokenRule("3")
			)),
			new PatternTokenRule("\\)")
		), TokenAction.identity());
		List<Token> complexTestTokens = reader.readTokens("(123)");
		processedTokens = complexEngine.process(complexTestTokens);
		assertEquals(complexTestTokens.size(), processedTokens.size());
		assertEquals(complexTestTokens, processedTokens);
		
		TokenRuleEngine endEngine = new TokenRuleEngine();
		endEngine.addRule(TokenRules.end(), TokenAction.identity());
		List<Token> endTestTokens = reader.readTokens(")");
		processedTokens = endEngine.process(endTestTokens);
		assertEquals(endTestTokens.size(), processedTokens.size());
		assertEquals(endTestTokens, processedTokens);
	}
}
