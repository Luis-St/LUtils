/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.token.actions.core;

import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenConverter}.<br>
 *
 * @author Luis-St
 */
class TokenConverterTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void convertWithValidToken() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		Token input = createToken("hello");
		
		Token result = converter.convert(input);
		
		assertEquals("HELLO", result.value());
	}
	
	@Test
	void convertWithEmptyToken() {
		TokenConverter converter = token -> createToken(token.value() + "_converted");
		Token input = createToken("");
		
		Token result = converter.convert(input);
		
		assertEquals("_converted", result.value());
	}
	
	@Test
	void convertReturnsNewToken() {
		TokenConverter converter = token -> createToken(token.value());
		Token input = createToken("test");
		
		Token result = converter.convert(input);
		
		assertEquals(input.value(), result.value());
		assertNotSame(input, result);
	}
	
	@Test
	void convertWithComplexTransformation() {
		TokenConverter converter = token -> {
			String value = token.value();
			if (value.isEmpty()) {
				return createToken("empty");
			}
			return createToken(new StringBuilder(value).reverse().toString());
		};
		
		Token input = createToken("abc");
		Token result = converter.convert(input);
		
		assertEquals("cba", result.value());
	}
	
	@Test
	void convertPreservesTokenProperties() {
		TokenConverter converter = token -> {
			return new SimpleToken(token.value().toUpperCase(), token.position());
		};
		
		Token input = createToken("test");
		Token result = converter.convert(input);
		
		assertEquals("TEST", result.value());
		assertEquals(input.position(), result.position());
	}
}
