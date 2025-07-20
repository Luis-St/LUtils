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

package net.luis.utils.io.codec.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodecGroupingFunction16Test {
	
	@Test
	void createWithSixteenInputs() {
		CodecGroupingFunction16<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> function =
			(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) -> a + b + c + d + e + f + g + h + i + j + k + l + m + n + o + p;
		
		Integer result = function.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
		
		assertEquals(136, result);
	}
	
	@Test
	void createWithHexadecimalString() {
		CodecGroupingFunction16<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> function = 
			(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) -> a + b + c + d + e + f + g + h + i + j + k + l + m + n + o + p;
		
		String result = function.create("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F");
		
		assertEquals("0123456789ABCDEF", result);
	}
	
	@Test
	void createMaximumParameterRecord() {
		record MaxData(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String o, String p) {}
		CodecGroupingFunction16<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, MaxData> function = MaxData::new;
		
		MaxData result = function.create("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P");
		
		assertEquals("A", result.a());
		assertEquals("P", result.p());
	}
}
