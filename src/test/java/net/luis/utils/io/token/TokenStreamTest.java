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

import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenStream}.<br>
 *
 * @author Luis-St
 */
class TokenStreamTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
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
	}
	
	@Test
	void size() {
		assertEquals(0, new TokenStream(Collections.emptyList()).size());
		assertEquals(1, new TokenStream(List.of(createToken("test"))).size());
		assertEquals(3, new TokenStream(List.of(createToken("a"), createToken("b"), createToken("c"))).size());
	}
	
	@Test
	void getCurrentIndex() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		assertEquals(0, new TokenStream(tokens).getCurrentIndex());
		assertEquals(1, new TokenStream(tokens, 1).getCurrentIndex());
	}
	
	@Test
	void getCurrentToken() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		assertEquals("second", stream.getCurrentToken().value());
	}
	
	@Test
	void getCurrentTokenWithEmptyStream() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		assertThrows(IndexOutOfBoundsException.class, stream::getCurrentToken);
	}
	
	@Test
	void getCurrentTokenAfterConsumingAllTokens() {
		List<Token> tokens = List.of(createToken("only"));
		TokenStream stream = new TokenStream(tokens);
		
		stream.consumeToken();
		assertThrows(IndexOutOfBoundsException.class, stream::getCurrentToken);
	}
	
	@Test
	void hasToken() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals(0, stream.getCurrentIndex());
		assertTrue(stream.hasToken());
		stream.consumeToken();
		assertEquals(1, stream.getCurrentIndex());
		assertTrue(stream.hasToken());
		stream.consumeToken();
		assertEquals(2, stream.getCurrentIndex());
		assertFalse(stream.hasToken());
	}
	
	@Test
	void hasTokenWithEmptyStream() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		assertFalse(stream.hasToken());
	}
	
	@Test
	void consumeToken() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(1, stream.consumeToken());
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(2, stream.consumeToken());
		assertEquals(2, stream.getCurrentIndex());
		assertEquals(3, stream.consumeToken());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void consumeTokenWhenNoTokenAvailable() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		assertThrows(NoSuchElementException.class, stream::consumeToken);
		
		List<Token> tokens = List.of(createToken("only"));
		TokenStream stream2 = new TokenStream(tokens);
		stream2.consumeToken();
		assertThrows(NoSuchElementException.class, stream2::consumeToken);
	}
	
	@Test
	void readToken() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens);
		
		assertEquals("first", stream.readToken().value());
		assertEquals(1, stream.getCurrentIndex());
		assertEquals("second", stream.readToken().value());
		assertEquals(2, stream.getCurrentIndex());
		assertEquals("third", stream.readToken().value());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void readTokenWhenNoTokenAvailable() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		assertThrows(NoSuchElementException.class, stream::readToken);
		
		List<Token> tokens = List.of(createToken("only"));
		TokenStream stream2 = new TokenStream(tokens);
		stream2.readToken();
		assertThrows(NoSuchElementException.class, stream2::readToken);
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
	void lookaheadStreamWithEmptyOriginal() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		TokenStream lookahead = stream.lookaheadStream();
		
		assertTrue(lookahead.isEmpty());
		assertFalse(lookahead.hasToken());
	}
	
	@Test
	void lookaheadStreamIndependence() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 1);
		TokenStream lookahead = stream.lookaheadStream();
		
		lookahead.consumeToken();
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(1, lookahead.getCurrentIndex());
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
	void lookbehindStreamWithEmptyOriginal() {
		TokenStream stream = new TokenStream(Collections.emptyList());
		TokenStream lookbehind = stream.lookbehindStream();
		
		assertTrue(lookbehind.isEmpty());
		assertFalse(lookbehind.hasToken());
	}
	
	@Test
	void lookbehindStreamIndependence() {
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenStream stream = new TokenStream(tokens, 2);
		TokenStream lookbehind = stream.lookbehindStream();
		
		lookbehind.consumeToken();
		assertEquals(2, stream.getCurrentIndex());
		assertEquals(1, lookbehind.getCurrentIndex());
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
	void reverseAtDifferentPositions() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"), createToken("d"));
		
		TokenStream stream1 = new TokenStream(tokens, 0);
		stream1.reverse();
		assertEquals(3, stream1.getCurrentIndex());
		assertEquals("a", stream1.getCurrentToken().value());
		
		TokenStream stream2 = new TokenStream(tokens, 3);
		stream2.reverse();
		assertEquals(0, stream2.getCurrentIndex());
		assertEquals("d", stream2.getCurrentToken().value());
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
	void fullWorkflow() {
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
}
