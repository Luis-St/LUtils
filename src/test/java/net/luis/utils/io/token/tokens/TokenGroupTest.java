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

package net.luis.utils.io.token.tokens;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroup}.<br>
 *
 * @author Luis-St
 */
class TokenGroupTest {
	
	private static final TokenDefinition ANY_DEFINITION = (word) -> true;
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	private static final TokenPosition POS_0_0 = new TokenPosition(0, 0, 0);
	private static final TokenPosition POS_0_1 = new TokenPosition(0, 1, 1);
	private static final TokenPosition POS_0_2 = new TokenPosition(0, 2, 2);
	private static final TokenPosition POS_0_3 = new TokenPosition(0, 3, 3);
	private static final TokenPosition POS_0_4 = new TokenPosition(0, 4, 4);
	private static final TokenPosition POS_0_5 = new TokenPosition(0, 5, 5);
	
	private static @NotNull Token createToken(@NotNull String value, @NotNull TokenPosition start, @NotNull TokenPosition end) {
		return new SimpleToken(ANY_DEFINITION, value, start, end);
	}
	
	@Test
	void constructor() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		Token token2 = createToken("22", POS_0_4, POS_0_5);
		List<Token> tokens = Lists.newArrayList(token0, token1, token2);
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(null, ANY_DEFINITION));
		assertThrows(NullPointerException.class, () -> new TokenGroup(tokens, null));
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(Collections.emptyList(), ANY_DEFINITION));
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(Collections.singletonList(token0), ANY_DEFINITION));
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(Lists.newArrayList(token0, token1, null), ANY_DEFINITION));
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(tokens, (word) -> word.matches("abc")));
		
		Token invalidPosToken1 = createToken("AA", POS_0_0, POS_0_1);
		Token invalidPosToken2 = createToken("BB", POS_0_4, new TokenPosition(0, 5, 5));
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(List.of(invalidPosToken1, invalidPosToken2), ANY_DEFINITION));
		
		Token unpositionedToken2 = createToken("BB", TokenPosition.UNPOSITIONED, new TokenPosition(0, 5, 5));
		assertDoesNotThrow(() -> new TokenGroup(List.of(invalidPosToken1, unpositionedToken2), ANY_DEFINITION));
		
		Token gapToken1 = createToken("AA", POS_0_0, POS_0_1);
		Token gapToken2 = createToken("BB", new TokenPosition(0, 3, 3), POS_0_4);
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(List.of(gapToken1, gapToken2), ANY_DEFINITION));
		
		Token validPosToken1 = createToken("AA", POS_0_0, POS_0_1);
		Token validPosToken2 = createToken("BB", POS_0_2, POS_0_3);
		List<Token> validPositionTokens = List.of(validPosToken1, validPosToken2);
		assertDoesNotThrow(() -> new TokenGroup(validPositionTokens, ANY_DEFINITION));
		assertDoesNotThrow(() -> new TokenGroup(validPositionTokens, (word) -> word.length() == 4));
	}
	
	@Test
	void tokens() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		List<Token> tokens = List.of(token0, token1);
		TokenGroup group = new TokenGroup(tokens, ANY_DEFINITION);
		
		assertIterableEquals(tokens, group.tokens());
		assertEquals(2, group.tokens().size());
		assertEquals(token0, group.tokens().get(0));
		assertEquals(token1, group.tokens().get(1));
		
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().add(createToken("03", POS_0_4, POS_0_5)));
	}
	
	@Test
	void definition() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		TokenGroup group = new TokenGroup(List.of(token0, token1), NUMBER_DEFINITION);
		
		assertEquals(NUMBER_DEFINITION, group.definition());
	}
	
	@Test
	void value() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		Token token2 = createToken("22", POS_0_4, POS_0_5);
		List<Token> tokens = List.of(token0, token1, token2);
		TokenGroup group = new TokenGroup(tokens, NUMBER_DEFINITION);
		
		assertEquals("001122", group.value());
		
		Token tokenA = createToken("hello", POS_0_0, new TokenPosition(0, 4, 4));
		Token tokenB = createToken(" ", new TokenPosition(0, 5, 5), new TokenPosition(0, 5, 5));
		Token tokenC = createToken("world", new TokenPosition(0, 6, 6), new TokenPosition(0, 10, 10));
		TokenGroup textGroup = new TokenGroup(List.of(tokenA, tokenB, tokenC), ANY_DEFINITION);
		assertEquals("hello world", textGroup.value());
	}
	
	@Test
	void isPositioned() {
		Token positionedToken0 = createToken("00", POS_0_0, POS_0_1);
		Token positionedToken1 = createToken("11", POS_0_2, POS_0_3);
		assertTrue(new TokenGroup(List.of(positionedToken0, positionedToken1), ANY_DEFINITION).isPositioned());
		
		Token unpositionedToken1 = createToken("11", TokenPosition.UNPOSITIONED, POS_0_3);
		assertFalse(new TokenGroup(List.of(positionedToken0, unpositionedToken1), ANY_DEFINITION).isPositioned());
	}
	
	@Test
	void startPosition() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		List<Token> tokens = List.of(token0, token1);
		TokenGroup group = new TokenGroup(tokens, ANY_DEFINITION);
		
		assertEquals(POS_0_0, group.startPosition());
		assertEquals(0, group.startPosition().line());
		assertEquals(0, group.startPosition().character());
		assertEquals(0, group.startPosition().characterInLine());
	}
	
	@Test
	void endPosition() {
		Token token0 = createToken("00", POS_0_0, POS_0_1);
		Token token1 = createToken("11", POS_0_2, POS_0_3);
		List<Token> tokens = List.of(token0, token1);
		TokenGroup group = new TokenGroup(tokens, ANY_DEFINITION);
		
		assertEquals(POS_0_3, group.endPosition());
		assertEquals(0, group.endPosition().line());
		assertEquals(3, group.endPosition().character());
		assertEquals(3, group.endPosition().characterInLine());
	}
}

