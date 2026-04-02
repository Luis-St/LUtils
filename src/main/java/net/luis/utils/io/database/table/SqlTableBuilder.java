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
import net.luis.utils.io.database.SqlDataType;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTableBuilder<E> {
	
	private final Class<E> type;
	private final String name;
	private final String schema;
	private Optional<SqlCompositePrimaryKey<E>> compositePrimaryKey = Optional.empty();
	private final List<SqlForeignKey<E, ?>> foreignKeys = Lists.newArrayList();
	private final List<List<SqlColumn<E, ?>>> uniqueConstraints = Lists.newArrayList();
	private final List<SqlCondition> checkConstraints = Lists.newArrayList();
	private final Map</*Name*/ String, SqlColumn<E, ?>> columns = Maps.newHashMap();
	private final AtomicInteger columnCounter = new AtomicInteger(0);
	
	SqlTableBuilder(@NonNull Class<E> type, @NonNull String name, @NonNull String schema) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.schema = Objects.requireNonNull(schema, "Schema must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		if (schema.isBlank()) {
			throw new IllegalArgumentException("Table schema must not be blank");
		}
	}
	
	public <C> @NonNull SqlColumn<E, C> column(@NonNull String name, @NonNull SqlDataType<C> dataType, @NonNull Function<E, C> getter) {
		return this.column(name, dataType, getter, UnaryOperator.identity());
	}
	
	public <C> @NonNull SqlColumn<E, C> column(@NonNull String name, @NonNull SqlDataType<C> dataType, @NonNull Function<E, C> getter, @NonNull UnaryOperator<SqlColumnBuilder<E, C>> action) {
		Objects.requireNonNull(action, "Column configuration action must not be null");
		
		SqlColumn<E, C> column = action.apply(SqlColumn.builder(name, this.columnCounter.getAndIncrement(), dataType, getter)).build();
		if (this.columns.containsKey(column.getName())) {
			throw new IllegalStateException("Column with name " + name + " already exists in table " + this.name);
		}
		if (column.isPrimaryKey() && this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Unable to add primary key column " + name + " to table " + this.name + " because a composite primary key is already defined");
		}
		
		this.columns.put(column.getName(), column);
		return column;
	}
	
	@SafeVarargs
	public final @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(SqlColumn<E, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		return this.compositePrimaryKey(Lists.newArrayList(columns));
	}
	
	public @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull List<SqlColumn<E, ?>> columns) {
		return this.compositePrimaryKey(new SqlCompositePrimaryKey<>(columns));
	}
	
	public @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull List<SqlColumn<E, ?>> columns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete) {
		return this.compositePrimaryKey(new SqlCompositePrimaryKey<>(columns, onUpdate, onDelete));
	}
	
	public @NonNull SqlCompositePrimaryKey<E> generateCompositePrimaryKey() {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Unable to generate composite primary key for table " + this.name + " because a composite primary key is already defined");
		}
		
		List<SqlColumn<E, ?>> columns = Lists.newArrayList();
		for (SqlColumn<E, ?> column : this.columns.values()) {
			if (column.isPrimaryKey()) {
				columns.add(column);
			}
		}
		
		if (columns.isEmpty()) {
			throw new IllegalStateException("Unable to generate composite primary key for table " + this.name + " because no primary key columns are defined");
		}
		return this.compositePrimaryKey(new SqlCompositePrimaryKey<>(columns));
	}
	
	private @NonNull SqlCompositePrimaryKey<E> compositePrimaryKey(@NonNull SqlCompositePrimaryKey<E> reference) {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Composite primary key already defined for table " + this.name);
		}
		
		this.compositePrimaryKey = Optional.of(reference);
		return reference;
	}
	
	public <T> @NonNull SqlForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		Objects.requireNonNull(referencedColumn, "Referenced column must not be null");
		return this.foreignKey(new SqlForeignKey<>(referencingColumns, referencedTable, Lists.newArrayList(referencedColumn)));
	}
	
	public <T> @NonNull SqlForeignKey<E, T> foreignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns) {
		return this.foreignKey(new SqlForeignKey<>(referencingColumns, referencedTable, referencedColumns));
	}
	
	public <T> @NonNull SqlForeignKey<E, T> foreignKey(
		@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete
	) {
		return this.foreignKey(new SqlForeignKey<>(referencingColumns, referencedTable, referencedColumns, onUpdate, onDelete));
	}
	
	private <T> @NonNull SqlForeignKey<E, T> foreignKey(@NonNull SqlForeignKey<E, T> foreignKey) {
		this.foreignKeys.add(foreignKey);
		return foreignKey;
	}
	
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
			
			if (!this.columns.containsKey(column.getName())) {
				throw new IllegalArgumentException("Column " + column.getName() + " is not defined in table " + this.name);
			}
		}
		
		this.uniqueConstraints.add(columns);
	}
	
	public void checkConstraint(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Condition must not be null");
		
		this.checkConstraints.add(condition);
	}
	
	public @NonNull SqlTable<E> build() {
		return new SqlTable<>(this.type, this.name, this.schema, this.compositePrimaryKey, this.foreignKeys, this.uniqueConstraints, this.checkConstraints, this.columns);
	}
}
