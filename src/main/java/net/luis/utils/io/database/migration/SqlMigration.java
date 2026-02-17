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

import net.luis.utils.io.database.SqlRenderable;
import net.luis.utils.io.database.dialect.SqlDialect;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;

/**
 * Interface representing a SQL migration.<br>
 *
 * @author Luis-St
 */
public interface SqlMigration extends SqlRenderable {
	
	/**
	 * Computes the diff between the current and target schema.<br>
	 *
	 * @param currentSchema The current schema
	 * @param targetSchema The target schema
	 * @return The schema diff
	 */
	static @NonNull SqlSchemaDiff diff(@NonNull SqlSchema currentSchema, @NonNull SqlSchema targetSchema) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the list of pending migrations that have not yet been applied.<br>
	 *
	 * @param dataSource The data source to check against
	 * @param migrationsDir The directory containing migration files
	 * @return The list of pending migrations
	 */
	static @NonNull List<SqlMigration> pending(@NonNull DataSource dataSource, @NonNull File migrationsDir) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the version of this migration.<br>
	 * @return The migration version
	 */
	@NonNull String version();
	
	/**
	 * Returns the description of this migration.<br>
	 * @return The migration description
	 */
	@NonNull String description();
	
	@Override
	@NonNull String toSql(@NonNull SqlDialect<?, ?> dialect);
}
