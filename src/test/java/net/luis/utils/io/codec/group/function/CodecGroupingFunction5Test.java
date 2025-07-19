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

class CodecGroupingFunction5Test {
	
	@Test
	void createWithFiveInputs() {
		CodecGroupingFunction5<Integer, Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d, e) -> a + b + c + d + e;
		
		Integer result = function.create(1, 2, 3, 4, 5);
		
		assertEquals(15, result);
	}
	
	@Test
	void createWithStrings() {
		CodecGroupingFunction5<String, String, String, String, String, String> function = (a, b, c, d, e) -> a + b + c + d + e;
		
		String result = function.create("A", "B", "C", "D", "E");
		
		assertEquals("ABCDE", result);
	}
	
	@Test
	void createCustomObject() {
		record Data(String a, String b, String c, String d, String e) {}
		CodecGroupingFunction5<String, String, String, String, String, Data> function = Data::new;
		
		Data result = function.create("1", "2", "3", "4", "5");
		
		assertEquals(new Data("1", "2", "3", "4", "5"), result);
	}
}
