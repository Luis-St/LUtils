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
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenStream}.<br>
 *
 * @author Luis-St
 */
class TokenStreamTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED);
	}
	
	@Test
	void createMutableWithTokens() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		
		TokenStream stream = TokenStream.createMutable(tokens);
		
		assertInstanceOf(MutableTokenStream.class, stream);
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(2, stream.size());
	}
	
	@Test
	void createMutableWithNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createMutable(null));
	}
	
	@Test
	void createMutableWithTokensAndIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		
		TokenStream stream = TokenStream.createMutable(tokens, 1);
		
		assertInstanceOf(MutableTokenStream.class, stream);
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(2, stream.size());
	}
	
	@Test
	void createMutableWithNullTokensAndIndex() {
		assertThrows(NullPointerException.class, () -> TokenStream.createMutable(null, 0));
	}
	
	@Test
	void createMutableWithNegativeIndex() {
		List<Token> tokens = List.of(createToken("a"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> TokenStream.createMutable(tokens, -1));
	}
	
	@Test
	void createImmutableWithTokens() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		
		TokenStream stream = TokenStream.createImmutable(tokens);
		
		assertInstanceOf(ImmutableTokenStream.class, stream);
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(2, stream.size());
	}
	
	@Test
	void createImmutableWithNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createImmutable(null));
	}
	
	@Test
	void createImmutableWithTokensAndIndex() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		
		TokenStream stream = TokenStream.createImmutable(tokens, 1);
		
		assertInstanceOf(ImmutableTokenStream.class, stream);
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(2, stream.size());
	}
	
	@Test
	void createImmutableWithNullTokensAndIndex() {
		assertThrows(NullPointerException.class, () -> TokenStream.createImmutable(null, 0));
	}
	
	@Test
	void createImmutableWithNegativeIndex() {
		List<Token> tokens = List.of(createToken("a"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> TokenStream.createImmutable(tokens, -1));
	}
	
	@Test
	void emptyStreamIsEmpty() {
		assertTrue(TokenStream.EMPTY.isEmpty());
		assertEquals(0, TokenStream.EMPTY.size());
		assertEquals(0, TokenStream.EMPTY.getCurrentIndex());
	}
	
	@Test
	void emptyStreamOperations() {
		assertThrows(UnsupportedOperationException.class, TokenStream.EMPTY::reset);
		assertThrows(UnsupportedOperationException.class, () -> TokenStream.EMPTY.advanceTo(1));
		assertThrows(EndOfTokenStreamException.class, TokenStream.EMPTY::getCurrentToken);
		assertThrows(UnsupportedOperationException.class, TokenStream.EMPTY::readToken);
	}
}
