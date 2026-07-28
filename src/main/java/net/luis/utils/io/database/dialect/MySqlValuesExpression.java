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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a reference to a column's proposed value within MySQL's {@code ON DUPLICATE KEY UPDATE} clause.<br>
 * Renders as {@code VALUES(column)}, MySQL's syntax for the value that was about to be inserted for that column.<br>
 *
 * @author Luis-St
 *
 * @param column The column to reference the proposed value of
 * @param <C> The type of the value held by the column
 */
public record MySqlValuesExpression<C>(@NonNull SqlColumn<?, C> column) implements SqlExpression<C> {
	
	/**
	 * Constructs a new mysql values expression with the given column.<br>
	 * @throws NullPointerException If the column is null
	 */
	public MySqlValuesExpression {
		Objects.requireNonNull(column, "Sql column must not be null");
	}
	
	@Override
	public @NonNull SqlType<C> type() {
		return this.column.type();
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return SqlRendered.of("VALUES(" + dialect.quoteIdentifier(this.column.name()) + ")");
	}
}
