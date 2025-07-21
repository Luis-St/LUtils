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

class CodecGroupingFunction2Test {
	
	@Test
	void createWithTwoInputs() {
		CodecGroupingFunction2<String, Integer, String> function = (str, num) -> str + ":" + num;
		
		String result = function.create("hello", 42);
		
		assertEquals("hello:42", result);
	}
	
	@Test
	void createWithNullInputs() {
		CodecGroupingFunction2<String, String, String> function = (first, second) -> (first == null ? "null" : first) + "-" + (second == null ? "null" : second);
		
		assertEquals("null-null", function.create(null, null));
		assertEquals("hello-null", function.create("hello", null));
		assertEquals("null-world", function.create(null, "world"));
	}
	
	@Test
	void createWithMathOperation() {
		CodecGroupingFunction2<Integer, Integer, Integer> function = Integer::sum;
		Integer result = function.create(10, 20);
		
		assertEquals(30, result);
	}
}
