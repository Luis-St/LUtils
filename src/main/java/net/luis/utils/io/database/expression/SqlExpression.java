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

import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRenderable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlExpression<T> extends SqlOrderable<T>, SqlRenderable {
	
	default @NonNull SqlExpression<T> as(@NonNull SqlAlias alias) {
		Objects.requireNonNull(alias, "Sql alias must not be null");
		return new SqlAliasedExpression<>(this, alias);
	}
	
	@NonNull SqlType<T> type();
	
	@Override
	default @NonNull OrderedSqlExpression<T> ascending() {
		return new AscendingOrderedSqlExpression<>(this);
	}
	
	@Override
	default @NonNull OrderedSqlExpression<T> descending() {
		return new DescendingOrderedSqlExpression<>(this);
	}
	
	@Override
	default @NonNull OrderedSqlExpression<T> nullsFirst() {
		return new DefaultOrderedSqlExpression<>(this, SqlNullOrdering.NULLS_FIRST);
	}
	
	@Override
	default @NonNull OrderedSqlExpression<T> nullsLast() {
		return new DefaultOrderedSqlExpression<>(this, SqlNullOrdering.NULLS_LAST);
	}
}
