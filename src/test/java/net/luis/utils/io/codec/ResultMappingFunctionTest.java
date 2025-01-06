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
 * Test class for {@link ResultMappingFunction}.<br>
 *
 * @author Luis-St
 */
class ResultMappingFunctionTest {
	
	@Test
	void direct() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.direct(null));
	}
	
	@Test
	void throwable() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.throwable(null));
	}
	
	@Test
	void apply() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(Integer::parseInt);
		
		Result<Integer> success = function.apply(Result.success("1"));
		assertTrue(success.isSuccess());
		assertEquals(1, success.orThrow());
		
		Result<Integer> error = function.apply(Result.error("error"));
		assertTrue(error.isError());
	}
}
