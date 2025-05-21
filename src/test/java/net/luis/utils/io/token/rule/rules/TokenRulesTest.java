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

package net.luis.utils.io.token.rule.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.definition.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRules}.<br>
 *
 * @author Luis-St
 */
class TokenRulesTest {
	
	@Test
	void alwaysMatch() {
		assertSame(AlwaysMatchTokenRule.INSTANCE, TokenRules.alwaysMatch());
	}
	
	@Test
	void pattern() {
		assertInstanceOf(PatternTokenRule.class, TokenRules.pattern("test"));
	}
	
	@Test
	void optional() {
		assertInstanceOf(OptionalTokenRule.class, TokenRules.optional(TokenRules.alwaysMatch()));
	}
	
	@Test
	void repeatAtLeast() {
		assertInstanceOf(RepeatedTokenRule.class, TokenRules.repeatAtLeast(TokenRules.alwaysMatch(), 1));
	}
	
	@Test
	void repeatExactly() {
		assertInstanceOf(RepeatedTokenRule.class, TokenRules.repeatExactly(TokenRules.alwaysMatch(), 1));
	}
	
	@Test
	void repeatAtMost() {
		assertInstanceOf(RepeatedTokenRule.class, TokenRules.repeatAtMost(TokenRules.alwaysMatch(), 1));
	}
	
	@Test
	void repeatInfinitely() {
		assertInstanceOf(RepeatedTokenRule.class, TokenRules.repeatInfinitely(TokenRules.alwaysMatch()));
	}
	
	@Test
	void repeatBetween() {
		assertInstanceOf(RepeatedTokenRule.class, TokenRules.repeatBetween(TokenRules.alwaysMatch(), 1, 2));
	}
	
