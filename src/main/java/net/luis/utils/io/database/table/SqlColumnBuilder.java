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
import net.luis.utils.io.database.SqlDataType;
import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class SqlColumnBuilder<E, C> {
	
	private final String name;
	private final SqlDataType<C> dataType;
	private final Function<E, C> getter;
	private boolean nullable = true;
	private Optional<C> defaultValue = Optional.empty();
	private boolean autoIncrement;
	private boolean unique;
	private boolean primaryKey;
	private Optional<SqlForeignKey<E, ?>> foreignKey = Optional.empty();
	private final List<SqlCondition> constraints = Lists.newArrayList();
	
	public SqlColumnBuilder(@NonNull String name, @NonNull SqlDataType<C> dataType, @NonNull Function<E, C> getter) {
		this.name = Objects.requireNonNull(name, "Column name must not be null");
		this.dataType = Objects.requireNonNull(dataType, "Data type must not be null");
		this.getter = Objects.requireNonNull(getter, "Getter function must not be null");
	}
	
	public @NonNull SqlColumnBuilder<E, C> notNull() {
		this.nullable = false;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<E, C> defaultValue(@NonNull C defaultValue) {
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		this.defaultValue = Optional.of(defaultValue);
		return this;
	}
	
	public @NonNull SqlColumnBuilder<E, C> autoIncrement() {
		this.autoIncrement = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<E, C> unique() {
		this.unique = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<E, C> primaryKey() {
		this.primaryKey = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<E, C> addConstraint(@NonNull SqlCondition constraint) {
		Objects.requireNonNull(constraint, "Constraint must not be null");
		this.constraints.add(constraint);
		return this;
	}
	
	public <T> @NonNull SqlColumnBuilder<E, C> foreignKey(@NonNull SqlTable<T> referencedTable) {
		this.foreignKey = Optional.of(new SqlForeignKey<>(referencedTable));
		return this;
	}
	
	public <T> @NonNull SqlColumnBuilder<E, C> foreignKey(@NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		this.foreignKey = Optional.of(new SqlForeignKey<>(referencedTable, referencedColumn));
		return this;
	}
	
	public @NonNull SqlColumn<E, C> build() {
		return new SqlColumn<>(this.name, this.dataType, this.getter, this.nullable, this.defaultValue, this.autoIncrement, this.unique, this.primaryKey, this.foreignKey, this.constraints);
	}
}
