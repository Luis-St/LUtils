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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.rendering.*;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a single column assignment of a sql update statement.<br>
 * A set clause assigns a value to a column based on the {@link SqlSetType set type}.<br>
 * The expression is only required if the type is not {@link SqlSetType#NULL}.<br>
 *
 * @see SqlSetType
 * @see SqlColumn
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the column belongs to
 * @param <V> The type of the value held by the column
 * @param column The column that is assigned a value
 * @param expression The expression used to compute the assigned value, or {@code null} if the type is {@link SqlSetType#NULL}
 * @param type The set type that determines how the value is computed
 */
public record SqlSetClause<E, V>(
	@NonNull SqlColumn<E, V> column,
	@Nullable SqlExpression<V> expression,
	@NonNull SqlSetType type
) implements SqlRenderable {
	
	/**
	 * Constructs a new sql set clause with the given column, expression and set type.<br>
	 *
	 * @param column The column that is assigned a value
	 * @param expression The expression used to compute the assigned value, or {@code null} if the type is {@link SqlSetType#NULL}
	 * @param type The set type that determines how the value is computed
	 * @throws NullPointerException If the column or the type is null, or if the expression is null and the type is not {@link SqlSetType#NULL}
	 */
	public SqlSetClause {
		Objects.requireNonNull(column, "Sql Column must not be null");
		Objects.requireNonNull(type, "Sql set type must not be null");
		
		if (type != SqlSetType.NULL) {
			Objects.requireNonNull(expression, "Sql expression must not be null");
		}
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		String quotedColumn = dialect.quoteIdentifier(this.column.name());
		String qualifiedColumn = dialect.quoteIdentifier(this.column.owningTable().name()) + "." + quotedColumn;
		switch (this.type) {
			case EXPRESSION -> renderer.literal(quotedColumn).literal("=");
			case INCREMENT -> renderer.literal(quotedColumn).literal("=").literal(qualifiedColumn).literal("+");
			case DECREMENT -> renderer.literal(quotedColumn).literal("=").literal(qualifiedColumn).literal("-");
			case NULL -> renderer.literal(quotedColumn).literal("=").null_();
		}
		
		if (this.expression != null) {
			renderer.rendered(dialect.renderExpression(this.expression));
		}
		return renderer.toSql();
	}
}
