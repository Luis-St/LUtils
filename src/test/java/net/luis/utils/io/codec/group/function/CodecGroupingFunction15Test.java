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

class CodecGroupingFunction15Test {
	
	@Test
	void createWithFifteenInputs() {
		CodecGroupingFunction15<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> a + b + c + d + e + f + g + h + i + j + k + l + m + n + o;
		
		Integer result = function.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
		
		assertEquals(120, result);
	}
	
	@Test
	void createCustomObject() {
		record LargeData(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String o) {}
		CodecGroupingFunction15<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, LargeData> function = LargeData::new;
		
		LargeData result = function.create("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
		
		assertEquals("1", result.a());
		assertEquals("15", result.o());
	}
}
