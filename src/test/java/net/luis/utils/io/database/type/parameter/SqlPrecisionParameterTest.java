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
 * Test class for {@link SqlPrecisionParameter}.<br>
 *
 * @author Luis-St
 */
class SqlPrecisionParameterTest {
	
	@Test
	void constructWithValidPrecisionAndScale() {
		SqlPrecisionParameter parameter = new SqlPrecisionParameter(10, 2);
		assertEquals(10, parameter.precision());
		assertEquals(2, parameter.scale());
	}
	
	@Test
	void constructWithZeroPrecision() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPrecisionParameter(0, 0));
	}
	
	@Test
	void constructWithNegativePrecision() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPrecisionParameter(-1, 0));
	}
	
	@Test
	void constructWithNegativeScale() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPrecisionParameter(10, -1));
	}
	
	@Test
	void constructWithScaleExceedingPrecision() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPrecisionParameter(2, 3));
	}
	
	@Test
	void constructWithPrecisionOfOneBoundary() {
		SqlPrecisionParameter parameter = new SqlPrecisionParameter(1, 0);
		assertEquals(1, parameter.precision());
	}
	
	@Test
	void constructWithZeroScaleBoundary() {
		SqlPrecisionParameter parameter = new SqlPrecisionParameter(5, 0);
		assertEquals(0, parameter.scale());
	}
	
	@Test
	void constructWithScaleEqualToPrecisionBoundary() {
		SqlPrecisionParameter parameter = new SqlPrecisionParameter(5, 5);
		assertEquals(5, parameter.scale());
	}
	
	@Test
	void equalsSamePrecisionAndScale() {
		assertEquals(new SqlPrecisionParameter(10, 2), new SqlPrecisionParameter(10, 2));
	}
	
	@Test
	void equalsDifferentPrecision() {
		assertNotEquals(new SqlPrecisionParameter(10, 2), new SqlPrecisionParameter(11, 2));
	}
	
	@Test
	void equalsDifferentScale() {
		assertNotEquals(new SqlPrecisionParameter(10, 2), new SqlPrecisionParameter(10, 3));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new SqlPrecisionParameter(10, 2));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new SqlPrecisionParameter(10, 2));
	}
	
	@Test
	void precisionAndScaleReturnConfiguredValues() {
		SqlPrecisionParameter parameter = new SqlPrecisionParameter(12, 4);
		assertEquals(12, parameter.precision());
		assertEquals(4, parameter.scale());
	}
	
	@Test
	void hashCodeConsistentForEqualParameters() {
		assertEquals(new SqlPrecisionParameter(10, 2).hashCode(), new SqlPrecisionParameter(10, 2).hashCode());
	}
	
	@Test
	void toStringContainsPrecisionAndScale() {
		String string = new SqlPrecisionParameter(10, 2).toString();
		assertTrue(string.contains("precision=10"));
		assertTrue(string.contains("scale=2"));
	}
}