	@Test
	void sequence() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((TokenRule[]) null));
		assertInstanceOf(SequenceTokenRule.class, TokenRules.sequence(TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void any() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((TokenRule[]) null));
		assertInstanceOf(AnyOfTokenRule.class, TokenRules.any(TokenRules.alwaysMatch(), TokenRules.end()));
	}
	
	@Test
	void boundary() {
		assertInstanceOf(BoundaryTokenRule.class, TokenRules.boundary(TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
		assertInstanceOf(BoundaryTokenRule.class, TokenRules.boundary(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void end() {
		assertSame(EndTokenRule.INSTANCE, TokenRules.end());
	}
	
	@Test
	void toRegex() {
		assertThrows(NullPointerException.class, () -> TokenRules.toRegex(null));
		
		assertEquals(".*?", TokenRules.toRegex(TokenRules.alwaysMatch()));
		assertEquals("$", TokenRules.toRegex(TokenRules.end()));
		
		assertEquals("test", TokenRules.toRegex(TokenRules.pattern("test")));
		assertEquals("test", TokenRules.toRegex(TokenRules.pattern(Pattern.compile("test"))));
		assertEquals("test", TokenRules.toRegex(TokenRules.pattern("(test)")));
		assertEquals("[a-z]", TokenRules.toRegex(TokenRules.pattern("[a-z]")));
		assertEquals("\\d+", TokenRules.toRegex(TokenRules.pattern("\\d+")));
		
		assertEquals("test?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("test"))));
		assertEquals("(.*?)?", TokenRules.toRegex(TokenRules.optional(TokenRules.alwaysMatch())));
		assertEquals("(\\d+)?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("\\d+"))));
		
		assertEquals("test{3}", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 3)));
		assertEquals("test", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 1)));
		
		assertEquals("test{2,}", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 2)));
		assertEquals("test+", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 1)));
		
		assertEquals("test{0,3}", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 3)));
		assertEquals("test?", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 1)));
		
		assertEquals("test*", TokenRules.toRegex(TokenRules.repeatInfinitely(TokenRules.pattern("test"))));
		
		assertEquals("test{2,5}", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("test"), 2, 5)));
		assertEquals("test?", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("test"), 0, 1)));
		
		assertEquals("(\\d+?){2,5}", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("\\d+?"), 2, 5)));
		assertEquals("(test*)+", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test*"), 1)));
		
		assertEquals("test\\d+", TokenRules.toRegex(TokenRules.sequence(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+")
		)));
		assertEquals("test\\d+.*?", TokenRules.toRegex(TokenRules.sequence(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+"),
			TokenRules.alwaysMatch()
		)));
		
		List<TokenRule> anyRules = Lists.newArrayList(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+")
		);
		assertEquals("test|\\d+", TokenRules.toRegex(TokenRules.any(new LinkedHashSet<>(anyRules))));
		anyRules.add(TokenRules.alwaysMatch());
		assertEquals("test|\\d+|.*?", TokenRules.toRegex(TokenRules.any(new LinkedHashSet<>(anyRules))));
		TokenRule charA = new CharTokenDefinition('a');
		TokenRule charB = new CharTokenDefinition('b');
		TokenRule charC = new CharTokenDefinition('c');
		assertEquals("[abc]", TokenRules.toRegex(TokenRules.any(Sets.newLinkedHashSet(Arrays.asList(charA, charB, charC)))));
		TokenRule charBracket = new CharTokenDefinition('[');
		TokenRule charBackslash = new CharTokenDefinition('\\');
		assertEquals("[\\[\\\\]", TokenRules.toRegex(TokenRules.any(Sets.newLinkedHashSet(Arrays.asList(charBracket, charBackslash)))));
		
		assertEquals("test(.*?)end", TokenRules.toRegex(TokenRules.boundary(
			TokenRules.pattern("test"),
			TokenRules.pattern("end")
		)));
		assertEquals("test(.*?)end", TokenRules.toRegex(TokenRules.boundary(
			TokenRules.pattern("test"),
			TokenRules.alwaysMatch(),
			TokenRules.pattern("end")
		)));
		assertEquals("test(\\d+?)end", TokenRules.toRegex(TokenRules.boundary(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+?"),
			TokenRules.pattern("end")
		)));
		
		assertEquals("[a-zA-Z0-9]+", TokenRules.toRegex(WordTokenDefinition.INSTANCE));
		assertEquals("a", TokenRules.toRegex(new CharTokenDefinition('a')));
		assertEquals("test", TokenRules.toRegex(new StringTokenDefinition("test", false)));
		assertEquals("\\\\a", TokenRules.toRegex(new EscapedTokenDefinition('a')));
		
		assertEquals("\\$", TokenRules.toRegex(new CharTokenDefinition('$')));
		assertEquals("\\(", TokenRules.toRegex(new CharTokenDefinition('(')));
		assertEquals("\\+", TokenRules.toRegex(new CharTokenDefinition('+')));
		assertEquals("\\.", TokenRules.toRegex(new CharTokenDefinition('.')));
		assertEquals("\\*", TokenRules.toRegex(new CharTokenDefinition('*')));
		
		TokenRule complexRule1 = TokenRules.sequence(
			TokenRules.pattern("start"),
			TokenRules.optional(TokenRules.pattern("\\d+")),
			TokenRules.repeatAtLeast(TokenRules.pattern("[a-z]"), 1)
		);
		assertEquals("start(\\d+)?[a-z]+", TokenRules.toRegex(complexRule1));
		
		TokenRule complexRule2 = TokenRules.any(new LinkedHashSet<>(Arrays.asList(
			TokenRules.pattern("option1"),
			TokenRules.sequence(
				TokenRules.pattern("prefix"),
				TokenRules.repeatBetween(TokenRules.pattern("\\w"), 1, 3)
			)
		)));
		assertEquals("option1|prefix\\w{1,3}", TokenRules.toRegex(complexRule2));
		
		TokenRule complexRule3 = TokenRules.boundary(
			TokenRules.pattern("\""),
			TokenRules.repeatInfinitely(
				TokenRules.any(new LinkedHashSet<>(Arrays.asList(
					TokenRules.pattern("[^\"]"),
					TokenRules.pattern("\"")
				)))
			),
			TokenRules.pattern("\"")
		);
		assertEquals("\"(([^\"]|\")*)\"", TokenRules.toRegex(complexRule3));
	}
}

