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
import net.luis.utils.io.database.condition.SqlExpression;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jetbrains.annotations.*;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlColumn<T> implements SqlExpression<T> {
	
	private final String name;
	private final @NonNull SqlDataType<T> dataType;
	private final boolean nullable;
	private final @NonNull Optional<T> defaultValue;
	private final boolean autoIncrement;
	private final boolean unique;
	private final boolean primaryKey;
	private final Optional<SqlForeignKey> foreignKey;
	private final List<SqlCondition> checks = Lists.newArrayList();
	private SqlTable<?> table;
	
	SqlColumn(
		@NonNull String name,
		@NonNull SqlDataType<T> dataType,
		boolean nullable,
		@NonNull Optional<T> defaultValue,
		boolean autoIncrement,
		boolean unique,
		boolean primaryKey,
		Optional<SqlForeignKey> foreignKey,
		@NonNull List<SqlCondition> checks
	) {
		this.name = Objects.requireNonNull(name, "Column name must not be null");
		this.dataType = Objects.requireNonNull(dataType, "Data type must not be null");
		this.nullable = nullable;
		this.defaultValue = Objects.requireNonNull(defaultValue, "Default value must not be null");
		this.autoIncrement = autoIncrement;
		this.unique = unique;
		this.primaryKey = primaryKey;
		this.foreignKey = Objects.requireNonNull(foreignKey, "Foreign key must not be null");
		this.checks.addAll(Objects.requireNonNull(checks, "Checks must not be null"));
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Column name must not be blank");
		}
		if (autoIncrement && !dataType.columnType().isNumeric()) {
			throw new IllegalArgumentException("Auto-increment is only supported for numeric data types");
		}
		foreignKey.ifPresent(sqlForeignKey -> sqlForeignKey.setReferencingColumn(this));
	}
	
	public static <T> @NonNull SqlColumnBuilder<T> builder(@NonNull String name, @NonNull SqlDataType<T> dataType) {
		return new SqlColumnBuilder<>(name, dataType);
	}
	
	public @UnknownNullability SqlTable<?> getOwningTable() {
		return this.table;
	}
	
	@ApiStatus.Internal
	void setOwningTable(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Owning table must not be null");
		if (this.table != null) {
			throw new IllegalStateException("Column '" + this.name + "' is already associated with table " + this.table.getName());
		}
		this.table = table;
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public @NonNull SqlDataType<T> getDataType() {
		return this.dataType;
	}
	
	public boolean isNullable() {
		return this.nullable;
	}
	
	public @NonNull Optional<T> getDefaultValue() {
		return this.defaultValue;
	}
	
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}
	
	public boolean isUnique() {
		return this.unique;
	}
	
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}
	
	public @NonNull @Unmodifiable List<SqlCondition> getChecks() {
		return Collections.unmodifiableList(this.checks);
	}
	
	public @NonNull SqlExpression<T> of(@NonNull SqlAlias alias) {
		// Binds the column to the given alias, used for nested queries, common table expressions, joins, etc.
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<T> as(@NonNull SqlAlias alias) {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<T> ascending() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<T> descending() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<T> nullsFirst() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<T> nullsLast() {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
