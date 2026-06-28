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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Represents the definition of a relational database table that maps to a specific entity type.<br>
 * A table is identified by its name and schema and holds an ordered set of {@link SqlColumn columns}
 * together with its keys and constraints.<br>
 * <p>
 *     Tables are built up incrementally using the column, key and constraint factory methods.<br>
 *     Columns are added through {@link #column(String, SqlType, Function)}, primary keys, foreign keys,
 *     unique constraints and check constraints are registered through the corresponding factory methods.<br>
 *     A table may optionally be {@link #isAudited() audited}, in which case audit columns are reserved
 *     and cannot be used as regular column names.
 * </p>
 * <p>
 *     Equality and hash code are based on the value of the table definition (name, schema, columns,
 *     keys and constraints) rather than on object identity.<br>
 *     This is safe because tables are expected to be built once during static initialization and not
 *     mutated afterwards.
 * </p>
 *
 * @see SqlColumn
 * @see SqlCompositePrimaryKey
 * @see SqlTableForeignKey
 * @see SqlUniqueConstraint
 * @see SqlAuditConfig
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity this table maps to
 */
public class SqlTable<E> {
	
	/**
	 * The entity type this table maps to.
	 */
	private final Class<E> type;
	/**
	 * The name of the table.
	 */
	private final String name;
	/**
	 * The name of the schema the table belongs to.
	 */
	private final String schema;
	/**
	 * The columns of the table mapped by their name in insertion order.
	 */
	private final Map</*Name*/ String, SqlColumn<E, ?>> columns = Maps.newLinkedHashMap();
	/**
	 * The foreign keys defined on the table.
	 */
	private final List<SqlTableForeignKey<E, ?>> foreignKeys = Lists.newArrayList();
	/**
	 * The unique constraints defined on the table.
	 */
	private final List<SqlUniqueConstraint<E>> uniqueConstraints = Lists.newArrayList();
	/**
	 * The check constraints defined on the table.
	 */
	private final List<SqlCondition> checkConstraints = Lists.newArrayList();
	/**
	 * The counter used to assign a unique one-based index to each added column.
	 */
	private final AtomicInteger columnCounter = new AtomicInteger(1);
	/**
	 * The audit configuration of the table or {@code null} if the table is not audited.
	 */
	private final SqlAuditConfig auditConfig;
	/**
	 * The composite primary key of the table or an empty optional if none is defined.
	 */
	private Optional<SqlCompositePrimaryKey<E>> compositePrimaryKey = Optional.empty();
	
	/**
	 * Constructs a new sql table with the given type, name, schema and optional audit configuration.<br>
	 *
	 * @param type The entity type this table maps to
	 * @param name The name of the table
	 * @param schema The name of the schema the table belongs to
	 * @param auditConfig The audit configuration of the table or {@code null} if the table is not audited
	 * @throws NullPointerException If the type, name or schema is null
	 * @throws IllegalArgumentException If the name or schema is blank
	 */
	protected SqlTable(@NonNull Class<E> type, @NonNull String name, @NonNull String schema, @Nullable SqlAuditConfig auditConfig) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.name = Objects.requireNonNull(name, "Sql table name must not be null");
		this.schema = Objects.requireNonNull(schema, "Sql schema name must not be null");
		this.auditConfig = auditConfig;
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Sql table name must not be blank");
		}
		if (schema.isBlank()) {
			throw new IllegalArgumentException("Sql schema name must not be blank");
		}
	}
	
	//region Table factory methods
	
	/**
	 * Creates a new non-audited table for the given type and name in the default {@code public} schema.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @return The newly created table
	 * @throws NullPointerException If the type or name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public static <T> @NonNull SqlTable<T> create(@NonNull Class<T> type, @NonNull String name) {
		return create(type, name, "public");
	}
	
	/**
	 * Creates a new non-audited table for the given type, name and schema.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @param schema The name of the schema the table belongs to
	 * @return The newly created table
	 * @throws NullPointerException If the type, name or schema is null
	 * @throws IllegalArgumentException If the name or schema is blank
	 */
	public static <T> @NonNull SqlTable<T> create(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		return new SqlTable<>(type, name, schema, null);
	}
	
	/**
	 * Creates a new audited table for the given type and name in the default {@code public} schema.<br>
	 * The table uses the {@link SqlAuditConfig#DEFAULT default} audit configuration.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @return The newly created audited table
	 * @throws NullPointerException If the type or name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name) {
		return audited(type, name, "public", SqlAuditConfig.DEFAULT);
	}
	
	/**
	 * Creates a new audited table for the given type, name and schema.<br>
	 * The table uses the {@link SqlAuditConfig#DEFAULT default} audit configuration.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @param schema The name of the schema the table belongs to
	 * @return The newly created audited table
	 * @throws NullPointerException If the type, name or schema is null
	 * @throws IllegalArgumentException If the name or schema is blank
	 */
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		return audited(type, name, schema, SqlAuditConfig.DEFAULT);
	}
	
	/**
	 * Creates a new audited table for the given type and name in the default {@code public} schema using the given audit configuration.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @param config The audit configuration to use
	 * @return The newly created audited table
	 * @throws NullPointerException If the type, name or config is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull SqlAuditConfig config) {
		return audited(type, name, "public", config);
	}
	
	/**
	 * Creates a new audited table for the given type, name and schema using the given audit configuration.<br>
	 *
	 * @param type The entity type the table maps to
	 * @param name The name of the table
	 * @param schema The name of the schema the table belongs to
	 * @param config The audit configuration to use
	 * @return The newly created audited table
	 * @throws NullPointerException If the type, name, schema or config is null
	 * @throws IllegalArgumentException If the name or schema is blank
	 */
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull String schema, @NonNull SqlAuditConfig config) {
		Objects.requireNonNull(config, "Sql audit config must not be null");
		return new SqlTable<>(type, name, schema, config);
	}
	//endregion
	
	//region Helper methods
	
	/**
	 * Validates and registers the given composite primary key as the composite primary key of this table.<br>
	 * <p>
	 *     The reference is rejected if it contains no columns, references columns that are not defined in
	 *     this table, references columns that are not marked as primary keys, or if it does not include
	 *     every column that is marked as a primary key in this table.
	 * </p>
	 *
	 * @param reference The composite primary key to set
	 * @return The composite primary key that was set
	 * @throws NullPointerException If the reference or any of its columns is null
	 * @throws IllegalStateException If a composite primary key is already defined, the reference contains no columns, a referenced column is not defined in this table, a referenced column is not marked as a primary key, or a primary key column of this table is not included in the reference
	 */
	private @NonNull SqlCompositePrimaryKey<E> setCompositePrimaryKey(@NonNull SqlCompositePrimaryKey<E> reference) {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Sql composite primary key already defined for table '" + this.name + "', unable to set composite primary key '" + reference + "'");
		}
		
		List<SqlColumn<E, ?>> compositePrimaryKeyColumns = reference.columns();
		if (compositePrimaryKeyColumns.isEmpty()) {
			throw new IllegalStateException("Sql composite primary key defined in table '" + this.name + "' but it does not contain any columns");
		}
		
		for (SqlColumn<E, ?> column : compositePrimaryKeyColumns) {
			Objects.requireNonNull(column, "Sql column must not be null");
			
			if (!this.columns.containsKey(column.name())) {
				throw new IllegalStateException("Sql column '" + column.name() + "' is not defined in table '" + this.name + "'");
			}
			if (!column.primaryKey()) {
				throw new IllegalStateException("Sql column '" + column.name() + "' of the composite primary key is not marked as a primary key in table '" + this.name + "'");
			}
		}
		
		List<SqlColumn<E, ?>> primaryKeyColumns = this.columns.values().stream().filter(SqlColumn::primaryKey).toList();
		for (SqlColumn<E, ?> primaryKeyColumn : primaryKeyColumns) {
			if (compositePrimaryKeyColumns.stream().noneMatch(column -> column.name().equals(primaryKeyColumn.name()))) {
				throw new IllegalStateException("Sql primary key column '" + primaryKeyColumn.name() + "' is defined in table '" + this.name + "' but not included in the composite primary key");
			}
		}
		
		this.compositePrimaryKey = Optional.of(reference);
		return reference;
	}
	
	/**
	 * Validates and registers the given foreign key on this table.<br>
	 * Every referencing column of the foreign key must be defined in this table.<br>
	 *
	 * @param foreignKey The foreign key to add
	 * @return The foreign key that was added
	 * @throws NullPointerException If the foreign key or any of its referencing columns is null
	 * @throws IllegalArgumentException If a referencing column does not belong to this table
	 */
	private <T> @NonNull SqlTableForeignKey<E, T> addForeignKey(@NonNull SqlTableForeignKey<E, T> foreignKey) {
		for (SqlColumn<E, ?> column : foreignKey.getReferencingColumns()) {
			Objects.requireNonNull(column, "Sql referencing column must not be null");
			
			if (!this.columns.containsKey(column.name())) {
				throw new IllegalArgumentException("Sql referencing column '" + column.name() + "' does not belong to the table '" + this.name + "'");
			}
		}
		
		this.foreignKeys.add(foreignKey);
		return foreignKey;
	}
	//endregion
	
	//region Column factory methods
	
	/**
	 * Adds a new column with the given name, type and value getter to this table.<br>
	 * The column is configured with its default settings.<br>
	 *
	 * @param name The name of the column
	 * @param type The sql type of the column
	 * @param getter The function used to extract the column value from an entity
	 * @return The newly created and registered column
	 * @throws NullPointerException If the name, type or getter is null
	 * @throws IllegalStateException If a composite primary key is already defined, a column with the same name already exists, or the column name collides with a reserved audit column name
	 */
	public <C> @NonNull SqlColumn<E, C> column(@NonNull String name, @NonNull SqlType<C> type, @NonNull Function<E, C> getter) {
		return this.column(name, type, getter, UnaryOperator.identity());
	}
	
	/**
	 * Adds a new column with the given name, type and value getter to this table, configured through the given builder action.<br>
	 * The action receives a {@link SqlColumnBuilder} that can be used to configure additional column properties such as
	 * nullability, default value, uniqueness, primary key membership and check constraints.<br>
	 *
	 * @param name The name of the column
	 * @param type The sql type of the column
	 * @param getter The function used to extract the column value from an entity
	 * @param action The action used to configure the column builder
	 * @return The newly created and registered column
	 * @throws NullPointerException If the name, type, getter or action is null
	 * @throws IllegalStateException If a composite primary key is already defined, a column with the same name already exists, or the column name collides with a reserved audit column name
	 */
	public <C> @NonNull SqlColumn<E, C> column(@NonNull String name, @NonNull SqlType<C> type, @NonNull Function<E, C> getter, @NonNull UnaryOperator<SqlColumnBuilder<E, C>> action) {
		Objects.requireNonNull(action, "Sql column configuration action must not be null");
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException(
				"Unable column '" + name + "' to table '" + this.name + "' because a composite primary key is defined, after a composite primary key is defined, no additional columns can be added to the table"
			);
		}
		
		SqlColumn<E, C> column = action.apply(new SqlColumnBuilder<>(this, name, this.columnCounter.getAndIncrement(), type, getter)).build();
		if (this.columns.containsKey(column.name())) {
			throw new IllegalStateException("Sql column with name '" + name + "' already exists in table '" + this.name + "'");
		}
		if (this.auditConfig != null && this.auditConfig.columnNames().contains(column.name())) {
			throw new IllegalStateException("Sql column name '" + column.name() + "' in table '" + this.name + "' collides with a reserved audit column name");
		}
		
		this.columns.put(column.name(), column);
		return column;
	}
	//endregion
	
	//region Key factory methods
	
	/**
	 * Defines the composite primary key of this table from the given columns.<br>
	 * All columns must already be defined in this table and marked as primary keys.<br>
	 *
	 * @param first The first column of the composite primary key
	 * @param second The second column of the composite primary key
	 * @param others The additional columns of the composite primary key
	 * @return The newly defined composite primary key
	 * @throws NullPointerException If the first or second column, the others array or any of its elements is null
	 * @throws IllegalStateException If a composite primary key is already defined, a column is not defined in this table, a column is not marked as a primary key, or a primary key column of this table is not included
	 */
	@SafeVarargs
	public final @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull SqlColumn<E, ?> first, @NonNull SqlColumn<E, ?> second, SqlColumn<E, ?> @NonNull ... others) {
		Objects.requireNonNull(first, "First sql column of the composite primary key must not be null");
		Objects.requireNonNull(second, "Second sql column of the composite primary key must not be null");
		Objects.requireNonNull(others, "Other columns of the composite primary key must not be null");
		
		List<SqlColumn<E, ?>> columns = Lists.newArrayList(first, second);
		for (SqlColumn<E, ?> column : others) {
			columns.add(Objects.requireNonNull(column, "Other columns of the composite primary key must not contain null values"));
		}
		return this.compositePrimaryKey(Lists.newArrayList(columns));
	}
	
	/**
	 * Defines the composite primary key of this table from the given list of columns.<br>
	 * All columns must already be defined in this table and marked as primary keys.<br>
	 *
	 * @param columns The columns that make up the composite primary key
	 * @return The newly defined composite primary key
	 * @throws NullPointerException If the columns list or any of its columns is null
	 * @throws IllegalArgumentException If the columns list contains less than 2 columns
	 * @throws IllegalStateException If a composite primary key is already defined, a column is not defined in this table, a column is not marked as a primary key, or a primary key column of this table is not included
	 */
	public @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull List<SqlColumn<E, ?>> columns) {
		return this.setCompositePrimaryKey(new SqlCompositePrimaryKey<>(columns));
	}
	
	/**
	 * Generates the composite primary key of this table from all columns that are marked as primary keys.<br>
	 *
	 * @return The newly generated composite primary key
	 * @throws IllegalStateException If a composite primary key is already defined or no primary key columns are defined in this table
	 */
	public @NonNull SqlCompositePrimaryKey<E> generateCompositePrimaryKey() {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Unable to generate composite primary key for table '" + this.name + "' because a composite primary key is already defined");
		}
		
		List<SqlColumn<E, ?>> columns = Lists.newArrayList();
		for (SqlColumn<E, ?> column : this.columns.values()) {
			if (column.primaryKey()) {
				columns.add(column);
			}
		}
		
		if (columns.isEmpty()) {
			throw new IllegalStateException("Unable to generate composite primary key for table '" + this.name + "' because no primary key columns are defined");
		}
		return this.setCompositePrimaryKey(new SqlCompositePrimaryKey<>(columns));
	}
	
	/**
	 * Adds a foreign key to this table that references a single column of another table.<br>
	 * The referential actions for update and delete default to {@link SqlReferentialAction#NO_ACTION}.<br>
	 *
	 * @param referencingColumns The columns of this table that reference the other table
	 * @param referencedTable The table that is referenced
	 * @param referencedColumn The column of the referenced table that is referenced
	 * @return The newly created and registered foreign key
	 * @throws NullPointerException If the referencing columns, referenced table or referenced column is null
	 * @throws IllegalArgumentException If the referencing columns are empty, a referencing column does not belong to this table, or the referenced column does not belong to the referenced table
	 */
	public <T> @NonNull SqlTableForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		Objects.requireNonNull(referencedColumn, "Sql referenced column must not be null");
		return this.addForeignKey(new SqlTableForeignKey<>(referencingColumns, SqlForeignKey.of(referencedTable, referencedColumn)));
	}
	
	/**
	 * Adds a foreign key to this table that references the given columns of another table.<br>
	 * The referential actions for update and delete default to {@link SqlReferentialAction#NO_ACTION}.<br>
	 *
	 * @param referencingColumns The columns of this table that reference the other table
	 * @param referencedTable The table that is referenced
	 * @param referencedColumns The columns of the referenced table that are referenced
	 * @return The newly created and registered foreign key
	 * @throws NullPointerException If the referencing columns, referenced table or referenced columns is null
	 * @throws IllegalArgumentException If the referencing or referenced columns are empty, a referencing column does not belong to this table, or a referenced column does not belong to the referenced table
	 */
	public <T> @NonNull SqlTableForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns) {
		return this.addForeignKey(new SqlTableForeignKey<>(referencingColumns, SqlForeignKey.of(referencedTable, referencedColumns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION)));
	}
	
	/**
	 * Adds a foreign key to this table that references the given columns of another table using the given referential actions.<br>
	 *
	 * @param referencingColumns The columns of this table that reference the other table
	 * @param referencedTable The table that is referenced
	 * @param referencedColumns The columns of the referenced table that are referenced
	 * @param onUpdate The referential action applied when a referenced row is updated
	 * @param onDelete The referential action applied when a referenced row is deleted
	 * @return The newly created and registered foreign key
	 * @throws NullPointerException If the referencing columns, referenced table, referenced columns, on-update action or on-delete action is null
	 * @throws IllegalArgumentException If the referencing or referenced columns are empty, a referencing column does not belong to this table, or a referenced column does not belong to the referenced table
	 */
	public <T> @NonNull SqlTableForeignKey<E, T> foreignKey(
		@NonNull List<SqlColumn<E, ?>> referencingColumns,
		@NonNull SqlTable<T> referencedTable,
		@NonNull List<SqlColumn<T, ?>> referencedColumns,
		@NonNull SqlReferentialAction onUpdate,
		@NonNull SqlReferentialAction onDelete
	) {
		return this.addForeignKey(new SqlTableForeignKey<>(referencingColumns, SqlForeignKey.of(referencedTable, referencedColumns, onUpdate, onDelete)));
	}
	//endregion
	
	//region Constraint factory methods
	
	/**
	 * Adds a unique constraint over the given columns to this table.<br>
	 * All columns must already be defined in this table.<br>
	 *
	 * @param columns The columns that make up the unique constraint
	 * @throws NullPointerException If the columns array or any of its elements is null
	 * @throws IllegalArgumentException If no columns are given or a column is not defined in this table
	 */
	@SafeVarargs
	public final void uniqueConstraint(SqlColumn<E, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		this.uniqueConstraint(Lists.newArrayList(columns));
	}
	
	/**
	 * Adds a unique constraint over the given list of columns to this table.<br>
	 * All columns must already be defined in this table.<br>
	 *
	 * @param columns The columns that make up the unique constraint
	 * @throws NullPointerException If the columns list or any of its elements is null
	 * @throws IllegalArgumentException If the columns list is empty or a column is not defined in this table
	 */
	public void uniqueConstraint(@NonNull List<SqlColumn<E, ?>> columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Unique constraint must contain at least one column");
		}
		
		for (SqlColumn<E, ?> column : columns) {
			Objects.requireNonNull(column, "Unique constraint columns must not contain null values");
			
			if (!this.columns.containsKey(column.name())) {
				throw new IllegalArgumentException("Column " + column.name() + " is not defined in table " + this.name);
			}
		}
		
		this.uniqueConstraints.add(new SqlUniqueConstraint<>(columns));
	}
	
	/**
	 * Adds a check constraint to this table.<br>
	 * The given condition must hold for every row of the table.<br>
	 *
	 * @param condition The condition that must hold for every row
	 * @throws NullPointerException If the condition is null
	 */
	public void checkConstraint(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		this.checkConstraints.add(condition);
	}
	//endregion
	
	/**
	 * Returns the entity type this table maps to.<br>
	 * @return The entity type
	 */
	public @NonNull Class<E> type() {
		return this.type;
	}
	
	/**
	 * Returns the name of this table.<br>
	 * @return The table name
	 */
	public @NonNull String name() {
		return this.name;
	}
	
	/**
	 * Returns the name of the schema this table belongs to.<br>
	 * @return The schema name
	 */
	public @NonNull String schema() {
		return this.schema;
	}
	
	/**
	 * Returns the columns of this table in insertion order.<br>
	 * @return An unmodifiable list of the columns
	 */
	public @NonNull @Unmodifiable List<SqlColumn<E, ?>> columns() {
		return List.copyOf(this.columns.values());
	}
	
	/**
	 * Returns the column of this table with the given name.<br>
	 *
	 * @param name The name of the column to look up
	 * @return The column with the given name or {@code null} if no such column exists
	 * @throws NullPointerException If the name is null
	 */
	public @Nullable SqlColumn<E, ?> columnForName(@NonNull String name) {
		Objects.requireNonNull(name, "Sql column name must not be null");
		return this.columns.get(name);
	}
	
	/**
	 * Returns the column of this table with the given one-based index.<br>
	 *
	 * @param index The one-based index of the column to look up
	 * @return The column with the given index or {@code null} if no such column exists
	 * @throws IllegalArgumentException If the index is less than 1
	 */
	public @Nullable SqlColumn<E, ?> columnForIndex(int index) {
		if (index < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than 0, but was " + index);
		}
		
		for (SqlColumn<E, ?> existing : this.columns.values()) {
			if (existing.index() == index) {
				return existing;
			}
		}
		return null;
	}
	
	/**
	 * Returns the composite primary key of this table.<br>
	 * @return An optional containing the composite primary key or an empty optional if none is defined
	 */
	public @NonNull Optional<SqlCompositePrimaryKey<E>> compositePrimaryKey() {
		return this.compositePrimaryKey;
	}
	
	/**
	 * Returns the primary key columns of this table.<br>
	 * If a composite primary key is defined, its columns are returned, otherwise all columns that are marked as primary keys are returned.<br>
	 *
	 * @return An unmodifiable list of the primary key columns
	 */
	public @NonNull @Unmodifiable List<SqlColumn<E, ?>> primaryKeyColumns() {
		if (this.compositePrimaryKey.isPresent()) {
			return this.compositePrimaryKey.get().columns();
		}
		
		List<SqlColumn<E, ?>> primaryKeyColumns = Lists.newArrayList();
		for (SqlColumn<E, ?> column : this.columns.values()) {
			if (column.primaryKey()) {
				primaryKeyColumns.add(column);
			}
		}
		return Collections.unmodifiableList(primaryKeyColumns);
	}
	
	/**
	 * Returns the foreign keys defined on this table.<br>
	 * @return An unmodifiable list of the foreign keys
	 */
	public @NonNull @Unmodifiable List<SqlTableForeignKey<E, ?>> foreignKeys() {
		return Collections.unmodifiableList(this.foreignKeys);
	}
	
	/**
	 * Returns the unique constraints defined on this table.<br>
	 * @return An unmodifiable list of the unique constraints
	 */
	public @NonNull @Unmodifiable List<SqlUniqueConstraint<E>> uniqueConstraints() {
		return Collections.unmodifiableList(this.uniqueConstraints);
	}
	
	/**
	 * Returns the check constraints defined on this table.<br>
	 * @return An unmodifiable list of the check constraints
	 */
	public @NonNull @Unmodifiable List<SqlCondition> checkConstraints() {
		return Collections.unmodifiableList(this.checkConstraints);
	}
	
	/**
	 * Checks whether this table is audited.<br>
	 * @return {@code true} if an audit configuration is set, otherwise {@code false}
	 */
	public boolean isAudited() {
		return this.auditConfig != null;
	}
	
	/**
	 * Returns the audit configuration of this table.<br>
	 * @return An optional containing the audit configuration or an empty optional if the table is not audited
	 */
	public @NonNull Optional<SqlAuditConfig> auditConfig() {
		return Optional.ofNullable(this.auditConfig);
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlTable<?> sqlTable)) return false;
		
		if (!this.type.equals(sqlTable.type)) return false;
		if (!this.name.equals(sqlTable.name)) return false;
		if (!this.schema.equals(sqlTable.schema)) return false;
		if (!this.foreignKeys.equals(sqlTable.foreignKeys)) return false;
		if (!this.uniqueConstraints.equals(sqlTable.uniqueConstraints)) return false;
		if (!this.checkConstraints.equals(sqlTable.checkConstraints)) return false;
		if (!this.columns.equals(sqlTable.columns)) return false;
		if (!Objects.equals(this.auditConfig, sqlTable.auditConfig)) return false;
		return Objects.equals(this.compositePrimaryKey, sqlTable.compositePrimaryKey);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.name, this.schema, this.foreignKeys, this.uniqueConstraints, this.checkConstraints, this.columns, this.auditConfig);
	}
	
	@Override
	public String toString() {
		return "public".equals(this.schema) ? this.name : this.schema + "." + this.name;
	}
	//endregion
}
