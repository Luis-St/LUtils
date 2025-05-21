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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingTokenAction}.<br>
 *
 * @author Luis-St
 */
class GroupingTokenActionTest {
	
	private static final TokenDefinition ANY_DEFINITION = (word) -> true;
	private static final TokenPosition POS_0_0 = new TokenPosition(0, 0, 0);
	private static final TokenPosition POS_0_1 = new TokenPosition(0, 1, 1);
	private static final TokenPosition POS_0_2 = new TokenPosition(0, 2, 2);
	private static final TokenPosition POS_0_3 = new TokenPosition(0, 3, 3);
	
	private static @NotNull Token createToken(@NotNull String value, @NotNull TokenPosition start, @NotNull TokenPosition end) {
		return new SimpleToken(ANY_DEFINITION, value, start, end);
	}
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new GroupingTokenAction(null));
		assertDoesNotThrow(() -> new GroupingTokenAction(ANY_DEFINITION));
	}
	
	@Test
	void definition() {
		GroupingTokenAction action = new GroupingTokenAction(ANY_DEFINITION);
		assertEquals(ANY_DEFINITION, action.definition());
	}
	
	@Test
	void apply() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, List.of(token0, token1), ANY_DEFINITION);
		
		GroupingTokenAction action = new GroupingTokenAction(ANY_DEFINITION);
		List<Token> result = action.apply(match);
		assertEquals(1, result.size());
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals(ANY_DEFINITION, group.definition());
		assertEquals(2, group.tokens().size());
		assertEquals("0011", group.value());
		assertEquals(POS_0_0, group.startPosition());
		assertEquals(POS_0_3, group.endPosition());
	}
}
