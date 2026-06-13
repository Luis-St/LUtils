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

package net.luis.utils.io.database.dialect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlFeature}.<br>
 *
 * @author Luis-St
 */
class SqlFeatureTest {
	
	@Test
	void valueOfUnknownName() {
		assertThrows(IllegalArgumentException.class, () -> SqlFeature.valueOf("NOPE"));
	}
	
	@Test
	void valueOfNullName() {
		assertThrows(NullPointerException.class, () -> SqlFeature.valueOf(null));
	}
	
	@Test
	void valuesContainsAllConstants() {
		assertEquals(27, SqlFeature.values().length);
		assertNotNull(SqlFeature.RETURNING);
		assertNotNull(SqlFeature.JOINED_DML);
		assertNotNull(SqlFeature.OFFSET_WITHOUT_LIMIT);
		assertNotNull(SqlFeature.UPSERT);
		assertNotNull(SqlFeature.UPDATE_RETURNING);
	}
	
	@Test
	void valueOfRoundTripsEveryConstant() {
		for (SqlFeature feature : SqlFeature.values()) {
			assertSame(feature, SqlFeature.valueOf(feature.name()));
		}
	}
}
