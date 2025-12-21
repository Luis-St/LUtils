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

package net.luis.utils.io.token.stream;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.tokens.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MutableTokenStream}.<br>
 *
 * @author Luis-St
 */
class MutableTokenStreamTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED);
	}
	
	@Test
	void constructorWithValidTokensAndIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		assertEquals(2, stream.size());
		assertEquals(0, stream.getCurrentIndex());
		assertEquals("a", stream.getCurrentToken().value());
	}
	
	@Test
	void constructorWithNullTokens() {
		assertThrows(NullPointerException.class, () -> new MutableTokenStream(null, 0));
	}
	
	@Test
	void constructorWithNegativeIndex() {
		List<Token> tokens = List.of(createToken("a"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> new MutableTokenStream(tokens, -1));
	}
	
	@Test
	void constructorWithShadowTokenSkipping() {
		Token normalToken = createToken("normal");
		Token shadowToken = new ShadowToken(createToken("shadow"));
		List<Token> tokens = List.of(shadowToken, normalToken);
		
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		assertEquals(1, stream.getCurrentIndex());
		assertEquals("normal", stream.getCurrentToken().value());
	}
	
	@Test
	void constructorWithOnlyShadowTokens() {
		Token shadowToken1 = new ShadowToken(createToken("shadow1"));
		Token shadowToken2 = new ShadowToken(createToken("shadow2"));
		List<Token> tokens = List.of(shadowToken1, shadowToken2);
		
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		assertEquals(2, stream.getCurrentIndex());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void getAllTokens() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		List<Token> result = stream.getAllTokens();
		
		assertEquals(2, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("c")));
	}
	
	@Test
	void getCurrentIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToValidIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		stream.advanceTo(2);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToNegativeIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		stream.advanceTo(-1);
		
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToBeyondSize() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		stream.advanceTo(5);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void getCurrentTokenWithValidIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertEquals("b", stream.getCurrentToken().value());
	}
	
	@Test
	void getCurrentTokenWithShadowTokensInBetween() {
		Token normalToken1 = createToken("normal1");
		Token shadowToken = new ShadowToken(createToken("shadow"));
		Token normalToken2 = createToken("normal2");
		List<Token> tokens = List.of(normalToken1, shadowToken, normalToken2);
		
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertEquals("normal2", stream.getCurrentToken().value());
	}
	
	@Test
	void getCurrentTokenWithNoMoreTokens() {
		List<Token> tokens = List.of(createToken("a"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertThrows(EndOfTokenStreamException.class, stream::getCurrentToken);
	}
	
	@Test
	void hasMoreTokensTrue() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		assertTrue(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensFalse() {
		List<Token> tokens = List.of(createToken("a"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensWithShadowTokens() {
		Token normalToken = createToken("normal");
		Token shadowToken = new ShadowToken(createToken("shadow"));
		List<Token> tokens = List.of(normalToken, shadowToken);
		
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void readTokenValid() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		Token token = stream.readToken();
		
		assertEquals("a", token.value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void readTokenWithShadowTokens() {
		Token normalToken1 = createToken("normal1");
		Token shadowToken = new ShadowToken(createToken("shadow"));
		Token normalToken2 = createToken("normal2");
		List<Token> tokens = List.of(normalToken1, shadowToken, normalToken2);
		
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		Token token = stream.readToken();
		
		assertEquals("normal1", token.value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void readTokenAtEnd() {
		List<Token> tokens = List.of(createToken("a"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		assertThrows(EndOfTokenStreamException.class, stream::readToken);
	}
	
	@Test
	void copyWithIndexValid() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		TokenStream copy = stream.copyWithIndex(1);
		
		assertEquals(1, copy.getCurrentIndex());
		assertInstanceOf(MutableTokenStream.class, copy);
	}
	
	@Test
	void copyWithIndexNegative() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		TokenStream copy = stream.copyWithIndex(-1);
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexBeyondSize() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		TokenStream copy = stream.copyWithIndex(5);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void reversed() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		TokenStream reversed = stream.reversed();
		
		assertEquals(1, reversed.getCurrentIndex());
		assertEquals("b", reversed.getCurrentToken().value());
		List<Token> reversedTokens = reversed.getAllTokens();
		assertEquals("c", reversedTokens.get(0).value());
		assertEquals("b", reversedTokens.get(1).value());
		assertEquals("a", reversedTokens.get(2).value());
	}
	
	@Test
	void reversedWithEmptyStream() {
		MutableTokenStream stream = new MutableTokenStream(Collections.emptyList(), 0);
		
		TokenStream reversed = stream.reversed();
		
		assertEquals(0, reversed.getCurrentIndex());
		assertTrue(reversed.isEmpty());
	}
	
	@Test
	void createLookaheadStream() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 1);
		
		TokenStream lookahead = stream.createLookaheadStream();
		
		assertEquals(0, lookahead.getCurrentIndex());
		assertEquals(2, lookahead.size());
		assertEquals("b", lookahead.getCurrentToken().value());
	}
	
	@Test
	void createLookaheadStreamAtEnd() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 2);
		
		TokenStream lookahead = stream.createLookaheadStream();
		
		assertEquals(0, lookahead.getCurrentIndex());
		assertTrue(lookahead.isEmpty());
	}
	
	@Test
	void createLookbehindStream() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 2);
		
		TokenStream lookbehind = stream.createLookbehindStream();
		
		assertEquals(0, lookbehind.getCurrentIndex());
		assertEquals(2, lookbehind.size());
		assertEquals("b", lookbehind.getCurrentToken().value());
		List<Token> lookbehindTokens = lookbehind.getAllTokens();
		assertEquals("b", lookbehindTokens.get(0).value());
		assertEquals("a", lookbehindTokens.get(1).value());
	}
	
	@Test
	void createLookbehindStreamAtBeginning() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		MutableTokenStream stream = new MutableTokenStream(tokens, 0);
		
		TokenStream lookbehind = stream.createLookbehindStream();
		
		assertEquals(0, lookbehind.getCurrentIndex());
		assertTrue(lookbehind.isEmpty());
	}
}
