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

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlAuditConfigBuilderTest {
	
	@Test
	void constructBuilderHasDefaults() {
		SqlAuditConfig config = SqlAuditConfig.builder().build();
		assertEquals("version", config.versionColumn());
		assertEquals("created_at", config.createdAtColumn());
		assertEquals("created_by", config.createdByColumn());
		assertEquals("updated_at", config.updatedAtColumn());
		assertEquals("updated_by", config.updatedByColumn());
		assertEquals(SqlAuditValueSource.ORM_CLOCK, config.valueSource());
	}
	
	@Test
	void versionColumnWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().versionColumn(null));
	}
	
	@Test
	void createdAtColumnWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().createdAtColumn(null));
	}
	
	@Test
	void createdByColumnWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().createdByColumn(null));
	}
	
	@Test
	void updatedAtColumnWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().updatedAtColumn(null));
	}
	
	@Test
	void updatedByColumnWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().updatedByColumn(null));
	}
	
	@Test
	void versionTypeWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().versionType(null));
	}
	
	@Test
	void timestampTypeWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().timestampType(null));
	}
	
	@Test
	void userTypeWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().userType(null));
	}
	
	@Test
	void valueSourceWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().valueSource(null));
	}
	
	@Test
	void clockWithNull() {
		assertThrows(NullPointerException.class, () -> SqlAuditConfig.builder().clock(null));
	}
	
	@Test
	void versionColumnReturnsSameBuilder() {
		SqlAuditConfigBuilder builder = SqlAuditConfig.builder();
		assertSame(builder, builder.versionColumn("v"));
	}
	
	@Test
	void settersReturnSameBuilder() {
		SqlAuditConfigBuilder builder = SqlAuditConfig.builder();
		assertSame(builder, builder.createdAtColumn("ca"));
		assertSame(builder, builder.createdByColumn("cb"));
		assertSame(builder, builder.updatedAtColumn("ua"));
		assertSame(builder, builder.updatedByColumn("ub"));
		assertSame(builder, builder.versionType(SqlTypes.LONG));
		assertSame(builder, builder.timestampType(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(3))));
		assertSame(builder, builder.userType(SqlTypes.STRING.configure(SqlParameter.length(100))));
		assertSame(builder, builder.valueSource(SqlAuditValueSource.DATABASE));
		assertSame(builder, builder.clock(Clock.systemUTC()));
	}
	
	@Test
	void versionColumnSetsValue() {
		SqlAuditConfig config = SqlAuditConfig.builder().versionColumn("ver").build();
		assertEquals("ver", config.versionColumn());
	}
	
	@Test
	void buildAppliesCustomColumnNames() {
		SqlAuditConfig config = SqlAuditConfig.builder()
			.versionColumn("v")
			.createdAtColumn("ca")
			.createdByColumn("cb")
			.updatedAtColumn("ua")
			.updatedByColumn("ub")
			.build();
		assertEquals("v", config.versionColumn());
		assertEquals("ca", config.createdAtColumn());
		assertEquals("cb", config.createdByColumn());
		assertEquals("ua", config.updatedAtColumn());
		assertEquals("ub", config.updatedByColumn());
	}
	
	@Test
	void buildAppliesCustomTypes() {
		SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(3));
		SqlType<String> userType = SqlTypes.STRING.configure(SqlParameter.length(100));
		SqlAuditConfig config = SqlAuditConfig.builder()
			.versionType(SqlTypes.LONG)
			.timestampType(timestampType)
			.userType(userType)
			.build();
		assertEquals(SqlTypes.LONG, config.versionType());
		assertEquals(timestampType, config.timestampType());
		assertEquals(userType, config.userType());
	}
	
	@Test
	void buildAppliesValueSourceAndClock() {
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC);
		SqlAuditConfig config = SqlAuditConfig.builder().valueSource(SqlAuditValueSource.DATABASE).clock(clock).build();
		assertEquals(SqlAuditValueSource.DATABASE, config.valueSource());
		assertEquals(clock, config.clock());
	}
	
	@Test
	void fullChainingConfiguresAllFields() {
		SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(3));
		SqlType<String> userType = SqlTypes.STRING.configure(SqlParameter.length(100));
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC);
		SqlAuditConfig config = SqlAuditConfig.builder()
			.versionColumn("v")
			.createdAtColumn("ca")
			.createdByColumn("cb")
			.updatedAtColumn("ua")
			.updatedByColumn("ub")
			.versionType(SqlTypes.LONG)
			.timestampType(timestampType)
			.userType(userType)
			.valueSource(SqlAuditValueSource.DATABASE)
			.clock(clock)
			.build();
		assertEquals("v", config.versionColumn());
		assertEquals("ub", config.updatedByColumn());
		assertEquals(SqlTypes.LONG, config.versionType());
		assertEquals(timestampType, config.timestampType());
		assertEquals(userType, config.userType());
		assertEquals(SqlAuditValueSource.DATABASE, config.valueSource());
		assertEquals(clock, config.clock());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SqlAuditConfigBuilder builder = SqlAuditConfig.builder();
		SqlAuditConfig first = builder.build();
		assertEquals("version", first.versionColumn());
		
		builder.versionColumn("changed");
		SqlAuditConfig second = builder.build();
		assertEquals("changed", second.versionColumn());
		assertEquals("version", first.versionColumn());
	}
	
	@Test
	void methodChainingConsistency() {
		SqlAuditConfigBuilder builder = SqlAuditConfig.builder();
		SqlAuditConfigBuilder returned = builder.versionColumn("v").createdAtColumn("ca").updatedByColumn("ub");
		assertSame(builder, returned);
	}
}
