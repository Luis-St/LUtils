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

package net.luis.utils.io.database.expression.orderable;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.AliasedSqlExpression;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record AscendingOrderedSqlExpression<T>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlNullOrdering nullOrdering
) implements OrderedSqlExpression<T> {
	
	public AscendingOrderedSqlExpression(@NonNull SqlExpression<T> expression) {
		this(expression, SqlNullOrdering.DEFAULT);
	}
	
	public AscendingOrderedSqlExpression {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(nullOrdering, "Sql null ordering must not be null");
	}
	
	@Override
	public @NonNull SqlOrdering ordering() {
		return SqlOrdering.ASCENDING;
	}
	
	@Override
	public @NonNull SqlExpression<T> as(@NonNull SqlAlias alias) {
		return new AliasedSqlExpression<>(this, alias);
	}
	
	@Override
	public @NonNull OrderedSqlExpression<T> ascending() {
		return this;
	}
	
	@Override
	public @NonNull OrderedSqlExpression<T> descending() {
		return new DescendingOrderedSqlExpression<>(this.expression);
	}
	
	@Override
	public @NonNull OrderedSqlExpression<T> nullsFirst() {
		if (this.nullOrdering == SqlNullOrdering.NULLS_FIRST) {
			return this;
		}
		return new AscendingOrderedSqlExpression<>(this.expression, SqlNullOrdering.NULLS_FIRST);
	}
	
	@Override
	public @NonNull OrderedSqlExpression<T> nullsLast() {
		if (this.nullOrdering == SqlNullOrdering.NULLS_LAST) {
			return this;
		}
		return new AscendingOrderedSqlExpression<>(this.expression, SqlNullOrdering.NULLS_LAST);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderExpression(this);
	}
}
