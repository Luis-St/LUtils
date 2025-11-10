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

class CodecBuilderFunction11Test {
	
	@Test
	void createWithElevenInputs() {
		CodecBuilderFunction11<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Double> function = (a, b, c, d, e, f, g, h, i, j, k) -> {
			return (a + b + c + d + e + f + g + h + i + j + k) / 11.0;
		};
		
		Double result = function.create(11, 22, 33, 44, 55, 66, 77, 88, 99, 110, 121);
		
		assertEquals(66.0, result);
	}
	
	@Test
	void createCustomRecord() {
		record Data(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k) {}
		CodecBuilderFunction11<String, String, String, String, String, String, String, String, String, String, String, Data> function = Data::new;
		
		Data result = function.create("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
		
		assertEquals("1", result.a());
		assertEquals("11", result.k());
	}
}
