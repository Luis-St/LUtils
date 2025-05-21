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

import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BoundaryTokenRule}.<br>
 *
 * @author Luis-St
 */
class BoundaryTokenRuleTest {
	
	private static final TokenDefinition START_DEFINITION = "("::equals;
	private static final TokenDefinition MIDDLE_DEFINITION = "test"::equals;
	private static final TokenDefinition END_DEFINITION = ")"::equals;
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	
	private static final Token START_TOKEN = SimpleToken.createUnpositioned(START_DEFINITION, "(");
	private static final Token MIDDLE_TOKEN = SimpleToken.createUnpositioned(MIDDLE_DEFINITION, "test");
	private static final Token END_TOKEN = SimpleToken.createUnpositioned(END_DEFINITION, ")");
	private static final Token NUMBER_TOKEN = SimpleToken.createUnpositioned(NUMBER_DEFINITION, "123");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, END_DEFINITION));
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(START_DEFINITION, null));
		
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, MIDDLE_DEFINITION, END_DEFINITION));
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(START_DEFINITION, null, END_DEFINITION));
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(START_DEFINITION, MIDDLE_DEFINITION, null));
		
		BoundaryTokenRule validRule = new BoundaryTokenRule(START_DEFINITION, END_DEFINITION);
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, validRule, END_DEFINITION));
		
		RepeatedTokenRule repeatedRule = new RepeatedTokenRule(MIDDLE_DEFINITION, 1, 3);
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, repeatedRule, END_DEFINITION));
		
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(MIDDLE_DEFINITION, MIDDLE_DEFINITION));
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, sequenceRule, END_DEFINITION));
		
		assertThrows(IllegalArgumentException.class, () -> new BoundaryTokenRule(START_DEFINITION, EndTokenRule.INSTANCE, END_DEFINITION));
		
		AnyOfTokenRule anyOfInvalid = new AnyOfTokenRule(Set.of(MIDDLE_DEFINITION, new BoundaryTokenRule(START_DEFINITION, END_DEFINITION)));
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, anyOfInvalid, END_DEFINITION));
		
		OptionalTokenRule optionalInvalid = new OptionalTokenRule(new RepeatedTokenRule(MIDDLE_DEFINITION, 1));
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, optionalInvalid, END_DEFINITION));
		
		AnyOfTokenRule anyOfValid = new AnyOfTokenRule(Set.of(MIDDLE_DEFINITION));
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, anyOfValid, END_DEFINITION));
		
		OptionalTokenRule optionalValid = new OptionalTokenRule(MIDDLE_DEFINITION);
		assertDoesNotThrow(() -> new BoundaryTokenRule(START_DEFINITION, optionalValid, END_DEFINITION));
	}
	
	@Test
	void startTokenRule() {
		assertEquals(START_DEFINITION, new BoundaryTokenRule(START_DEFINITION, END_DEFINITION).startTokenRule());
	}
	
	@Test
	void betweenTokenRule() {
		assertEquals(TokenRules.alwaysMatch(), new BoundaryTokenRule(START_DEFINITION, END_DEFINITION).betweenTokenRule());
		assertEquals(MIDDLE_DEFINITION, new BoundaryTokenRule(START_DEFINITION, MIDDLE_DEFINITION, END_DEFINITION).betweenTokenRule());
	}
	
	@Test
	void endTokenRule() {
		assertEquals(END_DEFINITION, new BoundaryTokenRule(START_DEFINITION, END_DEFINITION).endTokenRule());
	}
	
	@Test
	void match() {
		BoundaryTokenRule rule = new BoundaryTokenRule(START_DEFINITION, MIDDLE_DEFINITION, END_DEFINITION);
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
		assertNull(rule.match(Collections.emptyList(), 0));
		
		TokenRuleMatch match0 = rule.match(List.of(START_TOKEN, END_TOKEN), 0);
		assertNotNull(match0);
		assertEquals(0, match0.startIndex());
		assertEquals(2, match0.endIndex());
		assertEquals(2, match0.matchedTokens().size());
		assertEquals(START_TOKEN, match0.matchedTokens().get(0));
		assertEquals(END_TOKEN, match0.matchedTokens().get(1));
		
		TokenRuleMatch match1 = rule.match(List.of(START_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(3, match1.endIndex());
		assertEquals(3, match1.matchedTokens().size());
		assertEquals(START_TOKEN, match1.matchedTokens().get(0));
		assertEquals(MIDDLE_TOKEN, match1.matchedTokens().get(1));
		assertEquals(END_TOKEN, match1.matchedTokens().get(2));
		
		TokenRuleMatch match2 = rule.match(List.of(START_TOKEN, MIDDLE_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(match2);
		assertEquals(0, match2.startIndex());
		assertEquals(4, match2.endIndex());
		assertEquals(4, match2.matchedTokens().size());
		
		TokenRuleMatch match3 = rule.match(List.of(START_TOKEN, END_TOKEN, END_TOKEN), 0);
		assertNotNull(match3);
		assertEquals(0, match3.startIndex());
		assertEquals(2, match3.endIndex());
		assertEquals(2, match3.matchedTokens().size());
		
		assertNull(rule.match(List.of(START_TOKEN, MIDDLE_TOKEN), 0));
		assertNull(rule.match(List.of(MIDDLE_TOKEN, END_TOKEN), 0));
		assertNull(rule.match(List.of(START_TOKEN, END_TOKEN), 5));
		
		BoundaryTokenRule defaultRule = new BoundaryTokenRule(START_DEFINITION, END_DEFINITION);
		Token number0 = SimpleToken.createUnpositioned(NUMBER_DEFINITION, "00");
		Token number1 = SimpleToken.createUnpositioned(NUMBER_DEFINITION, "11");
		TokenRuleMatch match4 = defaultRule.match(List.of(START_TOKEN, number0, MIDDLE_TOKEN, number1, END_TOKEN), 0);
		assertNotNull(match4);
		assertEquals(0, match4.startIndex());
		assertEquals(5, match4.endIndex());
		assertEquals(5, match4.matchedTokens().size());
		
		OptionalTokenRule optionalRule = new OptionalTokenRule(MIDDLE_DEFINITION);
		BoundaryTokenRule boundaryWithOptional = new BoundaryTokenRule(START_DEFINITION, optionalRule, END_DEFINITION);
		TokenRuleMatch optMatch1 = boundaryWithOptional.match(List.of(START_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(optMatch1);
		assertEquals(3, optMatch1.matchedTokens().size());
		TokenRuleMatch optMatch2 = boundaryWithOptional.match(List.of(START_TOKEN, END_TOKEN), 0);
		assertNotNull(optMatch2);
		assertEquals(2, optMatch2.matchedTokens().size());
		
		AnyOfTokenRule anyOfRule = new AnyOfTokenRule(Set.of(MIDDLE_DEFINITION, NUMBER_DEFINITION));
		BoundaryTokenRule boundaryWithAnyOf = new BoundaryTokenRule(START_DEFINITION, anyOfRule, END_DEFINITION);
		TokenRuleMatch anyMatch1 = boundaryWithAnyOf.match(List.of(START_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(anyMatch1);
		assertEquals(3, anyMatch1.matchedTokens().size());
		TokenRuleMatch anyMatch2 = boundaryWithAnyOf.match(List.of(START_TOKEN, NUMBER_TOKEN, END_TOKEN), 0);
		assertNotNull(anyMatch2);
		assertEquals(3, anyMatch2.matchedTokens().size());
		
		RepeatedTokenRule repeatedRule = new RepeatedTokenRule(MIDDLE_DEFINITION, 1, 3);
		BoundaryTokenRule boundaryWithRepeated = new BoundaryTokenRule(START_DEFINITION, repeatedRule, END_DEFINITION);
		TokenRuleMatch repMatch1 = boundaryWithRepeated.match(List.of(START_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(repMatch1);
		assertEquals(3, repMatch1.matchedTokens().size());
		TokenRuleMatch repMatch3 = boundaryWithRepeated.match(List.of(START_TOKEN, MIDDLE_TOKEN, MIDDLE_TOKEN, MIDDLE_TOKEN, END_TOKEN), 0);
		assertNotNull(repMatch3);
		assertEquals(5, repMatch3.matchedTokens().size());
		
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(MIDDLE_DEFINITION, NUMBER_DEFINITION));
		BoundaryTokenRule boundaryWithSequence = new BoundaryTokenRule(START_DEFINITION, sequenceRule, END_DEFINITION);
		TokenRuleMatch seqMatch = boundaryWithSequence.match(List.of(START_TOKEN, MIDDLE_TOKEN, NUMBER_TOKEN, END_TOKEN), 0);
		assertNotNull(seqMatch);
		assertEquals(4, seqMatch.matchedTokens().size());
		
		TokenRule complexRule = new SequenceTokenRule(List.of(
			new OptionalTokenRule(MIDDLE_DEFINITION),
			new AnyOfTokenRule(Set.of(NUMBER_DEFINITION))
		));
		BoundaryTokenRule boundaryWithComplex = new BoundaryTokenRule(START_DEFINITION, complexRule, END_DEFINITION);
		TokenRuleMatch complexMatch1 = boundaryWithComplex.match(List.of(START_TOKEN, MIDDLE_TOKEN, NUMBER_TOKEN, END_TOKEN), 0);
		assertNotNull(complexMatch1);
		assertEquals(4, complexMatch1.matchedTokens().size());
		TokenRuleMatch complexMatch2 = boundaryWithComplex.match(List.of(START_TOKEN, NUMBER_TOKEN, END_TOKEN), 0);
		assertNotNull(complexMatch2);
		assertEquals(3, complexMatch2.matchedTokens().size());
	}
}
