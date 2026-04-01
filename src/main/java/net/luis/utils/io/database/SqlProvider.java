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

package net.luis.utils.io.database;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlProvider {
	
	void createSchema(@NotNull String name) throws SqlException;
	
	void createSchemaIfNotExists(@NotNull String name) throws SqlException;
	
	boolean existsSchema(@NotNull String name) throws SqlException;
	
	void dropSchema(@NotNull String name, boolean cascade) throws SqlException;
	
	<T> @NonNull SqlTableProvider<T> table(@NonNull SqlTable<T> table);
	
	<T> @NonNull SqlQueryProvider<T> from(@NonNull SqlTable<T> table);
}
