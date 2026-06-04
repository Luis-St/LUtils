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
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTable<E> {
	
	private final Class<E> type;
	private final String name;
	private final String schema;
	private final Map</*Name*/ String, SqlColumn<E, ?>> columns = Maps.newLinkedHashMap();
	private final List<SqlTableForeignKey<E, ?>> foreignKeys = Lists.newArrayList();
	private final List<SqlUniqueConstraint<E>> uniqueConstraints = Lists.newArrayList();
	private final List<SqlCondition> checkConstraints = Lists.newArrayList();
	private final AtomicInteger columnCounter = new AtomicInteger(1);
	private final SqlAuditConfig auditConfig;
	private Optional<SqlCompositePrimaryKey<E>> compositePrimaryKey = Optional.empty();
	
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
	
	//region Helper methods
	
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
	
	//region Table factory methods
	
	public static <T> @NonNull SqlTable<T> create(@NonNull Class<T> type, @NonNull String name) {
		return create(type, name, "public");
	}
	
	public static <T> @NonNull SqlTable<T> create(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		return new SqlTable<>(type, name, schema, null);
	}
	
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name) {
		return audited(type, name, "public", SqlAuditConfig.DEFAULT);
	}
	
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		return audited(type, name, schema, SqlAuditConfig.DEFAULT);
	}
	
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull SqlAuditConfig config) {
		return audited(type, name, "public", config);
	}
	
	public static <T> @NonNull SqlTable<T> audited(@NonNull Class<T> type, @NonNull String name, @NonNull String schema, @NonNull SqlAuditConfig config) {
		Objects.requireNonNull(config, "Sql audit config must not be null");
		return new SqlTable<>(type, name, schema, config);
	}
	//endregion
	
	//region Column factory methods
	
	public <C> @NonNull SqlColumn<E, C> column(@NonNull String name, @NonNull SqlType<C> type, @NonNull Function<E, C> getter) {
		return this.column(name, type, getter, UnaryOperator.identity());
	}
	
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
		
		SqlColumn<E, ?> previous = this.columnForIndex(column.index());
		if (previous != null) {
			throw new IllegalStateException("Multiple columns (" + column.name() + " and " + previous.name() + ") in table '" + this.name + "' share the same index " + column.index());
		}
		
		if (this.compositePrimaryKey.isEmpty()) {
			List<SqlColumn<E, ?>> primaryKeyColumns = Lists.newArrayList(this.columns.values().stream().filter(SqlColumn::primaryKey).toList());
			if (column.primaryKey()) {
				primaryKeyColumns.add(column);
			}
			
			if (primaryKeyColumns.size() > 1) {
				String columnNames = primaryKeyColumns.stream().map(SqlColumn::name).collect(Collectors.joining("', '", "'", "'"));
				throw new IllegalStateException("Multiple columns (" + columnNames + ") in table '" + this.name + "' are marked as primary keys but no composite primary key is defined");
			}
		}
		
		this.columns.put(column.name(), column);
		return column;
	}
	//endregion
	
	//region Key factory methods
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
	
	public @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull List<SqlColumn<E, ?>> columns) {
		return this.setCompositePrimaryKey(new SqlCompositePrimaryKey<>(columns));
	}
	
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
	
	public <T> @NonNull SqlTableForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		Objects.requireNonNull(referencedColumn, "Sql referenced column must not be null");
		return this.addForeignKey(new SqlTableForeignKey<>(referencingColumns, SqlForeignKey.of(referencedTable, referencedColumn)));
	}
	
	public <T> @NonNull SqlTableForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns) {
		return this.addForeignKey(new SqlTableForeignKey<>(referencingColumns, SqlForeignKey.of(referencedTable, referencedColumns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION)));
	}
	
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
	
	@SafeVarargs
	public final void uniqueConstraint(SqlColumn<E, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		this.uniqueConstraint(Lists.newArrayList(columns));
	}
	
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
	
	public void checkConstraint(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		this.checkConstraints.add(condition);
	}
	//endregion
	
	public @NonNull Class<E> type() {
		return this.type;
	}
	
	public @NonNull String name() {
		return this.name;
	}
	
	public @NonNull String schema() {
		return this.schema;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<E, ?>> columns() {
		return List.copyOf(this.columns.values());
	}
	
	public @Nullable SqlColumn<E, ?> columnForName(@NonNull String name) {
		return this.columns.get(name);
	}
	
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
	
	public @NonNull Optional<SqlCompositePrimaryKey<E>> compositePrimaryKey() {
		return this.compositePrimaryKey;
	}
	
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
	
	public @NonNull @Unmodifiable List<SqlTableForeignKey<E, ?>> foreignKeys() {
		return Collections.unmodifiableList(this.foreignKeys);
	}
	
	public @NonNull @Unmodifiable List<SqlUniqueConstraint<E>> uniqueConstraints() {
		return Collections.unmodifiableList(this.uniqueConstraints);
	}
	
	public @NonNull @Unmodifiable List<SqlCondition> checkConstraints() {
		return Collections.unmodifiableList(this.checkConstraints);
	}
	
	public boolean isAudited() {
		return this.auditConfig != null;
	}
	
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
		if (this.columnCounter.get() != sqlTable.columnCounter.get()) return false;
		if (!Objects.equals(this.auditConfig, sqlTable.auditConfig)) return false;
		return Objects.equals(this.compositePrimaryKey, sqlTable.compositePrimaryKey);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.name, this.schema, this.foreignKeys, this.uniqueConstraints, this.checkConstraints, this.columns, this.columnCounter.get(), this.auditConfig);
	}
	
	@Override
	public String toString() {
		return "public".equals(this.schema) ? this.name : this.schema + "." + this.name;
	}
	//endregion
}
