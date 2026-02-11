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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresTable<T> extends SqlTable<T> {
	
	@Override
	@NonNull PostgresSelectQuery<T> select();
	
	void setUnlogged(boolean unlogged);
	
	void setTablespace(@NonNull String tablespace);
	
	void partitionByRange(SqlColumn<?> @NonNull ... columns);
	
	void partitionByList(@NonNull SqlColumn<?> column);
	
	void partitionByHash(SqlColumn<?> @NonNull ... columns);
	
	void enableRowLevelSecurity();
}
