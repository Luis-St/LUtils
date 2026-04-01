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

/**
 *
 * @author Luis-St
 *
 */

public class SqlColumnBuilder<T> {
	
	private final String name;
	private final SqlDataType<T> dataType;
	private boolean nullable = true;
	private Optional<T> defaultValue = Optional.empty();
	private boolean autoIncrement;
	private boolean unique;
	private boolean primaryKey;
	private Optional<SqlForeignKey> foreignKey = Optional.empty();
	private final List<SqlCondition> constraints = Lists.newArrayList();
	
	public SqlColumnBuilder(@NonNull String name, @NonNull SqlDataType<T> dataType) {
		this.name = Objects.requireNonNull(name, "Column name must not be null");
		this.dataType = Objects.requireNonNull(dataType, "Data type must not be null");
	}
	
	public @NonNull SqlColumnBuilder<T> notNull() {
		this.nullable = false;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> defaultValue(@NonNull T defaultValue) {
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		this.defaultValue = Optional.of(defaultValue);
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> autoIncrement() {
		this.autoIncrement = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> unique() {
		this.unique = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> primaryKey() {
		this.primaryKey = true;
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> addConstraint(@NonNull SqlCondition constraint) {
		Objects.requireNonNull(constraint, "Constraint must not be null");
		this.constraints.add(constraint);
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> foreignKey(@NonNull SqlTable<?> referencedTable) {
		this.foreignKey = Optional.of(new SqlForeignKey(referencedTable));
		return this;
	}
	
	public @NonNull SqlColumnBuilder<T> foreignKey(@NonNull SqlTable<?> referencedTable, @NonNull SqlColumn<?> referencedColumn) {
		this.foreignKey = Optional.of(new SqlForeignKey(referencedTable, referencedColumn));
		return this;
	}
	
	public @NonNull SqlColumn<T> build() {
		return new SqlColumn<>(this.name, this.dataType, this.nullable, this.defaultValue, this.autoIncrement, this.unique, this.primaryKey, this.foreignKey, this.constraints);
	}
}
