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

package net.luis.utils.io.database.migration.store;

import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationStore}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationStoreTest {
	
	@Test
	void saveWithConnectionDelegatesToSaveInfo() {
		RecordingStore store = new RecordingStore();
		SqlMigrationInfo info = new SqlMigrationInfo(Version.of(1, 0, 0), "init", SqlMigrationStatus.APPLIED, null, null);
		assertDoesNotThrow(() -> store.save(null, info));
		assertSame(info, store.savedInfo);
	}
	
	@Test
	void updateWithConnectionDelegatesToUpdate() {
		RecordingStore store = new RecordingStore();
		Version version = Version.of(2, 0, 0);
		assertDoesNotThrow(() -> store.update(null, version, SqlMigrationStatus.ROLLED_BACK));
		assertSame(version, store.updatedVersion);
		assertEquals(SqlMigrationStatus.ROLLED_BACK, store.updatedStatus);
	}
	
	@Test
	void defaultSaveUsesValueFromOverriddenSaveInfo() {
		RecordingStore store = new RecordingStore();
		SqlMigrationInfo info = new SqlMigrationInfo(Version.of(1, 0, 0), "init", SqlMigrationStatus.PENDING, null, null);
		assertDoesNotThrow(() -> store.save(null, info));
		assertEquals(1, store.saveCount.get());
	}
	
	private static final class RecordingStore implements SqlMigrationStore {
		
		private final AtomicInteger saveCount = new AtomicInteger(0);
		private SqlMigrationInfo savedInfo;
		private Version updatedVersion;
		private SqlMigrationStatus updatedStatus;
		
		@Override
		public void initialize() {}
		
		@Override
		public @NonNull List<SqlMigrationInfo> loadAll() {
			return List.of();
		}
		
		@Override
		public void save(@NonNull SqlMigrationInfo info) {
			this.savedInfo = info;
			this.saveCount.incrementAndGet();
		}
		
		@Override
		public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) {
			this.updatedVersion = version;
			this.updatedStatus = status;
		}
	}
}
