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
 * Test class for {@link SqlFractionalParameter}.<br>
 *
 * @author Luis-St
 */
class SqlFractionalParameterTest {
	
	@Test
	void constructWithValidDigits() {
		SqlFractionalParameter parameter = new SqlFractionalParameter(6);
		assertEquals(6, parameter.digits());
	}
	
	@Test
	void constructWithNegativeDigits() {
		assertThrows(IllegalArgumentException.class, () -> new SqlFractionalParameter(-1));
	}
	
	@Test
	void constructWithDigitsAboveNine() {
		assertThrows(IllegalArgumentException.class, () -> new SqlFractionalParameter(10));
	}
	
	@Test
	void constructWithZeroDigitsBoundary() {
		SqlFractionalParameter parameter = new SqlFractionalParameter(0);
		assertEquals(0, parameter.digits());
	}
	
	@Test
	void constructWithNineDigitsBoundary() {
		SqlFractionalParameter parameter = new SqlFractionalParameter(9);
		assertEquals(9, parameter.digits());
	}
	
	@Test
	void equalsSameDigits() {
		assertEquals(new SqlFractionalParameter(3), new SqlFractionalParameter(3));
	}
	
	@Test
	void equalsDifferentDigits() {
		assertNotEquals(new SqlFractionalParameter(3), new SqlFractionalParameter(4));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new SqlFractionalParameter(3));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new SqlFractionalParameter(3));
	}
	
	@Test
	void digitsReturnsConfiguredValue() {
		assertEquals(7, new SqlFractionalParameter(7).digits());
	}
	
	@Test
	void hashCodeConsistentForEqualParameters() {
		assertEquals(new SqlFractionalParameter(3).hashCode(), new SqlFractionalParameter(3).hashCode());
	}
	
	@Test
	void toStringContainsDigits() {
		assertTrue(new SqlFractionalParameter(6).toString().contains("digits=6"));
	}
}
