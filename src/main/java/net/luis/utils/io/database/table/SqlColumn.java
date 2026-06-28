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
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.*;
import java.util.function.Function;

/**
 * Represents a single column of a {@link SqlTable}.<br>
 * A column couples its database metadata, such as name, type, position and constraints, with a getter that extracts the
 * column value from an entity of the owning table.<br>
 * As a {@link SqlExpression} a column can be used in queries and can be aliased through {@link #of(SqlAlias)}.<br>
 * Instances are usually created through a {@link SqlColumnBuilder}.<br>
 *
 * @see SqlTable
 * @see SqlColumnBuilder
 * @see SqlAliasedColumn
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table stores
 * @param <C> The type of the value held by this column
 * @param owningTable The table this column belongs to
 * @param name The name of the column
 * @param index The one-based position of the column within the table
 * @param type The sql type of the column value
 * @param getter The function that extracts the column value from an entity
 * @param nullable Whether the column accepts {@code null} values
 * @param defaultValue The default value of the column or an empty optional if none is set
 * @param autoIncrement Whether the column is auto-incremented
 * @param unique Whether the column values must be unique
 * @param primaryKey Whether the column is part of the primary key
 * @param foreignKey The foreign key referenced by the column or an empty optional if none is set
 * @param checks The check conditions applied to the column values
 */
public record SqlColumn<E, C>(
	@NonNull SqlTable<E> owningTable,
	@NonNull String name,
	int index,
	@NonNull SqlType<C> type,
	@NonNull Function<E, @Nullable C> getter,
	boolean nullable,
	@NonNull Optional<C> defaultValue,
	boolean autoIncrement,
	boolean unique,
	boolean primaryKey,
	@NonNull Optional<SqlForeignKey<?>> foreignKey,
	@NonNull @Unmodifiable List<SqlCondition> checks
) implements SqlExpression<C> {
	
	/**
	 * The set of jdbc type codes for which auto-increment is supported.
	 */
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
	
	/**
	 * Constructs a new sql column validating the given components.<br>
	 * The checks list is copied into an unmodifiable list.<br>
	 *
	 * @throws NullPointerException If the owning table, name, type, getter, default value, foreign key or checks is null
	 * @throws IllegalArgumentException If the name is blank, the index is less than 1 or auto-increment is set for a non-numeric type
	 */
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
	
	/**
	 * Creates an aliased expression that references this column through the given alias.<br>
	 *
	 * @param alias The alias to reference this column with
	 * @return An aliased column wrapping this column
	 * @throws NullPointerException If the alias is null
	 * @see SqlAliasedColumn
	 */
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
		if (!this.owningTable.schema().equals(sqlColumn.owningTable.schema())) return false;
		if (!this.owningTable.name().equals(sqlColumn.owningTable.name())) return false;
		if (!this.defaultValue.equals(sqlColumn.defaultValue)) return false;
		if (!this.checks.equals(sqlColumn.checks)) return false;
		return this.foreignKey.equals(sqlColumn.foreignKey);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.owningTable.schema(), this.owningTable.name(), this.name, this.index, this.type, this.nullable, this.defaultValue, this.autoIncrement, this.unique, this.primaryKey, this.foreignKey, this.checks);
	}
	//endregion
}
