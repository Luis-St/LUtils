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

package net.luis.utils.grammar.parser;

import net.luis.utils.grammar.parser.action.TokenAction;
import net.luis.utils.grammar.parser.rule.TokenRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParserRule}.<br>
 *
 * @author Luis-St
 */
class ParserRuleTest {
	
	private static final TokenRule NULL_MATCH_RULE = (_, _) -> null;
	private static final TokenRule OTHER_NULL_MATCH_RULE = (_, _) -> null;
	private static final TokenAction EMPTY_LIST_ACTION = (_, _) -> List.of();
	
	@Test
	void constructValidParserRule() {
		TokenAction identity = TokenAction.identity();
		ParserRule rule = new ParserRule("ruleName", NULL_MATCH_RULE, identity);
		
		assertEquals("ruleName", rule.name());
		assertSame(NULL_MATCH_RULE, rule.rule());
		assertSame(identity, rule.action());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new ParserRule(null, NULL_MATCH_RULE, TokenAction.identity()));
	}
	
	@Test
	void constructWithNullRule() {
		assertThrows(NullPointerException.class, () -> new ParserRule("ruleName", null, TokenAction.identity()));
	}
	
	@Test
	void constructWithNullAction() {
		assertThrows(NullPointerException.class, () -> new ParserRule("ruleName", NULL_MATCH_RULE, null));
	}
	
	@Test
	void constructWithEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> new ParserRule("", NULL_MATCH_RULE, TokenAction.identity()));
	}
	
	@Test
	void constructWithEmptyNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new ParserRule("", NULL_MATCH_RULE, TokenAction.identity()));
	}
	
	@Test
	void constructWithNonEmptyNameSucceeds() {
		ParserRule rule = assertDoesNotThrow(() -> new ParserRule("a", NULL_MATCH_RULE, TokenAction.identity()));
		
		assertEquals("a", rule.name());
	}
	
	@Test
	void nameReturnsStoredValue() {
		ParserRule rule = new ParserRule("expression", NULL_MATCH_RULE, TokenAction.identity());
		
		assertEquals("expression", rule.name());
	}
	
	@Test
	void ruleReturnsStoredValue() {
		ParserRule rule = new ParserRule("ruleName", NULL_MATCH_RULE, TokenAction.identity());
		
		assertSame(NULL_MATCH_RULE, rule.rule());
	}
	
	@Test
	void actionReturnsStoredValue() {
		TokenAction identity = TokenAction.identity();
		ParserRule rule = new ParserRule("ruleName", NULL_MATCH_RULE, identity);
		
		assertSame(identity, rule.action());
	}
	
	@Test
	void toStringContainsComponents() {
		ParserRule rule = new ParserRule("ruleName", NULL_MATCH_RULE, TokenAction.identity());
		
		assertTrue(rule.toString().contains("ruleName"));
		assertTrue(rule.toString().contains("ParserRule"));
	}
	
	@Test
	void constructWithLongName() {
		String longName = "a".repeat(50);
		ParserRule rule = new ParserRule(longName, NULL_MATCH_RULE, TokenAction.identity());
		
		assertEquals(longName, rule.name());
	}
	
	@Test
	void equalsAndHashCodeForEqualInstances() {
		TokenAction identity = TokenAction.identity();
		ParserRule rule1 = new ParserRule("ruleName", NULL_MATCH_RULE, identity);
		ParserRule rule2 = new ParserRule("ruleName", NULL_MATCH_RULE, identity);
		
		assertEquals(rule1, rule2);
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void equalsReflexiveAndAgainstNullAndOtherType() {
		ParserRule rule = new ParserRule("ruleName", NULL_MATCH_RULE, TokenAction.identity());
		
		assertEquals(rule, rule);
		assertNotEquals(null, rule);
		assertNotEquals(rule, "someString");
	}
	
	@Test
	void equalsDiffersByNameRuleOrAction() {
		TokenAction identity = TokenAction.identity();
		ParserRule base = new ParserRule("ruleName", NULL_MATCH_RULE, identity);
		ParserRule differentName = new ParserRule("otherName", NULL_MATCH_RULE, identity);
		ParserRule differentRule = new ParserRule("ruleName", OTHER_NULL_MATCH_RULE, identity);
		ParserRule differentAction = new ParserRule("ruleName", NULL_MATCH_RULE, EMPTY_LIST_ACTION);
		
		assertNotEquals(base, differentName);
		assertNotEquals(base, differentRule);
		assertNotEquals(base, differentAction);
	}
}
