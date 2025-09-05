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

package net.luis.utils.io.token;

import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenStream}.<br>
 *
 * @author Luis-St
 */
class TokenStreamTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull Token createShadowToken(@NotNull String value) {
		return new ShadowToken(createToken(value));
	}
	
	@Test
	void constructorWithValidTokenList() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals(2, stream.size());
		assertEquals(0, stream.getCurrentIndex());
		assertTrue(stream.hasToken());
	}
	
	@Test
	void constructorWithEmptyTokenList() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		
		assertEquals(0, stream.size());
		assertEquals(0, stream.getCurrentIndex());
		assertFalse(stream.hasToken());
		assertTrue(stream.isEmpty());
	}
	
	@Test
	void constructorWithValidTokenListAndIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		assertEquals(3, stream.size());
		assertEquals(1, stream.getCurrentIndex());
		assertTrue(stream.hasToken());
		assertEquals("second", stream.getCurrentToken().value());
	}
	
	@Test
	void constructorWithNullTokenList() {
		assertThrows(NullPointerException.class, () -> new TokenStream(null));
		assertThrows(NullPointerException.class, () -> new TokenStream(null, 0));
	}
	
	@Test
	void constructorWithNegativeIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> new TokenStream(tokens, -1));
	}
	
	@Test
	void constructorWithEmptyListAndValidIndex() {
		TokenStream stream = new TokenStream(Collections.emptyList(), 0);
		assertEquals(0, stream.getCurrentIndex());
		assertFalse(stream.hasToken());
	}
	
	@Test
	void constructorWithEmptyListAndInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> new TokenStream(Collections.emptyList(), -1));
	}
	
	@Test
	void isEmpty() {
		assertTrue(new TokenStream(Collections.emptyList()).isEmpty());
		assertFalse(new TokenStream(List.of(createToken("test"))).isEmpty());
		assertFalse(new TokenStream(List.of(createShadowToken("comment"))).isEmpty());
	}
	
	@Test
	void size() {
		assertEquals(0, new TokenStream(Collections.emptyList()).size());
		assertEquals(1, new TokenStream(List.of(createToken("test"))).size());
		assertEquals(3, new TokenStream(List.of(createToken("a"), createToken("b"), createToken("c"))).size());
		assertEquals(3, new TokenStream(List.of(createShadowToken("comment"), createToken("a"), createShadowToken("whitespace"))).size());
	}
	
	@Test
	void getCurrentIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		assertEquals(0, new TokenStream(tokens).getCurrentIndex());
		assertEquals(1, new TokenStream(tokens, 1).getCurrentIndex());
	}
	
	@Test
	void hasTokenWithMixedTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment"),
			createToken("actual"),
			createShadowToken("whitespace")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertTrue(stream.hasToken());
		
		stream.consumeToken();
		assertFalse(stream.hasToken());
	}
	
	@Test
	void hasTokenWithOnlyShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2"),
			createShadowToken("whitespace")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertFalse(stream.hasToken());
	}
	
	@Test
	void hasTokenWithOnlyNormalTokens() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens);
		
		assertTrue(stream.hasToken());
		stream.consumeToken();
		assertTrue(stream.hasToken());
		stream.consumeToken();
		assertFalse(stream.hasToken());
	}
	
	@Test
	void getCurrentTokenSkipsShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment"),
			createShadowToken("whitespace"),
			createToken("actual"),
			createShadowToken("trailing")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals("actual", stream.getCurrentToken().value());
		assertFalse(stream.getCurrentToken() instanceof ShadowToken);
		
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void getCurrentTokenAtNonShadowPosition() {
		List<Token> tokens = List.of(
			createToken("actual"),
			createShadowToken("comment")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals("actual", stream.getCurrentToken().value());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void getCurrentTokenWithOnlyShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertThrows(IndexOutOfBoundsException.class, stream::getCurrentToken);
	}
	
	@Test
	void consumeTokenSkipsShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2"),
			createToken("actual1"),
			createShadowToken("comment3"),
			createToken("actual2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals(0, stream.getCurrentIndex());
		
		assertEquals(3, stream.consumeToken());
		assertEquals(3, stream.getCurrentIndex());
		
		assertEquals(5, stream.consumeToken());
		assertEquals(5, stream.getCurrentIndex());
		
		assertThrows(NoSuchElementException.class, stream::consumeToken);
	}
	
	@Test
	void consumeTokenAtNonShadowPosition() {
		List<Token> tokens = List.of(
			createToken("actual"),
			createShadowToken("comment")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals(1, stream.consumeToken());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void consumeTokenWithOnlyShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertThrows(NoSuchElementException.class, stream::consumeToken);
	}
	
	@Test
	void readTokenSkipsShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createToken("actual1"),
			createShadowToken("comment2"),
			createToken("actual2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		Token token1 = stream.readToken();
		assertEquals("actual1", token1.value());
		assertFalse(token1 instanceof ShadowToken);
		assertEquals(2, stream.getCurrentIndex());
		
		Token token2 = stream.readToken();
		assertEquals("actual2", token2.value());
		assertFalse(token2 instanceof ShadowToken);
		assertEquals(4, stream.getCurrentIndex());
		
		assertThrows(NoSuchElementException.class, stream::readToken);
	}
	
	@Test
	void readTokenAtNonShadowPosition() {
		List<Token> tokens = List.of(
			createToken("actual"),
			createShadowToken("comment")
		);
		TokenStream stream = new TokenStream(tokens);
		
		Token token = stream.readToken();
		assertEquals("actual", token.value());
		assertFalse(token instanceof ShadowToken);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void readTokenWithOnlyShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertThrows(NoSuchElementException.class, stream::readToken);
	}
	
	@Test
	void advanceToWithNullOtherStream() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens);
		
		assertThrows(NullPointerException.class, () -> stream.advanceTo(null));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToWithEmptyStreams() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		TokenStream otherStream = new TokenStream(Collections.emptyList());
		
		assertEquals(0, stream.getCurrentIndex());
		stream.advanceTo(otherStream);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToWhenOtherStreamIsAhead() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"), createToken("fourth"));
		TokenStream stream = new TokenStream(tokens, 1);
		TokenStream otherStream = new TokenStream(tokens, 3);
		
		assertEquals(1, stream.getCurrentIndex());
		stream.advanceTo(otherStream);
		assertEquals(3, stream.getCurrentIndex());
		assertEquals("fourth", stream.getCurrentToken().value());
	}
	
	@Test
	void advanceToWhenOtherStreamIsBehind() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"), createToken("fourth"));
		TokenStream stream = new TokenStream(tokens, 3);
		TokenStream otherStream = new TokenStream(tokens, 1);
		
		assertEquals(3, stream.getCurrentIndex());
		stream.advanceTo(otherStream);
		assertEquals(3, stream.getCurrentIndex());
		assertEquals("fourth", stream.getCurrentToken().value());
	}
	
	@Test
	void reset() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 2);
		
		assertEquals(2, stream.getCurrentIndex());
		stream.reset();
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void resetEmptyStream() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		stream.reset();
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void resetWithShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment"),
			createToken("actual")
		);
		TokenStream stream = new TokenStream(tokens, 2);
		
		stream.reset();
		assertEquals(0, stream.getCurrentIndex());
		assertEquals("actual", stream.getCurrentToken().value());
	}
	
	@Test
	void lookaheadStream() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		TokenStream lookahead = stream.lookaheadStream();
		assertEquals(2, lookahead.size());
		assertEquals("second", lookahead.getCurrentToken().value());
		assertEquals("second", lookahead.readToken().value());
		assertEquals("third", lookahead.readToken().value());
	}
	
	@Test
	void lookaheadStreamAtEnd() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens, 1);
		stream.consumeToken();
		
		TokenStream lookahead = stream.lookaheadStream();
		assertTrue(lookahead.isEmpty());
		assertFalse(lookahead.hasToken());
	}
	
	@Test
	void lookaheadStreamWithShadowTokens() {
		List<Token> tokens = List.of(
			createToken("actual1"),
			createShadowToken("comment"),
			createToken("actual2")
		);
		TokenStream stream = new TokenStream(tokens, 1);
		
		TokenStream lookahead = stream.lookaheadStream();
		assertEquals(2, lookahead.size());
		assertTrue(lookahead.hasToken());
		assertEquals("actual2", lookahead.getCurrentToken().value());
	}
	
	@Test
	void lookbehindStream() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 2);
		
		TokenStream lookbehind = stream.lookbehindStream();
		assertEquals(2, lookbehind.size());
		assertEquals("second", lookbehind.getCurrentToken().value());
		assertEquals("second", lookbehind.readToken().value());
		assertEquals("first", lookbehind.readToken().value());
	}
	
	@Test
	void lookbehindStreamAtStart() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenStream lookbehind = stream.lookbehindStream();
		assertTrue(lookbehind.isEmpty());
		assertFalse(lookbehind.hasToken());
	}
	
	@Test
	void lookbehindStreamWithShadowTokens() {
		List<Token> tokens = List.of(
			createToken("actual1"),
			createShadowToken("comment"),
			createToken("actual2")
		);
		TokenStream stream = new TokenStream(tokens, 3);
		
		TokenStream lookbehind = stream.lookbehindStream();
		assertEquals(3, lookbehind.size());
		assertTrue(lookbehind.hasToken());
		assertEquals("actual2", lookbehind.getCurrentToken().value());
	}
	
	@Test
	void reverse() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		stream.reverse();
		assertEquals(1, stream.getCurrentIndex());
		assertEquals("second", stream.readToken().value());
		assertEquals("first", stream.readToken().value());
		assertFalse(stream.hasToken());
	}
	
	@Test
	void reverseWithEmptyStream() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		stream.reverse();
		assertEquals(0, stream.getCurrentIndex());
		assertTrue(stream.isEmpty());
	}
	
	@Test
	void reverseWithSingleToken() {
		List<Token> tokens = List.of(createToken("only"));
		TokenStream stream = new TokenStream(tokens, 0);
		
		stream.reverse();
		assertEquals(0, stream.getCurrentIndex());
		assertEquals("only", stream.getCurrentToken().value());
	}
	
	@Test
	void reverseWithShadowTokens() {
		List<Token> tokens = List.of(
			createToken("actual1"),
			createShadowToken("comment"),
			createToken("actual2")
		);
		TokenStream stream = new TokenStream(tokens, 1);
		
		stream.reverse();
		assertEquals("actual1", stream.getCurrentToken().value());
	}
	
	@Test
	void copy() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream original = new TokenStream(tokens, 1);
		TokenStream copy = original.copy(2);
		
		assertEquals(1, original.getCurrentIndex());
		assertEquals(2, copy.getCurrentIndex());
		assertEquals(original.size(), copy.size());
		assertEquals("third", copy.getCurrentToken().value());
		
		copy.consumeToken();
		assertEquals(1, original.getCurrentIndex());
		assertEquals(3, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithNegativeIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens);
		
		assertThrows(IndexOutOfBoundsException.class, () -> stream.copy(-1));
	}
	
	@Test
	void copyFromZero() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream original = new TokenStream(tokens, 2);
		TokenStream copy = original.copyFromZero();
		
		assertEquals(2, original.getCurrentIndex());
		assertEquals(0, copy.getCurrentIndex());
		assertEquals(original.size(), copy.size());
		assertEquals("first", copy.getCurrentToken().value());
		
		copy.consumeToken();
		assertEquals(2, original.getCurrentIndex());
		assertEquals(1, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithCurrentIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream original = new TokenStream(tokens, 1);
		TokenStream copy = original.copyWithCurrentIndex();
		
		assertEquals(1, original.getCurrentIndex());
		assertEquals(1, copy.getCurrentIndex());
		assertEquals(original.size(), copy.size());
		assertEquals("second", copy.getCurrentToken().value());
		
		copy.consumeToken();
		assertEquals(1, original.getCurrentIndex());
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void copyEmptyStream() {
		TokenStream original = new TokenStream(Collections.emptyList());
		TokenStream copy1 = original.copy(0);
		TokenStream copy2 = original.copyFromZero();
		TokenStream copy3 = original.copyWithCurrentIndex();
		
		assertTrue(copy1.isEmpty());
		assertTrue(copy2.isEmpty());
		assertTrue(copy3.isEmpty());
		assertEquals(0, copy1.getCurrentIndex());
		assertEquals(0, copy2.getCurrentIndex());
		assertEquals(0, copy3.getCurrentIndex());
	}
	
	@Test
	void fullWorkflowWithoutShadowTokens() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"), createToken("fourth"));
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals("first", stream.getCurrentToken().value());
		assertTrue(stream.hasToken());
		
		assertEquals("first", stream.readToken().value());
		assertEquals("second", stream.readToken().value());
		assertEquals("third", stream.getCurrentToken().value());
		
		TokenStream lookahead = stream.lookaheadStream();
		assertEquals(2, lookahead.size());
		assertEquals("third", lookahead.readToken().value());
		
		TokenStream lookbehind = stream.lookbehindStream();
		assertEquals(2, lookbehind.size());
		assertEquals("second", lookbehind.readToken().value());
		
		assertEquals("third", stream.readToken().value());
		assertEquals("fourth", stream.readToken().value());
		assertFalse(stream.hasToken());
		
		stream.reset();
		assertEquals("first", stream.getCurrentToken().value());
	}
	
	@Test
	void fullWorkflowWithShadowTokens() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createToken("first"),
			createShadowToken("whitespace"),
			createToken("second"),
			createShadowToken("comment2"),
			createToken("third")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertTrue(stream.hasToken());
		assertEquals("first", stream.getCurrentToken().value());
		
		assertEquals("first", stream.readToken().value());
		assertEquals("second", stream.readToken().value());
		assertEquals("third", stream.getCurrentToken().value());
		
		assertEquals("third", stream.readToken().value());
		assertFalse(stream.hasToken());
		
		stream.reset();
		assertEquals("first", stream.getCurrentToken().value());
	}
	
	@Test
	void modificationIndependence() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		TokenStream original = new TokenStream(tokens, 1);
		
		TokenStream lookahead = original.lookaheadStream();
		TokenStream lookbehind = original.lookbehindStream();
		TokenStream copy = original.copyWithCurrentIndex();
		
		original.consumeToken();
		lookahead.consumeToken();
		lookbehind.consumeToken();
		copy.reverse();
		
		assertEquals(2, original.getCurrentIndex());
		assertEquals(1, lookahead.getCurrentIndex());
		assertEquals(1, lookbehind.getCurrentIndex());
		assertEquals(1, copy.getCurrentIndex());
	}
	
	@Test
	void edgeCaseOnlyShadowTokensAtEnd() {
		List<Token> tokens = List.of(
			createToken("actual"),
			createShadowToken("comment1"),
			createShadowToken("comment2")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertTrue(stream.hasToken());
		assertEquals("actual", stream.readToken().value());
		assertFalse(stream.hasToken());
		
		assertThrows(IndexOutOfBoundsException.class, stream::getCurrentToken);
		assertThrows(NoSuchElementException.class, stream::readToken);
		assertThrows(NoSuchElementException.class, stream::consumeToken);
	}
	
	@Test
	void edgeCaseShadowTokensAtBeginning() {
		List<Token> tokens = List.of(
			createShadowToken("comment1"),
			createShadowToken("comment2"),
			createToken("actual")
		);
		TokenStream stream = new TokenStream(tokens);
		
		assertTrue(stream.hasToken());
		assertEquals("actual", stream.getCurrentToken().value());
		assertEquals(0, stream.getCurrentIndex());
		
		assertEquals("actual", stream.readToken().value());
		assertEquals(3, stream.getCurrentIndex());
		assertFalse(stream.hasToken());
	}
}
