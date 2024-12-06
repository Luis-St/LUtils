/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ValueParser}.<br>
 *
 * @author Luis-St
 */
class ValueParserTest {
	
	private static final ValueParser<String, Integer> PARSER = value -> Integer.parseInt(Objects.requireNonNull(value));
	
	@Test
	void parse() {
		assertThrows(NullPointerException.class, () -> PARSER.parse(null));
		assertThrows(NumberFormatException.class, () -> PARSER.parse("a"));
		assertEquals(1, PARSER.parse("1"));
		assertEquals(-1, PARSER.parse("-1"));
	}
	
	@Test
	void tryParse() {
		assertDoesNotThrow(() -> PARSER.tryParse(null));
		assertDoesNotThrow(() -> PARSER.tryParse("a"));
		assertEquals(Optional.empty(), PARSER.tryParse(null));
		assertEquals(Optional.empty(), PARSER.tryParse("a"));
		assertEquals(Optional.of(1), PARSER.tryParse("1"));
		assertEquals(Optional.of(-1), PARSER.tryParse("-1"));
	}
	
	@Test
	void asConverter() {
		assertThrows(NullPointerException.class, () -> PARSER.asConverter(null));
		assertNotNull(PARSER.asConverter(String::valueOf));
	}
}