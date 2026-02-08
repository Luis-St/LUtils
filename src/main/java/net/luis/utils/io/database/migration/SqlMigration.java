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

import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;

/**
 * Interface representing a SQL migration.<br>
 *
 * @author Luis-St
 */
public interface SqlMigration {

	static @NonNull SqlSchemaDiff diff(@NonNull SqlSchema currentSchema, @NonNull SqlSchema targetSchema) {
		throw new UnsupportedOperationException();
	}

	static @NonNull List<SqlMigration> pending(@NonNull DataSource dataSource, @NonNull File migrationsDir) {
		throw new UnsupportedOperationException();
	}

	@NonNull String version();

	@NonNull String description();

	@NonNull String sql();
}
