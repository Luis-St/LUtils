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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a {@link SqlColumn} that is referenced through a {@link SqlAlias}.<br>
 * As a {@link SqlExpression} it renders the column qualified by the alias instead of by its owning table, which is used
 * when the table of the column is aliased within a query.<br>
 * Instances are usually created through {@link SqlColumn#of(SqlAlias)}.<br>
 *
 * @see SqlColumn
 * @see SqlAlias
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the owning table stores
 * @param <C> The type of the value held by the wrapped column
 * @param column The column referenced through the alias
 * @param alias The alias the column is referenced with
 */
public record SqlAliasedColumn<E, C>(
	@NonNull SqlColumn<E, C> column,
	@NonNull SqlAlias alias
) implements SqlExpression<C> {
	
	/**
	 * Constructs a new aliased column validating the given components.<br>
	 * @throws NullPointerException If the column or alias is null
	 */
	public SqlAliasedColumn {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(alias, "Sql alias must not be null");
	}
	
	@Override
	public @NonNull SqlType<C> type() {
		return this.column.type();
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return SqlRendered.of(dialect.quoteIdentifier(this.alias.get()) + "." + dialect.quoteIdentifier(this.column.name()));
	}
}
