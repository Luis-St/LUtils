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

package net.luis.utils.io.codec.group.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodecGroupingFunction7Test {
	
	@Test
	void createWithSevenInputs() {
		CodecGroupingFunction7<String, String, String, String, String, String, String, String> function = (a, b, c, d, e, f, g) -> a + b + c + d + e + f + g;
		
		String result = function.create("A", "B", "C", "D", "E", "F", "G");
		
		assertEquals("ABCDEFG", result);
	}
	
	@Test
	void createWithNumbers() {
		CodecGroupingFunction7<Double, Double, Double, Double, Double, Double, Double, Double> function = (a, b, c, d, e, f, g) -> (a + b + c + d + e + f + g) / 7.0;
		
		Double result = function.create(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
		
		assertEquals(4.0, result);
	}
}
