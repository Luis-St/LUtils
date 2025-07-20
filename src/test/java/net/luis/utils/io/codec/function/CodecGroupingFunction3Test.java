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

class CodecGroupingFunction3Test {
	
	@Test
	void createWithThreeInputs() {
		CodecGroupingFunction3<String, Integer, Boolean, String> function = (str, num, bool) -> str + ":" + num + ":" + bool;
		
		String result = function.create("test", 123, true);
		
		assertEquals("test:123:true", result);
	}
	
	@Test
	void createWithMixedTypes() {
		CodecGroupingFunction3<Integer, Integer, Integer, Double> function = (a, b, c) -> (a + b + c) / 3.0;
		
		Double result = function.create(10, 20, 30);
		
		assertEquals(20.0, result);
	}
	
	@Test
	void createConditionalLogic() {
		CodecGroupingFunction3<String, String, String, String> function = 
			(first, second, third) -> {
				if (first != null && second != null && third != null) {
					return first + second + third;
				}
				return "incomplete";
			};
		
		assertEquals("abcdef", function.create("ab", "cd", "ef"));
		assertEquals("incomplete", function.create("ab", null, "ef"));
	}
}
