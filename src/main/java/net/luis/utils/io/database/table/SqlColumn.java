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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public record SqlColumn<E, C>(
	@NonNull SqlTable<E> owningTable,
	@NonNull String name,
	int index,
	@NonNull SqlType<C> type,
	@NonNull Function<E, C> getter,
	boolean nullable,
	@NonNull Optional<C> defaultValue,
	boolean autoIncrement,
	boolean unique,
	boolean primaryKey,
	@NonNull Optional<SqlForeignKey<?>> foreignKey,
	@NonNull @Unmodifiable List<SqlCondition> checks
) implements SqlExpression<C> {
	
	private static final Set<Integer> NUMERIC_TYPES = Set.of(
		Types.TINYINT,
		Types.SMALLINT,
		Types.INTEGER,
		Types.BIGINT,
		Types.FLOAT,
		Types.REAL,
		Types.DOUBLE,
		Types.NUMERIC,
		Types.DECIMAL
	);
	
	public SqlColumn {
		Objects.requireNonNull(owningTable, "Owning Sql table must not be null");
		Objects.requireNonNull(name, "Sql column name must not be null");
		Objects.requireNonNull(type, "Sql column type must not be null");
		Objects.requireNonNull(getter, "Getter function must not be null");
		Objects.requireNonNull(defaultValue, "Sql default value must not be null");
		Objects.requireNonNull(foreignKey, "Sql foreign key must not be null");
		Objects.requireNonNull(checks, "Sql checks must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Sql column name must not be blank");
		}
		if (index < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than 0");
		}
		if (autoIncrement && !NUMERIC_TYPES.contains(type.jdbcType())) {
			throw new IllegalArgumentException("Auto-increment is only supported for numeric data types");
		}
		
		checks = List.copyOf(checks);
	}
	
	@Override
	public @NonNull SqlType<C> type() {
		return this.type;
	}
	
	public @NonNull SqlExpression<C> of(@NonNull SqlAlias alias) {
		return new SqlAliasedColumn<>(this, alias);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		String prefix = "public".equals(this.owningTable.schema()) ? "" : dialect.quoteIdentifier(this.owningTable.schema()) + ".";
		return SqlRendered.of(prefix + dialect.quoteIdentifier(this.owningTable.name()) + "." + dialect.quoteIdentifier(this.name));
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlColumn<?, ?> sqlColumn)) return false;
		
		if (this.index != sqlColumn.index) return false;
		if (this.unique != sqlColumn.unique) return false;
		if (this.nullable != sqlColumn.nullable) return false;
		if (this.primaryKey != sqlColumn.primaryKey) return false;
		if (this.autoIncrement != sqlColumn.autoIncrement) return false;
		if (!this.name.equals(sqlColumn.name)) return false;
		if (!this.type.equals(sqlColumn.type)) return false;
		if (!this.owningTable.equals(sqlColumn.owningTable)) return false;
		if (!this.defaultValue.equals(sqlColumn.defaultValue)) return false;
		if (!this.checks.equals(sqlColumn.checks)) return false;
		return this.foreignKey.equals(sqlColumn.foreignKey);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.owningTable, this.name, this.index, this.type, this.nullable, this.defaultValue, this.autoIncrement, this.unique, this.primaryKey, this.foreignKey, this.checks);
	}
	//endregion
}
