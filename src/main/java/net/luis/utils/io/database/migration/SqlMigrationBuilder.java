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
 *     Provides a fluent API for creating, dropping, and renaming tables,
 *     adding, dropping, renaming, and altering columns, managing indexes,
 *     constraints, and sequences.<br>
 *     No raw SQL is allowed; all schema modifications go through this builder.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigrationBuilder {
	
	/**
	 * Creates a new table with the specified name and definition.<br>
	 *
	 * @param name The table name
	 * @param definition A consumer to define the table structure
	 * @throws SqlException If the table creation fails
	 */
	void createTable(@NonNull String name, @NonNull Consumer<SqlTableBuilder> definition) throws SqlException;
	
	/**
	 * Drops the table with the specified name.<br>
	 *
	 * @param name The table name
	 * @throws SqlException If the table drop fails
	 */
	void dropTable(@NonNull String name) throws SqlException;
	
	/**
	 * Renames a table.<br>
	 *
	 * @param oldName The current table name
	 * @param newName The new table name
	 * @throws SqlException If the table rename fails
	 */
	void renameTable(@NonNull String oldName, @NonNull String newName) throws SqlException;
	
	/**
	 * Adds a column to the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The column name
	 * @param type The column type
	 * @throws SqlException If the column addition fails
	 */
	void addColumn(@NonNull String table, @NonNull String name, @NonNull SqlColumnType type) throws SqlException;
	
	/**
	 * Adds a column with additional options to the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The column name
	 * @param type The column type
	 * @param options A consumer to configure column options
	 * @throws SqlException If the column addition fails
	 */
	void addColumn(@NonNull String table, @NonNull String name, @NonNull SqlColumnType type, @NonNull Consumer<SqlColumnBuilder> options) throws SqlException;
	
	/**
	 * Drops a column from the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The column name
	 * @throws SqlException If the column drop fails
	 */
	void dropColumn(@NonNull String table, @NonNull String name) throws SqlException;
	
	/**
	 * Renames a column in the specified table.<br>
	 *
	 * @param table The table name
	 * @param oldName The current column name
	 * @param newName The new column name
	 * @throws SqlException If the column rename fails
	 */
	void renameColumn(@NonNull String table, @NonNull String oldName, @NonNull String newName) throws SqlException;
	
	/**
	 * Alters a column in the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The column name
	 * @param changes A consumer to define the column alterations
	 * @throws SqlException If the column alteration fails
	 */
	void alterColumn(@NonNull String table, @NonNull String name, @NonNull Consumer<SqlColumnAlter> changes) throws SqlException;
	
	/**
	 * Creates an index on the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The index name
	 * @param definition A consumer to define the index
	 * @throws SqlException If the index creation fails
	 */
	void createIndex(@NonNull String table, @NonNull String name, @NonNull Consumer<SqlIndexBuilder> definition) throws SqlException;
	
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
	 * @param table The table name
	 * @param name The constraint name
	 * @param columns The column names forming the unique constraint
	 * @throws SqlException If the constraint addition fails
	 */
	void addUniqueConstraint(@NonNull String table, @NonNull String name, String @NonNull ... columns) throws SqlException;
	
	/**
	 * Adds a foreign key constraint to the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The constraint name
	 * @param column The column name in the source table
	 * @param refTable The referenced table name
	 * @param refColumn The referenced column name
	 * @throws SqlException If the foreign key addition fails
	 */
	void addForeignKey(@NonNull String table, @NonNull String name, @NonNull String column, @NonNull String refTable, @NonNull String refColumn) throws SqlException;
	
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
	 * @param table The table name
	 * @param name The constraint name
	 * @param condition The check condition
	 * @throws SqlException If the check constraint addition fails
	 */
	void addCheckConstraint(@NonNull String table, @NonNull String name, @NonNull SqlCondition condition) throws SqlException;
	
	/**
	 * Drops a constraint from the specified table.<br>
	 *
	 * @param table The table name
	 * @param name The constraint name
	 * @throws SqlException If the constraint drop fails
	 */
	void dropConstraint(@NonNull String table, @NonNull String name) throws SqlException;
	
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
