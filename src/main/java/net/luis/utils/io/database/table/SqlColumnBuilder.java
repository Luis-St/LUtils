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
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Function;

/**
 * A fluent builder for creating {@link SqlColumn} instances.<br>
 * The builder is initialized with the required column properties and exposes chainable methods to configure the optional
 * constraints of the column before it is created through {@link #build()}.<br>
 * By default a column is nullable, has no default value and is neither auto-incremented, unique nor a primary key.<br>
 *
 * @see SqlColumn
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table stores
 * @param <C> The type of the value held by the built column
 */
public class SqlColumnBuilder<E, C> {
	
	/**
	 * The table the built column belongs to.
	 */
	private final SqlTable<E> table;
	/**
	 * The name of the built column.
	 */
	private final String name;
	/**
	 * The one-based position of the built column within the table.
	 */
	private final int index;
	/**
	 * The sql type of the built column value.
	 */
	private final SqlType<C> type;
	/**
	 * The function that extracts the column value from an entity.
	 */
	private final Function<E, C> getter;
	/**
	 * The check conditions applied to the column values.
	 */
	private final List<SqlCondition> constraints = Lists.newArrayList();
	/**
	 * Whether the built column accepts {@code null} values.
	 */
	private boolean nullable = true;
	/**
	 * The default value of the built column or an empty optional if none is set.
	 */
	private Optional<C> defaultValue = Optional.empty();
	/**
	 * Whether the built column is auto-incremented.
	 */
	private boolean autoIncrement;
	/**
	 * Whether the built column values must be unique.
	 */
	private boolean unique;
	/**
	 * Whether the built column is part of the primary key.
	 */
	private boolean primaryKey;
	/**
	 * The foreign key referenced by the built column or an empty optional if none is set.
	 */
	private Optional<SqlForeignKey<?>> foreignKey = Optional.empty();
	
	/**
	 * Constructs a new column builder with the required column properties.<br>
	 *
	 * @param table The table the built column belongs to
	 * @param name The name of the built column
	 * @param index The one-based position of the built column within the table
	 * @param type The sql type of the built column value
	 * @param getter The function that extracts the column value from an entity
	 * @throws NullPointerException If the table, name, type or getter is null
	 */
	public SqlColumnBuilder(@NonNull SqlTable<E> table, @NonNull String name, int index, @NonNull SqlType<C> type, @NonNull Function<E, C> getter) {
		this.table = Objects.requireNonNull(table, "Owning sql table must not be null");
		this.name = Objects.requireNonNull(name, "Sql column name must not be null");
		this.index = index;
		this.type = Objects.requireNonNull(type, "Sql column type must not be null");
		this.getter = Objects.requireNonNull(getter, "Getter function must not be null");
	}
	
	/**
	 * Marks the built column as not nullable so that it rejects {@code null} values.<br>
	 * @return This builder
	 */
	public @NonNull SqlColumnBuilder<E, C> notNull() {
		this.nullable = false;
		return this;
	}
	
	/**
	 * Sets the default value of the built column.<br>
	 *
	 * @param defaultValue The default value to set
	 * @return This builder
	 * @throws NullPointerException If the default value is null
	 */
	public @NonNull SqlColumnBuilder<E, C> defaultValue(@NonNull C defaultValue) {
		Objects.requireNonNull(defaultValue, "Sql default value must not be null");
		this.defaultValue = Optional.of(defaultValue);
		return this;
	}
	
	/**
	 * Marks the built column as auto-incremented.<br>
	 * Auto-increment is only supported for numeric column types.<br>
	 * @return This builder
	 */
	public @NonNull SqlColumnBuilder<E, C> autoIncrement() {
		this.autoIncrement = true;
		return this;
	}
	
	/**
	 * Marks the built column as unique so that its values must not repeat.<br>
	 * @return This builder
	 */
	public @NonNull SqlColumnBuilder<E, C> unique() {
		this.unique = true;
		return this;
	}
	
	/**
	 * Marks the built column as part of the primary key.<br>
	 * This also marks the column as not nullable.<br>
	 * @return This builder
	 */
	public @NonNull SqlColumnBuilder<E, C> primaryKey() {
		this.primaryKey = true;
		this.nullable = false;
		return this;
	}
	
	/**
	 * Adds a check constraint that the column values must satisfy.<br>
	 *
	 * @param constraint The check condition to add
	 * @return This builder
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull SqlColumnBuilder<E, C> addConstraint(@NonNull SqlCondition constraint) {
		Objects.requireNonNull(constraint, "Sql constraint must not be null");
		this.constraints.add(constraint);
		return this;
	}
	
	/**
	 * Sets a foreign key that references the primary key columns of the given table.<br>
	 *
	 * @param referencedTable The table to reference
	 * @return This builder
	 * @throws NullPointerException If the referenced table is null
	 * @see SqlForeignKey#of(SqlTable)
	 */
	public <T> @NonNull SqlColumnBuilder<E, C> foreignKey(@NonNull SqlTable<T> referencedTable) {
		this.foreignKey = Optional.of(SqlForeignKey.of(referencedTable));
		return this;
	}
	
	/**
	 * Sets a foreign key that references the given column of the given table.<br>
	 *
	 * @param referencedTable The table to reference
	 * @param referencedColumn The column to reference
	 * @return This builder
	 * @throws NullPointerException If the referenced table or column is null
	 * @see SqlForeignKey#of(SqlTable, SqlColumn)
	 */
	public <T> @NonNull SqlColumnBuilder<E, C> foreignKey(@NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		this.foreignKey = Optional.of(SqlForeignKey.of(referencedTable, referencedColumn));
		return this;
	}
	
	/**
	 * Builds a new column from the current configuration of this builder.<br>
	 *
	 * @return The created column
	 * @throws IllegalArgumentException If auto-increment is set for a non-numeric column type
	 * @see SqlColumn
	 */
	public @NonNull SqlColumn<E, C> build() {
		return new SqlColumn<>(this.table, this.name, this.index, this.type, this.getter, this.nullable, this.defaultValue, this.autoIncrement, this.unique, this.primaryKey, this.foreignKey, this.constraints);
	}
}
