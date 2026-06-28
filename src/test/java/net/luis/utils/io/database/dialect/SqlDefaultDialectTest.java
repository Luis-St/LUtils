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

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.SqlCheckConstraintInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDefaultDialect}.<br>
 *
 * @author Luis-St
 */
class SqlDefaultDialectTest {
	
	private static final SqlDefaultDialect DIALECT = new SqlDefaultDialect();
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
	}
	
	@Test
	void nameReturnsDefault() {
		assertEquals("Default", DIALECT.name());
	}
	
	@Test
	void getCheckConstraintsQueryStringIsNull() {
		assertNull(DIALECT.getCheckConstraintsQueryString());
	}
	
	@Test
	void maxBindParametersInheritsDefault() {
		assertEquals(65535, DIALECT.maxBindParameters());
	}
	
	@Test
	void getCheckConstraintsReturnsEmptyWhenNoQuery() throws SqlException {
		List<SqlCheckConstraintInfo> constraints = DIALECT.getCheckConstraints(SqlTestFixtures.placeholderConnection(), "public", "test_table");
		assertTrue(constraints.isEmpty());
	}
	
	@Test
	void supportedFeatureSetIsExactlyFour() {
		for (SqlFeature feature : SqlFeature.values()) {
			boolean expected = feature == SqlFeature.CTE || feature == SqlFeature.RECURSIVE_CTE
				|| feature == SqlFeature.ALTER_COLUMN || feature == SqlFeature.ADD_CONSTRAINT;
			assertEquals(expected, DIALECT.isFeatureSupported(feature), "Feature " + feature);
		}
	}
}
