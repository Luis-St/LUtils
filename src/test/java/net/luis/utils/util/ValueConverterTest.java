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

package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ValueConverter}.<br>
 *
 * @author Luis-St
 */
class ValueConverterTest {
	
	private static final ValueConverter<String, Integer> CONVERTER = new ValueConverter<String, Integer>() {
		@Override
		public @NotNull String convert(@Nullable Integer value) {
			return String.valueOf(Objects.requireNonNull(value));
		}
		
		@Override
		public @NotNull Integer parse(@Nullable String value) {
			return Integer.parseInt(Objects.requireNonNull(value));
		}
	};
	
	@Test
	void parse() {
		assertThrows(NullPointerException.class, () -> CONVERTER.parse(null));
		assertThrows(NumberFormatException.class, () -> CONVERTER.parse("a"));
		assertEquals(1, CONVERTER.parse("1"));
		assertEquals(-1, CONVERTER.parse("-1"));
	}
	
	@Test
	void tryParse() {
		assertDoesNotThrow(() -> CONVERTER.tryParse(null));
		assertDoesNotThrow(() -> CONVERTER.tryParse("a"));
		assertEquals(Optional.empty(), CONVERTER.tryParse(null));
		assertEquals(Optional.empty(), CONVERTER.tryParse("a"));
		assertEquals(Optional.of(1), CONVERTER.tryParse("1"));
		assertEquals(Optional.of(-1), CONVERTER.tryParse("-1"));
	}
	
	@Test
	void convert() {
		assertThrows(NullPointerException.class, () -> CONVERTER.convert(null));
		assertEquals("1", CONVERTER.convert(1));
		assertEquals("-1", CONVERTER.convert(-1));
	}
	
	@Test
	void tryConvert() {
		assertDoesNotThrow(() -> CONVERTER.tryConvert(null));
		assertEquals(Optional.empty(), CONVERTER.tryConvert(null));
		assertEquals(Optional.of("1"), CONVERTER.tryConvert(1));
		assertEquals(Optional.of("-1"), CONVERTER.tryConvert(-1));
	}
	
	@Test
	void asConverter() {
		assertDoesNotThrow(() -> CONVERTER.asConverter(null));
		assertNotNull(CONVERTER.asConverter(String::valueOf));
		assertSame(CONVERTER, CONVERTER.asConverter(String::valueOf));
	}
}
