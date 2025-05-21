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

import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenTransformer}.<br>
 *
 * @author Luis-St
 */
class TokenTransformerTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	private static final List<Token> TEST_TOKENS = List.of(TOKEN_1, TOKEN_2);
	
	@Test
	void transform() {
		TokenTransformer identityTransformer = List::copyOf;
		List<Token> result0 = identityTransformer.transform(TEST_TOKENS);
		assertEquals(TEST_TOKENS.size(), result0.size());
		assertTrue(result0.containsAll(TEST_TOKENS));
		assertThrows(UnsupportedOperationException.class, () -> result0.add(TOKEN_0));
		
		TokenTransformer filterTransformer = inputTokens -> {
			List<Token> result = new ArrayList<>();
			for (Token token : inputTokens) {
				if (token.value().contains("1")) {
					result.add(token);
				}
			}
			return Collections.unmodifiableList(result);
		};
		
		List<Token> result1 = filterTransformer.transform(TEST_TOKENS);
		assertEquals(1, result1.size());
		assertEquals(TOKEN_1, result1.getFirst());
		assertFalse(result1.contains(TOKEN_2));
		assertThrows(UnsupportedOperationException.class, () -> result1.add(TOKEN_0));
		
		TokenTransformer emptyTransformer = inputTokens -> Collections.emptyList();
		List<Token> result2 = emptyTransformer.transform(TEST_TOKENS);
		assertNotNull(result2);
		assertTrue(result2.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result2.add(TOKEN_0));
	}
}
