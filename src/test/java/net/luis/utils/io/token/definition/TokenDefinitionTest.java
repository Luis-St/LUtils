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

package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinition}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	private static final List<Token> TOKENS = List.of(TOKEN_0, TOKEN_1, TOKEN_2);
	
	@Test
	void of() {
		assertInstanceOf(CharTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.of('a')));
		assertInstanceOf(CharTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.of('\\')));
		
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, false));
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, true));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", false));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", true));
		assertInstanceOf(StringTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.of("a", false)));
		assertInstanceOf(StringTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.of("a", true)));
		assertInstanceOf(StringTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.of(" ", false)));
	}
	
	@Test
	void ofEscaped() {
		assertInstanceOf(EscapedTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.ofEscaped('a')));
		assertInstanceOf(EscapedTokenDefinition.class, assertDoesNotThrow(() -> TokenDefinition.ofEscaped('\\')));
	}
	
	@Test
	void combine() {
		assertThrows(NullPointerException.class, () -> TokenDefinition.combine((TokenDefinition[]) null));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.combine(new TokenDefinition[0]));
		
		TokenDefinition charDefinition = TokenDefinition.of('a');
		assertSame(charDefinition, TokenDefinition.combine(charDefinition));
		
		TokenDefinition combined0 = TokenDefinition.combine(
			TokenDefinition.of('a'),
			TokenDefinition.of('b'),
			TokenDefinition.of('c')
		);
		assertTrue(combined0.matches("abc"));
		StringTokenDefinition combined0String = assertInstanceOf(StringTokenDefinition.class, combined0);
		assertEquals("abc", combined0String.token());
		assertFalse(combined0String.equalsIgnoreCase());
		
		TokenDefinition combined1 = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of("world", false)
		);
		assertTrue(combined1.matches("helloworld"));
		StringTokenDefinition combined1String = assertInstanceOf(StringTokenDefinition.class, combined1);
		assertEquals("helloworld", combined1String.token());
		assertFalse(combined1String.equalsIgnoreCase());
		
		TokenDefinition combined2 = TokenDefinition.combine(
			TokenDefinition.ofEscaped('\\'),
			TokenDefinition.of('n')
		);
		assertTrue(combined2.matches("\\\\n"));
		StringTokenDefinition combined2String = assertInstanceOf(StringTokenDefinition.class, combined2);
		assertEquals("\\\\n", combined2String.token());
		assertFalse(combined2String.equalsIgnoreCase());
		
		TokenDefinition combined3 = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of('-'),
			TokenDefinition.ofEscaped('!')
		);
		assertTrue(combined3.matches("hello-\\!"));
		StringTokenDefinition combined3String = assertInstanceOf(StringTokenDefinition.class, combined3);
		assertTrue(combined3.matches("hello-\\!"));
		assertEquals("hello-\\!", combined3String.token());
		assertFalse(combined3String.equalsIgnoreCase());
		
		TokenDefinition combined4 = TokenDefinition.combine(
			TokenDefinition.of("HELLO", false),
			TokenDefinition.of("world", false)
		);
		assertTrue(combined4.matches("HELLOworld"));
		StringTokenDefinition combined4String = assertInstanceOf(StringTokenDefinition.class, combined4);
		assertTrue(combined4String.matches("HELLOworld"));
		assertEquals("HELLOworld", combined4String.token());
		assertFalse(combined4String.equalsIgnoreCase());
		
		TokenDefinition customDefinition = "custom"::equals;
		assertSame(customDefinition, TokenDefinition.combine(customDefinition));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.combine(TokenDefinition.of('a'), "custom"::equals));
	}
	
	@Test
	void match() {
		TokenDefinition testDefinition = TOKEN_0.definition();
		assertThrows(NullPointerException.class, () -> testDefinition.match(null, 0));
		assertNull(testDefinition.match(TOKENS, 1));
		assertNull(testDefinition.match(TOKENS, 3));
		assertNull(testDefinition.match(Collections.emptyList(), 0));
		
		TokenRuleMatch match = testDefinition.match(TOKENS, 0);
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(TOKEN_0, match.matchedTokens().getFirst());
		assertEquals(testDefinition, match.matchingTokenRule());
	}
}

