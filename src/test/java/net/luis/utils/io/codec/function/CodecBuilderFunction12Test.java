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

class CodecBuilderFunction12Test {
	
	@Test
	void createWithTwelveInputs() {
		CodecBuilderFunction12<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d, e, f, g, h, i, j, k, l) -> {
			return a + b + c + d + e + f + g + h + i + j + k + l;
		};
		
		Integer result = function.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		
		assertEquals(78, result);
	}
	
	@Test
	void createWithMonths() {
		CodecBuilderFunction12<String, String, String, String, String, String, String, String, String, String, String, String, String> function = (jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec) -> {
			return String.join(",", jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec);
		};
		
		String result = function.create("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		
		assertEquals("Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec", result);
	}
}
