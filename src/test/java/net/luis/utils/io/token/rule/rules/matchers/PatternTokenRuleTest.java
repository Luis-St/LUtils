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

package net.luis.utils.io.token.rule.rules.matchers;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PatternTokenRule}.<br>
 *
 * @author Luis-St
 */
class PatternTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullStringPattern() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((String) null));
	}
	
	@Test
	void constructorWithNullPattern() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((Pattern) null));
	}
	
	@Test
	void constructorWithInvalidRegexPattern() {
		assertThrows(PatternSyntaxException.class, () -> new PatternTokenRule("[a-z"));
		assertThrows(PatternSyntaxException.class, () -> new PatternTokenRule("(unclosed"));
		assertThrows(PatternSyntaxException.class, () -> new PatternTokenRule("*invalid"));
	}
	
	@Test
	void constructorWithValidStringPattern() {
		assertDoesNotThrow(() -> new PatternTokenRule("\\d+"));
		assertDoesNotThrow(() -> new PatternTokenRule("[a-zA-Z]+"));
		assertDoesNotThrow(() -> new PatternTokenRule("test.*"));
		assertDoesNotThrow(() -> new PatternTokenRule("^start"));
		assertDoesNotThrow(() -> new PatternTokenRule("end$"));
	}
	
	@Test
	void constructorWithValidCompiledPattern() {
		Pattern compiled = Pattern.compile("\\d+");
		
		assertDoesNotThrow(() -> new PatternTokenRule(compiled));
	}
	
	@Test
	void patternReturnsCorrectPatternFromString() {
		String regex = "test\\d+";
		PatternTokenRule rule = new PatternTokenRule(regex);
		
		assertEquals(regex, rule.pattern().pattern());
	}
	
	@Test
	void patternReturnsCorrectPatternFromCompiledPattern() {
		Pattern compiled = Pattern.compile("\\d+");
		PatternTokenRule rule = new PatternTokenRule(compiled);
		
		assertEquals(compiled, rule.pattern());
		assertEquals("\\d+", rule.pattern().pattern());
	}
	
	@Test
	void matchWithNullTokenList() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		assertNull(rule.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		List<Token> tokens = List.of(createToken("123"));
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, -1));
	}
	
	@Test
	void matchWithSimpleDigitPattern() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		Token numberToken = createToken("123");
		Token textToken = createToken("abc");
		
		TokenRuleMatch numberMatch = rule.match(List.of(numberToken), 0);
		assertNotNull(numberMatch);
		assertEquals(0, numberMatch.startIndex());
		assertEquals(1, numberMatch.endIndex());
		assertEquals(1, numberMatch.matchedTokens().size());
		assertEquals(numberToken, numberMatch.matchedTokens().getFirst());
		
		TokenRuleMatch textMatch = rule.match(List.of(textToken), 0);
		assertNull(textMatch);
	}
	
	@Test
	void matchWithAlphabeticPattern() {
		PatternTokenRule rule = new PatternTokenRule("[a-zA-Z]+");
		Token textToken = createToken("Hello");
		Token numberToken = createToken("123");
		Token mixedToken = createToken("Hello123");
		
		TokenRuleMatch textMatch = rule.match(List.of(textToken), 0);
		assertNotNull(textMatch);
		assertEquals(textToken, textMatch.matchedTokens().getFirst());
		
		assertNull(rule.match(List.of(numberToken), 0));
		assertNull(rule.match(List.of(mixedToken), 0));
	}
	
	@Test
	void matchWithExactStringPattern() {
		PatternTokenRule rule = new PatternTokenRule("hello");
		Token exactToken = createToken("hello");
		Token differentToken = createToken("world");
		Token partialToken = createToken("helloworld");
		
		TokenRuleMatch exactMatch = rule.match(List.of(exactToken), 0);
		assertNotNull(exactMatch);
		assertEquals(exactToken, exactMatch.matchedTokens().getFirst());
		
		assertNull(rule.match(List.of(differentToken), 0));
		assertNull(rule.match(List.of(partialToken), 0));
	}
	
	@Test
	void matchWithWildcardPattern() {
		PatternTokenRule rule = new PatternTokenRule("test.*");
		Token matchingToken1 = createToken("test");
		Token matchingToken2 = createToken("testing");
		Token matchingToken3 = createToken("test123");
		Token nonMatchingToken = createToken("other");
		
		assertNotNull(rule.match(List.of(matchingToken1), 0));
		assertNotNull(rule.match(List.of(matchingToken2), 0));
		assertNotNull(rule.match(List.of(matchingToken3), 0));
		assertNull(rule.match(List.of(nonMatchingToken), 0));
	}
	
	@Test
	void matchWithCharacterClassPattern() {
		PatternTokenRule rule = new PatternTokenRule("[0-9a-fA-F]+");
		Token hexToken1 = createToken("123ABC");
		Token hexToken2 = createToken("deadbeef");
		Token hexToken3 = createToken("0xFF");
		Token nonHexToken = createToken("xyz");
		
		assertNotNull(rule.match(List.of(hexToken1), 0));
		assertNotNull(rule.match(List.of(hexToken2), 0));
		assertNull(rule.match(List.of(hexToken3), 0));
		assertNull(rule.match(List.of(nonHexToken), 0));
	}
	
	@Test
	void matchWithQuantifierPatterns() {
		PatternTokenRule optionalRule = new PatternTokenRule("colou?r");
		PatternTokenRule oneOrMoreRule = new PatternTokenRule("a+");
		PatternTokenRule zeroOrMoreRule = new PatternTokenRule("b*");
		PatternTokenRule exactRule = new PatternTokenRule("c{3}");
		PatternTokenRule rangeRule = new PatternTokenRule("d{2,4}");
		
		assertNotNull(optionalRule.match(List.of(createToken("color")), 0));
		assertNotNull(optionalRule.match(List.of(createToken("colour")), 0));
		assertNull(optionalRule.match(List.of(createToken("colouur")), 0));
		
		assertNotNull(oneOrMoreRule.match(List.of(createToken("a")), 0));
		assertNotNull(oneOrMoreRule.match(List.of(createToken("aaa")), 0));
		assertNull(oneOrMoreRule.match(List.of(createToken("")), 0));
		
		assertNotNull(zeroOrMoreRule.match(List.of(createToken("")), 0));
		assertNotNull(zeroOrMoreRule.match(List.of(createToken("bbb")), 0));
		
		assertNotNull(exactRule.match(List.of(createToken("ccc")), 0));
		assertNull(exactRule.match(List.of(createToken("cc")), 0));
		assertNull(exactRule.match(List.of(createToken("cccc")), 0));
		
		assertNull(rangeRule.match(List.of(createToken("d")), 0));
		assertNotNull(rangeRule.match(List.of(createToken("dd")), 0));
		assertNotNull(rangeRule.match(List.of(createToken("dddd")), 0));
		assertNull(rangeRule.match(List.of(createToken("ddddd")), 0));
	}
	
	@Test
	void matchWithAnchorPatterns() {
		PatternTokenRule startAnchor = new PatternTokenRule("^test(.*?)");
		PatternTokenRule endAnchor = new PatternTokenRule("(.*?)test$");
		PatternTokenRule bothAnchors = new PatternTokenRule("^test$");
		
		assertNotNull(startAnchor.match(List.of(createToken("test")), 0));
		assertNotNull(startAnchor.match(List.of(createToken("testing")), 0));
		assertNull(startAnchor.match(List.of(createToken("pretest")), 0));
		
		assertNotNull(endAnchor.match(List.of(createToken("test")), 0));
		assertNotNull(endAnchor.match(List.of(createToken("pretest")), 0));
		assertNull(endAnchor.match(List.of(createToken("testing")), 0));
		
		assertNotNull(bothAnchors.match(List.of(createToken("test")), 0));
		assertNull(bothAnchors.match(List.of(createToken("testing")), 0));
		assertNull(bothAnchors.match(List.of(createToken("pretest")), 0));
	}
	
	@Test
	void matchWithGroupPatterns() {
		PatternTokenRule groupRule = new PatternTokenRule("(hello|world)");
		PatternTokenRule nonCapturingRule = new PatternTokenRule("(?:foo|bar)");
		
		assertNotNull(groupRule.match(List.of(createToken("hello")), 0));
		assertNotNull(groupRule.match(List.of(createToken("world")), 0));
		assertNull(groupRule.match(List.of(createToken("goodbye")), 0));
		
		assertNotNull(nonCapturingRule.match(List.of(createToken("foo")), 0));
		assertNotNull(nonCapturingRule.match(List.of(createToken("bar")), 0));
		assertNull(nonCapturingRule.match(List.of(createToken("baz")), 0));
	}
	
	@Test
	void matchWithSpecialCharacterPatterns() {
		PatternTokenRule escapeRule = new PatternTokenRule("\\.");
		PatternTokenRule literalRule = new PatternTokenRule("\\$\\^\\*");
		
		assertNotNull(escapeRule.match(List.of(createToken(".")), 0));
		assertNull(escapeRule.match(List.of(createToken("a")), 0));
		
		assertNotNull(literalRule.match(List.of(createToken("$^*")), 0));
		assertNull(literalRule.match(List.of(createToken("abc")), 0));
	}
	
	@Test
	void matchAtDifferentIndices() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		List<Token> tokens = List.of(
			createToken("abc"),
			createToken("123"),
			createToken("def"),
			createToken("456")
		);
		
		assertNull(rule.match(tokens, 0));
		assertNotNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 2));
		assertNotNull(rule.match(tokens, 3));
	}
	
	@Test
	void matchWithEmptyStringPattern() {
		PatternTokenRule rule = new PatternTokenRule("");
		Token emptyToken = createToken("");
		Token nonEmptyToken = createToken("test");
		
		assertNotNull(rule.match(List.of(emptyToken), 0));
		assertNull(rule.match(List.of(nonEmptyToken), 0));
	}
	
	@Test
	void matchWithDotPattern() {
		PatternTokenRule rule = new PatternTokenRule(".");
		Token singleChar = createToken("a");
		Token multiChar = createToken("abc");
		Token emptyToken = createToken("");
		
		assertNotNull(rule.match(List.of(singleChar), 0));
		assertNull(rule.match(List.of(multiChar), 0));
		assertNull(rule.match(List.of(emptyToken), 0));
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		Token token = createToken("123");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(tokens, 0);
		TokenRuleMatch match2 = rule.match(tokens, 0);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void matchWithCaseInsensitivePattern() {
		Pattern caseInsensitive = Pattern.compile("hello", Pattern.CASE_INSENSITIVE);
		PatternTokenRule rule = new PatternTokenRule(caseInsensitive);
		
		assertNotNull(rule.match(List.of(createToken("hello")), 0));
		assertNotNull(rule.match(List.of(createToken("HELLO")), 0));
		assertNotNull(rule.match(List.of(createToken("Hello")), 0));
		assertNull(rule.match(List.of(createToken("world")), 0));
	}
}
