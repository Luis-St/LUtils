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
 * Represents a foreign key reference to columns of another sql table.<br>
 * The foreign key defines which table and columns are referenced as well as the referential actions<br>
 * that are applied when the referenced rows are updated or deleted.<br>
 *
 * @see SqlTable
 * @see SqlColumn
 * @see SqlReferentialAction
 * @see SqlTableForeignKey
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity the referenced table maps to
 */
public record SqlForeignKey<T>(
	@NonNull SqlTable<T> referencedTable,
	@NonNull @Unmodifiable List<SqlColumn<T, ?>> referencedColumns,
	@NonNull SqlReferentialAction onUpdate,
	@NonNull SqlReferentialAction onDelete
) {
	
	/**
	 * Constructs a new foreign key with the given referenced table, columns and referential actions.<br>
	 * The referenced columns are copied into an unmodifiable list.<br>
	 *
	 * @param referencedTable The table that is referenced by this foreign key
	 * @param referencedColumns The columns of the referenced table that are referenced
	 * @param onUpdate The action to apply when a referenced row is updated
	 * @param onDelete The action to apply when a referenced row is deleted
	 * @throws NullPointerException If the referenced table, the referenced columns, the update action or the delete action is null
	 * @throws IllegalArgumentException If the referenced columns are empty or contain a column that does not belong to the referenced table
	 */
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
	
	/**
	 * Creates a foreign key that references the primary key columns of the given table.<br>
	 * Both the update and delete action are set to {@link SqlReferentialAction#NO_ACTION}.<br>
	 *
	 * @param referencedTable The table that is referenced by this foreign key
	 * @return The created foreign key
	 * @param <T> The type of the entity the referenced table maps to
	 * @throws NullPointerException If the referenced table is null
	 * @throws IllegalArgumentException If the referenced table has no primary key columns
	 */
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable) {
		Objects.requireNonNull(referencedTable, "Sql referenced table must not be null");
		
		return new SqlForeignKey<>(referencedTable, referencedTable.primaryKeyColumns(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	/**
	 * Creates a foreign key that references the given column of the given table.<br>
	 * Both the update and delete action are set to {@link SqlReferentialAction#NO_ACTION}.<br>
	 *
	 * @param referencedTable The table that is referenced by this foreign key
	 * @param referencedColumn The column of the referenced table that is referenced
	 * @return The created foreign key
	 * @param <T> The type of the entity the referenced table maps to
	 * @throws NullPointerException If the referenced table or the referenced column is null
	 * @throws IllegalArgumentException If the referenced column does not belong to the referenced table
	 */
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		return new SqlForeignKey<>(referencedTable, List.of(referencedColumn), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	/**
	 * Creates a foreign key that references the given columns of the given table using the given referential actions.<br>
	 *
	 * @param referencedTable The table that is referenced by this foreign key
	 * @param referencedColumns The columns of the referenced table that are referenced
	 * @param onUpdate The action to apply when a referenced row is updated
	 * @param onDelete The action to apply when a referenced row is deleted
	 * @return The created foreign key
	 * @param <T> The type of the entity the referenced table maps to
	 * @throws NullPointerException If the referenced table, the referenced columns, the update action or the delete action is null
	 * @throws IllegalArgumentException If the referenced columns are empty or contain a column that does not belong to the referenced table
	 */
	public static <T> @NonNull SqlForeignKey<T> of(@NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete) {
		return new SqlForeignKey<>(referencedTable, referencedColumns, onUpdate, onDelete);
	}
}
