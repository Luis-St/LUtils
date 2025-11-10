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

class CodecBuilderFunction6Test {
	
	@Test
	void createWithSixInputs() {
		CodecBuilderFunction6<Integer, Integer, Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d, e, f) -> a * b * c * d * e * f;
		
		Integer result = function.create(1, 2, 1, 2, 1, 3);
		
		assertEquals(12, result);
	}
	
	@Test
	void createWithBooleans() {
		CodecBuilderFunction6<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean> function = (a, b, c, d, e, f) -> a && b && c && d && e && f;
		
		assertTrue(function.create(true, true, true, true, true, true));
		assertFalse(function.create(true, true, false, true, true, true));
	}
}
