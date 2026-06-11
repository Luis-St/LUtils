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

package net.luis.utils.io.database.audit;

import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditConfig}.<br>
 *
 * @author Luis-St
 */
class SqlAuditConfigTest {
	
	private static final SqlType<Long> VERSION_TYPE = SqlTypes.LONG;
	private static final SqlType<LocalDateTime> TIMESTAMP_TYPE = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6));
	private static final SqlType<String> USER_TYPE = SqlTypes.STRING.configure(SqlParameter.length(255));
	private static final Clock CLOCK = Clock.systemUTC();
	
	@Test
	void constructValidConfig() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		assertEquals("version", config.versionColumn());
		assertEquals("updated_by", config.updatedByColumn());
		assertEquals(VERSION_TYPE, config.versionType());
		assertEquals(SqlAuditValueSource.ORM_CLOCK, config.valueSource());
		assertEquals(CLOCK, config.clock());
	}
	
	@Test
	void constructWithNullVersionColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig(null, "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullCreatedAtColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", null, "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullCreatedByColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", null, "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullUpdatedAtColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", null, "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullUpdatedByColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", null, VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullVersionType() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", null, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullTimestampType() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, null, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullUserType() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, null, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithNullValueSource() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, null, CLOCK));
	}
	
	@Test
	void constructWithNullClock() {
		assertThrows(NullPointerException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, null));
	}
	
	@Test
	void constructWithBlankColumnName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAuditConfig("   ", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithBlankLastColumnName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructWithDuplicateColumnNames() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAuditConfig("dup", "dup", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructAllNamesNonBlankPasses() {
		assertDoesNotThrow(() -> new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK));
	}
	
	@Test
	void constructDistinctNamesPasses() {
		SqlAuditConfig config = new SqlAuditConfig("v", "ca", "cb", "ua", "ub", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		assertEquals(List.of("v", "ca", "cb", "ua", "ub"), config.columnNames());
	}
	
	@Test
	void builderReturnsFreshBuilder() {
		assertNotNull(SqlAuditConfig.builder());
		assertNotSame(SqlAuditConfig.builder(), SqlAuditConfig.builder());
	}
	
	@Test
	void columnNamesReturnsNamesInOrder() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		assertEquals(List.of("version", "created_at", "created_by", "updated_at", "updated_by"), config.columnNames());
	}
	
	@Test
	void auditColumnsReturnsFiveColumns() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		assertEquals(5, config.auditColumns().size());
	}
	
	@Test
	void defaultConfigHasExpectedColumnNames() {
		assertEquals(List.of("version", "created_at", "created_by", "updated_at", "updated_by"), SqlAuditConfig.DEFAULT.columnNames());
		assertEquals(SqlAuditValueSource.ORM_CLOCK, SqlAuditConfig.DEFAULT.valueSource());
	}
	
	@Test
	void auditColumnsMapRolesAndNullability() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		List<SqlAuditColumn> columns = config.auditColumns();
		assertEquals(new SqlAuditColumn("version", VERSION_TYPE, SqlAuditRole.VERSION, false), columns.get(0));
		assertEquals(new SqlAuditColumn("created_at", TIMESTAMP_TYPE, SqlAuditRole.CREATED_AT, false), columns.get(1));
		assertEquals(new SqlAuditColumn("created_by", USER_TYPE, SqlAuditRole.CREATED_BY, true), columns.get(2));
		assertEquals(new SqlAuditColumn("updated_at", TIMESTAMP_TYPE, SqlAuditRole.UPDATED_AT, true), columns.get(3));
		assertEquals(new SqlAuditColumn("updated_by", USER_TYPE, SqlAuditRole.UPDATED_BY, true), columns.get(4));
	}
	
	@Test
	void columnNamesMatchAuditColumnNames() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		List<String> auditColumnNames = config.auditColumns().stream().map(SqlAuditColumn::name).toList();
		assertEquals(config.columnNames(), auditColumnNames);
	}
	
	@Test
	void auditColumnsListIsUnmodifiable() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		SqlAuditColumn extra = new SqlAuditColumn("extra", VERSION_TYPE, SqlAuditRole.VERSION, false);
		assertThrows(UnsupportedOperationException.class, () -> config.auditColumns().add(extra));
	}
	
	@Test
	void columnNamesListIsUnmodifiable() {
		SqlAuditConfig config = new SqlAuditConfig("version", "created_at", "created_by", "updated_at", "updated_by", VERSION_TYPE, TIMESTAMP_TYPE, USER_TYPE, SqlAuditValueSource.ORM_CLOCK, CLOCK);
		assertThrows(UnsupportedOperationException.class, () -> config.columnNames().add("extra"));
	}
	
	@Test
	void customConfigRoundTripsThroughBuilder() {
		SqlAuditConfig config = SqlAuditConfig.builder()
			.versionColumn("v")
			.createdAtColumn("ca")
			.createdByColumn("cb")
			.updatedAtColumn("ua")
			.updatedByColumn("ub")
			.versionType(VERSION_TYPE)
			.build();
		assertEquals(List.of("v", "ca", "cb", "ua", "ub"), config.columnNames());
		assertEquals(VERSION_TYPE, config.auditColumns().get(0).type());
	}
}
