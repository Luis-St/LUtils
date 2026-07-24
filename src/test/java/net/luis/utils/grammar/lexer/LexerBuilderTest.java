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

package net.luis.utils.grammar.lexer;

import net.luis.utils.grammar.lexer.rule.CharRules;
import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LexerBuilder}.<br>
 *
 * @author Luis-St
 */
class LexerBuilderTest {
	
	@Test
	void allowWithNullCharacterArray() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(b -> b.allow((char[]) null)));
	}
	
	@Test
	void allowRangeWithEndLessThanStartThrows() {
		assertThrows(IllegalArgumentException.class, () -> Lexer.builder(b -> b.allowRange('z', 'a')));
	}
	
	@Test
	void ruleWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(b -> {
			b.allow('a');
			b.rule(null, StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		}));
	}
	
	@Test
	void ruleWithNullTokenTypeThrows() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(b -> {
			b.allow('a');
			b.rule("NAME", null, CharRules.literal('a'));
		}));
	}
	
	@Test
	void ruleWithNullPatternThrows() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(b -> {
			b.allow('a');
			b.rule("NAME", StandardTokenType.IDENTIFIER, null);
		}));
	}
	
	@Test
	void ruleWithEmptyNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> Lexer.builder(b -> {
			b.allow('a');
			b.rule("", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		}));
	}
	
	@Test
	void shadowRuleWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(b -> {
			b.allow('a');
			b.shadowRule(null, StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		}));
	}
	
	@Test
	void buildWithNoAllowedCharactersThrows() {
		assertThrows(IllegalStateException.class, () -> Lexer.builder(b -> b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'))));
	}
	
	@Test
	void buildWithNoRulesThrows() {
		assertThrows(IllegalStateException.class, () -> Lexer.builder(b -> b.allow('a')));
	}
	
	@Test
	void allowWithEmptyCharacterArray() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow();
			b.allow('a');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertEquals(1, lexer.getAllowedChars().size());
	}
	
	@Test
	void allowWithPopulatedCharacterArray() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b', 'c');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertTrue(lexer.getAllowedChars().contains('a'));
		assertTrue(lexer.getAllowedChars().contains('b'));
		assertTrue(lexer.getAllowedChars().contains('c'));
	}
	
	@Test
	void allowRangeWithStartEqualToEndAddsSingleCharacter() {
		Lexer lexer = Lexer.builder(b -> {
			b.allowRange('m', 'm');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('m'));
		});
		assertEquals(1, lexer.getAllowedChars().size());
		assertTrue(lexer.getAllowedChars().contains('m'));
	}
	
	@Test
	void allowRangeWithMultiCharacterRangeAddsAllCharacters() {
		Lexer lexer = Lexer.builder(b -> {
			b.allowRange('a', 'e');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertEquals(5, lexer.getAllowedChars().size());
		assertTrue(lexer.getAllowedChars().containsAll(java.util.List.of('a', 'b', 'c', 'd', 'e')));
	}
	
	@Test
	void ruleAddsNonShadowRule() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertEquals(1, lexer.getRules().size());
		assertFalse(lexer.getRules().getFirst().shadow());
	}
	
	@Test
	void shadowRuleAddsShadowRule() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow(' ');
			b.shadowRule("WS", StandardTokenType.WHITESPACE, CharRules.literal(' '));
		});
		assertTrue(lexer.getRules().getFirst().shadow());
	}
	
	@Test
	void buildWithSingleAllowedCharacterAndSingleRule() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertNotNull(lexer);
		assertEquals(1, lexer.getAllowedChars().size());
		assertEquals(1, lexer.getRules().size());
	}
	
	@Test
	void ruleOrderIsPreserved() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b');
			b.rule("FIRST", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
			b.rule("SECOND", StandardTokenType.IDENTIFIER, CharRules.literal('b'));
		});
		assertEquals("FIRST", lexer.getRules().get(0).name());
		assertEquals("SECOND", lexer.getRules().get(1).name());
	}
	
	@Test
	void allowAndAllowRangeCombinedProduceUnionOfCharacters() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('x');
			b.allowRange('x', 'z');
			b.rule("NAME", StandardTokenType.IDENTIFIER, CharRules.literal('x'));
		});
		assertEquals(3, lexer.getAllowedChars().size());
		assertTrue(lexer.getAllowedChars().containsAll(java.util.List.of('x', 'y', 'z')));
	}
	
	@Test
	void multipleRuleAndShadowRuleCallsAccumulateInOrder() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b', ' ');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
			b.shadowRule("WS", StandardTokenType.WHITESPACE, CharRules.literal(' '));
			b.rule("B", StandardTokenType.IDENTIFIER, CharRules.literal('b'));
		});
		assertEquals(3, lexer.getRules().size());
		assertFalse(lexer.getRules().get(0).shadow());
		assertTrue(lexer.getRules().get(1).shadow());
		assertFalse(lexer.getRules().get(2).shadow());
	}
}
