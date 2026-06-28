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

package net.luis.utils.io.database.migration;

import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSchemaSnapshot}.<br>
 *
 * @author Luis-St
 */
class SqlSchemaSnapshotTest {
	
	@Test
	void constructWithColumnsAndConstraints() {
		List<SqlSchemaColumnInfo> columns = List.of(new SqlSchemaColumnInfo("t", "c", Types.INTEGER, null, false, false, false, false, 0));
		Map<String, List<SqlCheckConstraintInfo>> checks = Map.of("t", List.of(new SqlCheckConstraintInfo("chk", "c > 0")));
		SqlSchemaSnapshot snapshot = new SqlSchemaSnapshot(columns, checks);
		assertEquals(columns, snapshot.columns());
		assertEquals(checks, snapshot.checkConstraints());
	}
	
	@Test
	void constructWithEmptyCollections() {
		SqlSchemaSnapshot snapshot = assertDoesNotThrow(() -> new SqlSchemaSnapshot(List.of(), Map.of()));
		assertTrue(snapshot.columns().isEmpty());
		assertTrue(snapshot.checkConstraints().isEmpty());
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaSnapshot(null, Map.of()));
	}
	
	@Test
	void constructWithNullCheckConstraints() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaSnapshot(List.of(), null));
	}
}
