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

class CodecGroupingFunction4Test {
	
	@Test
	void createWithFourInputs() {
		CodecGroupingFunction4<String, String, String, String, String> function = (a, b, c, d) -> String.join("-", a, b, c, d);
		
		String result = function.create("alpha", "beta", "gamma", "delta");
		
		assertEquals("alpha-beta-gamma-delta", result);
	}
	
	@Test
	void createWithNumbers() {
		CodecGroupingFunction4<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a * b + c * d;
		
		Integer result = function.create(2, 3, 4, 5);
		
		assertEquals(26, result);
	}
	
	@Test
	void createWithComplexObject() {
		record Person(String first, String last, Integer age, String city) {}
		CodecGroupingFunction4<String, String, Integer, String, Person> function = Person::new;
		
		Person result = function.create("John", "Doe", 30, "NYC");
		
		assertEquals("John", result.first());
		assertEquals("Doe", result.last());
		assertEquals(30, result.age());
		assertEquals("NYC", result.city());
	}
}
