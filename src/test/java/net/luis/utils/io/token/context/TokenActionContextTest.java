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

package net.luis.utils.io.token.context;

import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenActionContext}.<br>
 *
 * @author Luis-St
 */
class TokenActionContextTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullStream() {
		assertThrows(NullPointerException.class, () -> new TokenActionContext(null));
	}
	
	@Test
	void constructorWithMutableStream() {
		TokenStream mutableStream = TokenStream.createMutable(List.of(createToken("test")));
		assertThrows(IllegalArgumentException.class, () -> new TokenActionContext(mutableStream));
	}
	
	@Test
	void constructorWithImmutableStream() {
		TokenStream immutableStream = TokenStream.createImmutable(List.of(createToken("test")));
		assertDoesNotThrow(() -> new TokenActionContext(immutableStream));
	}
	
	@Test
	void constructorWithValidStream() {
		TokenStream immutableStream = TokenStream.createImmutable(List.of(createToken("test")));
		TokenActionContext context = new TokenActionContext(immutableStream);
		
		assertEquals(immutableStream, context.stream());
	}
	
	@Test
	void streamAccessor() {
		TokenStream immutableStream = TokenStream.createImmutable(List.of(createToken("hello"), createToken("world")));
		TokenActionContext context = new TokenActionContext(immutableStream);
		
		assertSame(immutableStream, context.stream());
	}
	
	@Test
	void constructorWithEmptyImmutableStream() {
		TokenStream emptyStream = TokenStream.createImmutable(List.of());
		TokenActionContext context = new TokenActionContext(emptyStream);
		
		assertEquals(emptyStream, context.stream());
	}
	
	@Test
	void testEquals() {
		TokenStream stream1 = TokenStream.createImmutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createImmutable(List.of(createToken("test")));
		TokenStream stream3 = TokenStream.createImmutable(List.of(createToken("different")));
		
		TokenActionContext context1 = new TokenActionContext(stream1);
		TokenActionContext context2 = new TokenActionContext(stream1);
		TokenActionContext context3 = new TokenActionContext(stream2);
		TokenActionContext context4 = new TokenActionContext(stream3);
		
		assertEquals(context1, context2);
		assertNotEquals(context1, context3);
		assertNotEquals(context1, context4);
		assertNotEquals(context1, null);
		assertNotEquals(context1, "string");
	}
	
	@Test
	void testHashCode() {
		TokenStream stream1 = TokenStream.createImmutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createImmutable(List.of(createToken("test")));
		
		TokenActionContext context1 = new TokenActionContext(stream1);
		TokenActionContext context2 = new TokenActionContext(stream1);
		TokenActionContext context3 = new TokenActionContext(stream2);
		
		assertEquals(context1.hashCode(), context2.hashCode());
	}
	
	@Test
	void testToString() {
		TokenStream immutableStream = TokenStream.createImmutable(List.of(createToken("test")));
		TokenActionContext context = new TokenActionContext(immutableStream);
		
		String toString = context.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("TokenActionContext"));
		assertTrue(toString.contains("stream"));
	}
}
