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

import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationInfo}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationInfoTest {
	
	@Test
	void constructWithAllFields() {
		Version version = Version.of(1, 0, 0);
		Instant appliedAt = Instant.now();
		SqlMigrationInfo info = new SqlMigrationInfo(version, "init", SqlMigrationStatus.APPLIED, appliedAt, "abc");
		assertSame(version, info.version());
		assertEquals("init", info.description());
		assertEquals(SqlMigrationStatus.APPLIED, info.status());
		assertSame(appliedAt, info.appliedAt());
		assertEquals("abc", info.checksum());
	}
	
	@Test
	void constructWithNullableFieldsNull() {
		Version version = Version.of(1, 0, 0);
		SqlMigrationInfo info = assertDoesNotThrow(() -> new SqlMigrationInfo(version, "desc", SqlMigrationStatus.PENDING, null, null));
		assertNull(info.appliedAt());
		assertNull(info.checksum());
	}
	
	@Test
	void constructWithNullVersion() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationInfo(null, "desc", SqlMigrationStatus.PENDING, null, null));
	}
	
	@Test
	void constructWithNullDescription() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationInfo(Version.of(1, 0, 0), null, SqlMigrationStatus.PENDING, null, null));
	}
	
	@Test
	void constructWithNullStatus() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationInfo(Version.of(1, 0, 0), "desc", null, null, null));
	}
	
	@Test
	void equalInstancesAreEqual() {
		Version version = Version.of(1, 0, 0);
		Instant appliedAt = Instant.now();
		SqlMigrationInfo first = new SqlMigrationInfo(version, "init", SqlMigrationStatus.APPLIED, appliedAt, "abc");
		SqlMigrationInfo second = new SqlMigrationInfo(version, "init", SqlMigrationStatus.APPLIED, appliedAt, "abc");
		SqlMigrationInfo different = new SqlMigrationInfo(version, "init", SqlMigrationStatus.APPLIED, appliedAt, "def");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
