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

package net.luis.utils.io.database.dialect.oracle;

import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing an Oracle-specific table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface OracleTable<T> extends SqlTable<T> {
	
	void setTablespace(@NonNull String tablespace);
	
	void enableRowMovement();
	
	void partitionByRange(@NonNull SqlColumn<?> column);
	
	void partitionByList(@NonNull SqlColumn<?> column);
	
	void partitionByHash(@NonNull SqlColumn<?> column, int partitions);
	
	void enableFlashback();
	
	void setCompression(@NonNull OracleCompression compression);
	
	void createMaterializedView(@NonNull String name, @NonNull String query);
}
