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
import net.luis.utils.io.database.condition.SqlCondition;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTable<T> {
	
	private final Class<T> type;
	private final String name;
	private final String schema;
	private final Optional<SqlCompositePrimaryKey> compositePrimaryKey;
	private final List<SqlForeignKey> foreignKeys = Lists.newArrayList();
	private final List<List<SqlColumn<?>>> uniqueConstraints = Lists.newArrayList();
	private final List<SqlCondition> checkConstraints = Lists.newArrayList();
	private final Map</*Name*/ String, SqlColumn<?>> columns = Maps.newHashMap();
	
	SqlTable(
		@NonNull Class<T> type,
		@NonNull String name,
		@NonNull String schema,
		@NonNull Optional<SqlCompositePrimaryKey> compositePrimaryKey,
		@NonNull List<SqlForeignKey> foreignKeys,
		@NonNull List<List<SqlColumn<?>>> uniqueConstraints,
		@NonNull List<SqlCondition> checkConstraints,
		@NonNull Map<String, SqlColumn<?>> columns
	) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.schema = Objects.requireNonNull(schema, "Schema must not be null");
		this.compositePrimaryKey = Objects.requireNonNull(compositePrimaryKey, "Composite primary key must not be null");
		this.foreignKeys.addAll(Objects.requireNonNull(foreignKeys, "Foreign keys must not be null"));
		this.uniqueConstraints.addAll(Objects.requireNonNull(uniqueConstraints, "Unique constraints must not be null"));
		this.checkConstraints.addAll(Objects.requireNonNull(checkConstraints, "Check constraints must not be null"));
		this.columns.putAll(Objects.requireNonNull(columns, "Columns must not be null"));
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		if (schema.isBlank()) {
			throw new IllegalArgumentException("Table schema must not be blank");
		}
		
		for (List<SqlColumn<?>> uniqueConstraint : uniqueConstraints) {
			for (SqlColumn<?> column : uniqueConstraint) {
				this.validateColumn(column);
			}
		}
		
		this.validatePrimaryKey();
		
		for (SqlColumn<?> column : this.columns.values()) {
			column.setOwningTable(this);
		}
	}
	
	public static <T> @NonNull SqlTableBuilder<T> of(@NonNull Class<T> type, @NonNull String name) {
		return of(type, name, "public");
	}
	
	public static <T> @NonNull SqlTableBuilder<T> of(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		return new SqlTableBuilder<>(type, name, schema);
	}
	
	//region Validation
	
	private void validatePrimaryKey() {
		List<SqlColumn<?>> primaryKeyColumns = this.columns.values().stream().filter(SqlColumn::isPrimaryKey).toList();
		if (this.compositePrimaryKey.isEmpty()) {
			if (primaryKeyColumns.size() > 1) {
				throw new IllegalStateException(
					"Multiple columns (" + primaryKeyColumns.stream().map(SqlColumn::getName).collect(Collectors.joining(", ")) + ") in table " + this.name + " are marked as primary keys but no composite primary key is defined"
				);
			}
		} else {
			List<SqlColumn<?>> compositePrimaryKeyColumns = this.compositePrimaryKey.get().columns();
			if (compositePrimaryKeyColumns.isEmpty()) {
				throw new IllegalStateException("Composite primary key defined in table " + this.name + " but it does not contain any columns");
			}
			
			for (SqlColumn<?> column : compositePrimaryKeyColumns) {
				this.validatePrimaryKeyColumn(column);
			}
			
			for (SqlColumn<?> primaryKeyColumn : primaryKeyColumns) {
				if (compositePrimaryKeyColumns.stream().noneMatch(column -> column.getName().equals(primaryKeyColumn.getName()))) {
					throw new IllegalStateException("Primary key column '" + primaryKeyColumn.getName() + "' is defined in table " + this.name + " but not included in the composite primary key");
				}
			}
		}
	}
	
	private void validatePrimaryKeyColumn(@NonNull SqlColumn<?> column) {
		Objects.requireNonNull(column, "Column must not be null");
		this.validateColumn(column);
		
		if (!column.isPrimaryKey()) {
			throw new IllegalStateException("Column '" + column.getName() + "' of the composite primary key is not marked as a primary key in table " + this.name);
		}
	}
	
	private void validateColumn(@NonNull SqlColumn<?> column) {
		Objects.requireNonNull(column, "Column must not be null");
		if (column.getOwningTable() != null) {
			throw new IllegalStateException("Column '" + column.getName() + "' does already belong to another table " + column.getOwningTable().getName());
		}
		
		if (!this.columns.containsKey(column.getName())) {
			throw new IllegalStateException("Column '" + column.getName() + "' is not defined in table " + this.name);
		}
	}
	//endregion
	
	public @NonNull Class<T> getType() {
		return this.type;
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public @NonNull String getSchema() {
		return this.schema;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<?>> getPrimaryKeyColumns() {
		if (this.compositePrimaryKey.isPresent()) {
			return this.compositePrimaryKey.get().columns();
		}
		
		List<SqlColumn<?>> primaryKeyColumns = Lists.newArrayList();
		for (SqlColumn<?> column : this.columns.values()) {
			if (column.isPrimaryKey()) {
				primaryKeyColumns.add(column);
			}
		}
		return Collections.unmodifiableList(primaryKeyColumns);
	}
	
	public @NonNull Optional<SqlCompositePrimaryKey> getCompositePrimaryKey() {
		return this.compositePrimaryKey;
	}
	
	public @NonNull @Unmodifiable List<SqlForeignKey> getForeignKeys() {
		return Collections.unmodifiableList(this.foreignKeys);
	}
	
	public @NonNull @Unmodifiable List<List<SqlColumn<?>>> getUniqueConstraints() {
		return Collections.unmodifiableList(this.uniqueConstraints);
	}
	
	public @NonNull @Unmodifiable List<SqlCondition> getCheckConstraints() {
		return Collections.unmodifiableList(this.checkConstraints);
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<?>> getColumns() {
		return List.copyOf(this.columns.values());
	}
}
