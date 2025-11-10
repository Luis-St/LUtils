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

class CodecBuilderFunction1Test {
	
	@Test
	void createWithSingleInput() {
		CodecBuilderFunction1<String, Integer> function = String::length;
		
		Integer result = function.create("hello");
		
		assertEquals(5, result);
	}
	
	@Test
	void createWithNullInput() {
		CodecBuilderFunction1<String, String> function = input -> input == null ? "null" : input.toUpperCase();
		
		String result = function.create(null);
		
		assertEquals("null", result);
	}
	
	@Test
	void createWithComplexLogic() {
		CodecBuilderFunction1<Integer, String> function = input -> {
			if (input < 0) {
				return "negative";
			}
			if (input == 0) {
				return "zero";
			}
			return "positive";
		};
		
		assertEquals("negative", function.create(-5));
		assertEquals("zero", function.create(0));
		assertEquals("positive", function.create(10));
	}
}
