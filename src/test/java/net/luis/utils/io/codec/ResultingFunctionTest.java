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

package net.luis.utils.io.codec;

import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class ResultingFunctionTest {
	
	@Test
	void direct() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.direct(null));
	}
	
	@Test
	void throwable() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.throwable(null));
	}
	
	@Test
	void apply() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(Integer::parseInt);
		
		Result<Integer> success = function.apply("1");
		assertTrue(success.isSuccess());
		assertEquals(1, success.orThrow());
		
		Result<Integer> error = function.apply("error");
		assertTrue(error.isError());
	}
}
