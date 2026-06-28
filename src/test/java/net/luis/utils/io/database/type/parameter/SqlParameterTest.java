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

package net.luis.utils.io.database.type.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlParameter}.<br>
 *
 * @author Luis-St
 */
class SqlParameterTest {
	
	@Test
	void lengthCreatesLengthParameter() {
		SqlLengthParameter parameter = SqlParameter.length(20);
		assertInstanceOf(SqlLengthParameter.class, parameter);
		assertEquals(20, parameter.length());
	}
	
	@Test
	void lengthWithInvalidValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlParameter.length(0));
	}
	
	@Test
	void precisionCreatesPrecisionParameter() {
		SqlPrecisionParameter parameter = SqlParameter.precision(10, 2);
		assertInstanceOf(SqlPrecisionParameter.class, parameter);
		assertEquals(10, parameter.precision());
		assertEquals(2, parameter.scale());
	}
	
	@Test
	void precisionWithInvalidValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlParameter.precision(-1, 0));
	}
	
	@Test
	void fractionalCreatesFractionalParameter() {
		SqlFractionalParameter parameter = SqlParameter.fractional(6);
		assertInstanceOf(SqlFractionalParameter.class, parameter);
		assertEquals(6, parameter.digits());
	}
	
	@Test
	void fractionalWithInvalidValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlParameter.fractional(10));
	}
}
