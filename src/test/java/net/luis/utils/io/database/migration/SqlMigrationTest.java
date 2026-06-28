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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigration}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationTest {
	
	@Test
	void allowsNonAtomicExecutionDefaultsToFalse() {
		SqlMigration migration = new NoOpMigration();
		assertFalse(migration.allowsNonAtomicExecution());
	}
	
	@Test
	void allowsNonAtomicExecutionCanBeOverridden() {
		SqlMigration migration = new SqlMigration() {
			
			@Override
			public @NonNull Version version() {
				return Version.of(1, 0, 0);
			}
			
			@Override
			public @NonNull String description() {
				return "non-atomic";
			}
			
			@Override
			public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {}
			
			@Override
			public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {}
			
			@Override
			public boolean allowsNonAtomicExecution() {
				return true;
			}
		};
		assertTrue(migration.allowsNonAtomicExecution());
	}
	
	private static final class NoOpMigration implements SqlMigration {
		
		@Override
		public @NonNull Version version() {
			return Version.of(1, 0, 0);
		}
		
		@Override
		public @NonNull String description() {
			return "no-op";
		}
		
		@Override
		public void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {}
		
		@Override
		public void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) {}
	}
}
