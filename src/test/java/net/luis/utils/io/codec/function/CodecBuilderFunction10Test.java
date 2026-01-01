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

package net.luis.utils.io.codec.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodecBuilderFunction10Test {
	
	@Test
	void createWithTenInputs() {
		CodecBuilderFunction10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d, e, f, g, h, i, j) -> a + b + c + d + e + f + g + h + i + j;
		
		Integer result = function.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		assertEquals(55, result);
	}
	
	@Test
	void createWithStrings() {
		CodecBuilderFunction10<String, String, String, String, String, String, String, String, String, String, String> function = (a, b, c, d, e, f, g, h, i, j) -> a + b + c + d + e + f + g + h + i + j;
		
		String result = function.create("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
		
		assertEquals("ABCDEFGHIJ", result);
	}
}
