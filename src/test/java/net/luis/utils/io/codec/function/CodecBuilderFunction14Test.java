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

class CodecBuilderFunction14Test {
	
	@Test
	void createWithFourteenInputs() {
		CodecBuilderFunction14<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> function = (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> {
			return a + b + c + d + e + f + g + h + i + j + k + l + m + n;
		};
		
		String result = function.create("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N");
		
		assertEquals("ABCDEFGHIJKLMN", result);
	}
	
	@Test
	void createWithNumbers() {
		CodecBuilderFunction14<Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> function = (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> {
			return (a + b + c + d + e + f + g + h + i + j + k + l + m + n) / 14.0;
		};
		
		Double result = function.create(14.0, 28.0, 42.0, 56.0, 70.0, 84.0, 98.0, 112.0, 126.0, 140.0, 154.0, 168.0, 182.0, 196.0);
		
		assertEquals(105.0, result);
	}
}
