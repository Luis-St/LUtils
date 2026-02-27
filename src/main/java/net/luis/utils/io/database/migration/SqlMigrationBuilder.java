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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlColumnType;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.table.*;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * DSL interface for defining migration operations.<br>
 * <p>
 *     Provides a fluent API for creating, dropping, and renaming tables, adding, dropping, renaming, and altering columns,
 *     managing indexes, constraints, and sequences.<br>
 *     No raw SQL is allowed; all schema modifications go through this builder.
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigrationBuilder {

	/**
	 * Creates a new table with the specified reference and definition.<br>
	 *
	 * @param table The table reference
	 * @param definition A consumer to define the table structure
	 * @throws SqlException If the table creation fails
	 */
	void createTable(@NonNull SqlTable<?> table, @NonNull Consumer<SqlTableBuilder> definition) throws SqlException;

	/**
	 * Drops the specified table.<br>
	 *
	 * @param table The table reference
	 * @throws SqlException If the table drop fails
	 */
	void dropTable(@NonNull SqlTable<?> table) throws SqlException;

	/**
	 * Renames a table.<br>
	 *
	 * @param from The current table reference
	 * @param to The new table reference
	 * @throws SqlException If the table rename fails
	 */
	void renameTable(@NonNull SqlTable<?> from, @NonNull SqlTable<?> to) throws SqlException;

	/**
	 * Adds a column to its owning table.<br>
	 *
	 * @param column The column reference (carries the target table via {@code column.getTable()})
	 * @param type The column type
	 * @throws SqlException If the column addition fails
	 */
	void addColumn(@NonNull SqlColumn<?> column, @NonNull SqlColumnType type) throws SqlException;

	/**
	 * Adds a column with additional options to its owning table.<br>
	 *
	 * @param column The column reference (carries the target table via {@code column.getTable()})
	 * @param type The column type
	 * @param options A consumer to configure column options
	 * @param <V> The column value type
	 * @throws SqlException If the column addition fails
	 */
	<V> void addColumn(@NonNull SqlColumn<V> column, @NonNull SqlColumnType type, @NonNull Consumer<SqlColumnBuilder<V>> options) throws SqlException;

	/**
	 * Drops a column from its owning table.<br>
	 *
	 * @param column The column reference (carries the target table via {@code column.getTable()})
	 * @throws SqlException If the column drop fails
	 */
	void dropColumn(@NonNull SqlColumn<?> column) throws SqlException;

	/**
	 * Renames a column in its owning table.<br>
	 *
	 * @param from The current column reference
	 * @param to The new column reference
	 * @throws SqlException If the column rename fails
	 */
	void renameColumn(@NonNull SqlColumn<?> from, @NonNull SqlColumn<?> to) throws SqlException;

	/**
	 * Alters a column in its owning table.<br>
	 *
	 * @param column The column reference (carries the target table via {@code column.getTable()})
	 * @param changes A consumer to define the column alterations
	 * @param <V> The column value type
	 * @throws SqlException If the column alteration fails
	 */
	<V> void alterColumn(@NonNull SqlColumn<V> column, @NonNull Consumer<SqlColumnAlter<V>> changes) throws SqlException;

	/**
	 * Creates an index on the specified table.<br>
	 *
	 * @param table The table reference
	 * @param name The index name
	 * @param definition A consumer to define the index
	 * @throws SqlException If the index creation fails
	 */
	void createIndex(@NonNull SqlTable<?> table, @NonNull String name, @NonNull Consumer<SqlIndexBuilder> definition) throws SqlException;

	/**
	 * Drops the index with the specified name.<br>
	 *
	 * @param name The index name
	 * @throws SqlException If the index drop fails
	 */
	void dropIndex(@NonNull String name) throws SqlException;

	/**
	 * Adds a unique constraint to the specified table.<br>
	 *
	 * @param table The table reference
	 * @param name The constraint name
	 * @param columns The columns forming the unique constraint
	 * @throws SqlException If the constraint addition fails
	 */
	void addUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String name, SqlColumn<?> @NonNull ... columns) throws SqlException;

	/**
	 * Adds a foreign key constraint.<br>
	 * The source table, referenced table, and referenced column are all derived from the {@link SqlForeignColumn}.<br>
	 *
	 * @param column The foreign key column (carries the source table, referenced table, and referenced column)
	 * @param name The constraint name
	 * @throws SqlException If the foreign key addition fails
	 */
	void addForeignKey(@NonNull SqlForeignColumn<?, ?> column, @NonNull String name) throws SqlException;

	/**
	 * Adds a composite foreign key constraint to the specified table.<br>
	 * Generates SQL: {@code ALTER TABLE table ADD CONSTRAINT name FOREIGN KEY (col1, col2, ...) REFERENCES refTable(refCol1, refCol2, ...)}.<br>
	 *
	 * @param table The table to add the constraint to
	 * @param name The constraint name
	 * @param columns The columns in the source table forming the foreign key
	 * @param referencedTable The referenced table
	 * @param referencedColumns The referenced columns in the target table
	 * @param onDelete The action to perform when a referenced row is deleted
	 * @param onUpdate The action to perform when a referenced row is updated
	 * @throws SqlException If the composite foreign key addition fails
	 */
	void addCompositeForeignKey(
		@NonNull SqlTable<?> table,
		@NonNull String name,
		@NonNull SqlColumn<?> @NonNull [] columns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull SqlColumn<?> @NonNull [] referencedColumns,
		@NonNull SqlForeignKeyAction onDelete,
		@NonNull SqlForeignKeyAction onUpdate
	) throws SqlException;

	/**
	 * Adds a check constraint to the specified table.<br>
	 *
	 * @param table The table reference
	 * @param name The constraint name
	 * @param condition The check condition
	 * @throws SqlException If the check constraint addition fails
	 */
	void addCheckConstraint(@NonNull SqlTable<?> table, @NonNull String name, @NonNull SqlCondition condition) throws SqlException;

	/**
	 * Drops a constraint from the specified table.<br>
	 *
	 * @param table The table reference
	 * @param name The constraint name
	 * @throws SqlException If the constraint drop fails
	 */
	void dropConstraint(@NonNull SqlTable<?> table, @NonNull String name) throws SqlException;

	/**
	 * Creates a new sequence with the specified name and definition.<br>
	 *
	 * @param name The sequence name
	 * @param definition A consumer to define the sequence
	 * @throws SqlException If the sequence creation fails
	 */
	void createSequence(@NonNull String name, @NonNull Consumer<SqlSequenceBuilder> definition) throws SqlException;

	/**
	 * Drops the sequence with the specified name.<br>
	 *
	 * @param name The sequence name
	 * @throws SqlException If the sequence drop fails
	 */
	void dropSequence(@NonNull String name) throws SqlException;

	/**
	 * Alters the sequence with the specified name.<br>
	 *
	 * @param name The sequence name
	 * @param changes A consumer to define the sequence alterations
	 * @throws SqlException If the sequence alteration fails
	 */
	void alterSequence(@NonNull String name, @NonNull Consumer<SqlSequenceAlter> changes) throws SqlException;
}
