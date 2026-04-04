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
import net.luis.utils.io.database.condition.SqlExpression;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlAlreadyBindException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlCodec;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class SqlColumn<E, C> implements SqlExpression<C> {
	
	private final String name;
	private final int index;
	private final SqlCodec<C> codec;
	private final Function<E, C> getter;
	private final boolean nullable;
	private final Optional<C> defaultValue;
	private final boolean autoIncrement;
	private final boolean unique;
	private final boolean primaryKey;
	private final Optional<SqlForeignKey<E, ?>> foreignKey;
	private final List<SqlCondition> checks = Lists.newArrayList();
	private SqlTable<E> table;
	
	SqlColumn(
		@NonNull String name,
		int index,
		@NonNull SqlCodec<C> codec,
		@NonNull Function<E, C> getter,
		boolean nullable,
		@NonNull Optional<C> defaultValue,
		boolean autoIncrement,
		boolean unique,
		boolean primaryKey,
		Optional<SqlForeignKey<E, ?>> foreignKey,
		@NonNull List<SqlCondition> checks
	) {
		this.name = Objects.requireNonNull(name, "Column name must not be null");
		this.index = index;
		this.codec = Objects.requireNonNull(codec, "Column codec must not be null");
		this.getter = Objects.requireNonNull(getter, "Getter function must not be null");
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
		/*if (autoIncrement && !dataType.columnType().isNumeric()) {
			// ToDo: Fix validation
			throw new IllegalArgumentException("Auto-increment is only supported for numeric data types");
		}*/
	}
	
	public static <E, C> @NonNull SqlColumnBuilder<E, C> builder(@NonNull String name, int index, @NonNull SqlCodec<C> codec, @NonNull Function<E, C> getter) {
		return new SqlColumnBuilder<>(name, index, codec, getter);
	}
	
	public void bindTo(@NonNull SqlTable<E> table) throws SqlAlreadyBindException {
		Objects.requireNonNull(table, "Owning table must not be null");
		if (this.table != null) {
			throw new SqlAlreadyBindException("Column " + this.name + " is already associated with table " + this.table.getName());
		}
		
		this.table = table;
		if (this.foreignKey.isPresent()) {
			this.foreignKey.get().bindTo(table, this);
		}
	}
	
	public @UnknownNullability SqlTable<?> getOwningTable() {
		return this.table;
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public @NonNull SqlCodec<C> getCodec() {
		return this.codec;
	}
	
	public @NonNull Function<E, C> getGetter() {
		return this.getter;
	}
	
	public boolean isNullable() {
		return this.nullable;
	}
	
	public @NonNull Optional<C> getDefaultValue() {
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
	
	public @NonNull Optional<SqlForeignKey<E, ?>> getForeignKey() {
		return this.foreignKey;
	}
	
	public @NonNull @Unmodifiable List<SqlCondition> getChecks() {
		return Collections.unmodifiableList(this.checks);
	}
	
	public @NonNull SqlExpression<C> of(@NonNull SqlAlias alias) {
		// Binds the column to the given alias, used for nested queries, common table expressions, joins, etc.
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<C> as(@NonNull SqlAlias alias) {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<C> ascending() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<C> descending() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<C> nullsFirst() {
		return null;
	}
	
	@Override
	public @NonNull SqlExpression<C> nullsLast() {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
