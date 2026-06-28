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

package net.luis.utils.io.database.expression;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A sql expression that assigns an alias to a wrapped expression.<br>
 *
 * @author Luis-St
 *
 * @param expression The wrapped expression
 * @param alias The alias assigned to the expression
 * @param <T> The type of the value the expression evaluates to
 */
public record SqlAliasedExpression<T>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlAlias alias
) implements SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new aliased expression with the given expression and alias.<br>
	 * @throws NullPointerException If the expression or alias is null
	 */
	public SqlAliasedExpression {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(alias, "Sql alias must not be null");
	}
	
	@Override
	public @NonNull SqlExpression<T> as(@NonNull SqlAlias alias) {
		return new SqlAliasedExpression<>(this.expression, alias);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderExpression(this);
	}
}
