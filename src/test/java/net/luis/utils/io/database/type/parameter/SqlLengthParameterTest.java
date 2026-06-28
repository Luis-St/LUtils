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
 * Test class for {@link SqlLengthParameter}.<br>
 *
 * @author Luis-St
 */
class SqlLengthParameterTest {
	
	@Test
	void constructWithPositiveLength() {
		SqlLengthParameter parameter = new SqlLengthParameter(10);
		assertEquals(10, parameter.length());
	}
	
	@Test
	void constructWithZeroLength() {
		assertThrows(IllegalArgumentException.class, () -> new SqlLengthParameter(0));
	}
	
	@Test
	void constructWithNegativeLength() {
		assertThrows(IllegalArgumentException.class, () -> new SqlLengthParameter(-5));
	}
	
	@Test
	void constructWithLengthOfOneBoundary() {
		SqlLengthParameter parameter = new SqlLengthParameter(1);
		assertEquals(1, parameter.length());
	}
	
	@Test
	void equalsSameLength() {
		assertEquals(new SqlLengthParameter(8), new SqlLengthParameter(8));
	}
	
	@Test
	void equalsDifferentLength() {
		assertNotEquals(new SqlLengthParameter(8), new SqlLengthParameter(9));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new SqlLengthParameter(8));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new SqlLengthParameter(8));
	}
	
	@Test
	void lengthReturnsConfiguredValue() {
		assertEquals(42, new SqlLengthParameter(42).length());
	}
	
	@Test
	void hashCodeConsistentForEqualParameters() {
		assertEquals(new SqlLengthParameter(8).hashCode(), new SqlLengthParameter(8).hashCode());
	}
	
	@Test
	void toStringContainsLength() {
		assertTrue(new SqlLengthParameter(10).toString().contains("length=10"));
	}
}
