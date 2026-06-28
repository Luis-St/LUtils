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

package net.luis.utils.io.database.dialect.renderer.expression;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.*;
import net.luis.utils.io.database.expression.orderable.OrderedSqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for sql expressions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlExpression} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlExpressionRenderer {
	
	/**
	 * The sql dialect used to render the expressions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql expression renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the expressions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlExpressionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given sql expression into dialect-specific sql.<br>
	 * The expression is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param expression The sql expression to render
	 * @return The rendered sql
	 * @throws NullPointerException If the expression is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlExpression<?> expression) throws SqlException {
		return switch (expression) {
			case OrderedSqlExpression<?> expr -> this.renderOrdered(expr);
			case SqlAliasedExpression<?> aliased -> this.renderAliased(aliased);
			case SqlColumn<?, ?> column -> column.toSql(this.dialect);
			case SqlFunction<?> func -> this.dialect.renderFunction(func);
			case SqlValueExpression<?> value -> this.renderValue(value);
			
			case null -> throw new NullPointerException("Sql expression must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql expression type: " + expression.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given ordered sql expression into dialect-specific sql.<br>
	 * The underlying expression is rendered first, followed by the ordering and null ordering clause.<br>
	 *
	 * @param expression The ordered sql expression to render
	 * @return The rendered sql
	 * @throws NullPointerException If the expression is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderOrdered(@NonNull OrderedSqlExpression<?> expression) throws SqlException {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(this.render(expression.expression()));
		renderer.rendered(this.dialect.renderOrdering(expression.ordering(), expression.nullOrdering()));
		return renderer.toSql();
	}
	
	/**
	 * Renders the given aliased sql expression into dialect-specific sql.<br>
	 * The underlying expression is rendered first, followed by an {@code AS} clause with the quoted alias.<br>
	 *
	 * @param expression The aliased sql expression to render
	 * @return The rendered sql
	 * @throws NullPointerException If the expression is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAliased(@NonNull SqlAliasedExpression<?> expression) throws SqlException {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(this.render(expression.expression()));
		renderer.as().literal(this.dialect.quoteIdentifier(expression.alias().get()));
		return renderer.toSql();
	}
	
	/**
	 * Renders the given value sql expression into dialect-specific sql.<br>
	 * The value is rendered as a bound parameter using its type and value.<br>
	 *
	 * @param expression The value sql expression to render
	 * @return The rendered sql
	 * @throws NullPointerException If the expression is null
	 * @throws SqlException If rendering fails
	 * @param <T> The type of the value
	 */
	protected <T> @NonNull SqlRendered renderValue(@NonNull SqlValueExpression<T> expression) throws SqlException {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		
		return SqlRenderer.empty().parameter(expression.type(), expression.value()).toSql();
	}
}
