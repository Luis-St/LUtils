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
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MariaDbDialect}.<br>
 *
 * @author Luis-St
 */
class MariaDbDialectTest {
	
	private static final MariaDbDialect DIALECT = new MariaDbDialect();
	
	@Test
	void isFeatureSupportedNullFeature() {
		assertThrows(NullPointerException.class, () -> DIALECT.isFeatureSupported(null));
	}
	
	@Test
	void renderReturningNullColumns() {
		assertThrows(NullPointerException.class, () -> DIALECT.renderReturning(null));
	}
	
	@Test
	void isFeatureSupportedForSupportedFeature() {
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RECURSIVE_CTE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.WINDOW_FUNCTIONS));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_UPDATE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.FOR_SHARE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.SKIP_LOCKED));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.NO_WAIT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.UPSERT_SUFFIX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ROW_LOCKING));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.INSERT_OR_IGNORE));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RENAME_INDEX));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ALTER_COLUMN));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.ADD_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.DROP_CONSTRAINT));
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.JOINED_DML));
	}
	
	@Test
	void isFeatureSupportedForUnsupportedFeature() {
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.LATERAL_JOIN));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.NULLS_ORDERING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRUNCATE_CASCADE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.IS_DISTINCT_FROM));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TRANSACTIONAL_DDL));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPSERT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.ARRAY_TYPE));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.TABLE_REBUILD));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.UPDATE_RETURNING));
	}
	
	@Test
	void renderReturningEmptyColumns() throws SqlException {
		String sql = DIALECT.renderReturning(List.of()).sql();
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("*"));
	}
	
	@Test
	void renderReturningWithColumns() throws SqlException {
		String sql = DIALECT.renderReturning(List.of(SqlTestFixtures.integerColumn())).sql();
		assertTrue(sql.contains("RETURNING"));
		assertTrue(sql.contains("`id`"));
	}
	
	@Test
	void nameReturnsMariaDb() {
		assertEquals("MariaDB", DIALECT.name());
	}
	
	@Test
	void uuidTypeSupportedViaRegistry() throws SqlException {
		assertEquals("UUID", DIALECT.getTypeName(SqlTypes.UUID));
	}
	
	@Test
	void jsonTypeSupportedViaRegistry() throws SqlException {
		assertEquals("JSON", DIALECT.getTypeName(SqlTypes.JSON));
	}
	
	@Test
	void inheritsBacktickQuotingFromMySql() {
		assertEquals("`col`", DIALECT.quoteIdentifier("col"));
	}
	
	@Test
	void featureSetDiffersFromMySqlForReturningAndSchemas() {
		MySqlDialect mySql = new MySqlDialect();
		assertTrue(DIALECT.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(mySql.isFeatureSupported(SqlFeature.RETURNING));
		assertFalse(DIALECT.isFeatureSupported(SqlFeature.SCHEMAS));
		assertTrue(mySql.isFeatureSupported(SqlFeature.SCHEMAS));
	}
}
