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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.SqlReferentialAction;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlForeignKey<T>(
	@NonNull SqlTable<T> referencedTable,
	@NonNull @Unmodifiable List<SqlColumn<T, ?>> referencedColumns,
	@NonNull SqlReferentialAction onUpdate,
	@NonNull SqlReferentialAction onDelete
) {
	
	public SqlForeignKey {
		Objects.requireNonNull(referencedTable, "Sql referenced table must not be null");
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		Objects.requireNonNull(onUpdate, "On sql update action must not be null");
		Objects.requireNonNull(onDelete, "On sql delete action must not be null");
		
		if (referencedColumns.isEmpty()) {
			throw new IllegalArgumentException("Sql referenced columns must not be empty");
		}
		
		referencedColumns = List.copyOf(referencedColumns);
		
		for (SqlColumn<?, ?> column : referencedColumns) {
			if (!column.owningTable().equals(referencedTable)) {
				throw new IllegalArgumentException("Sql referenced column " + column.name() + " does not belong to the referenced table '" + referencedTable.name() + "'");
			}
		}
	}
	
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable) {
		Objects.requireNonNull(referencedTable, "Sql referenced table must not be null");
		
		return new SqlForeignKey<>(referencedTable, referencedTable.primaryKeyColumns(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		return new SqlForeignKey<>(referencedTable, List.of(referencedColumn), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete) {
		return new SqlForeignKey<>(referencedTable, referencedColumns, onUpdate, onDelete);
	}
}
