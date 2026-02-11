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
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.operation.*;
import net.luis.utils.io.database.query.SqlSelectQuery;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL column.<br>
 * <p>
 *     Extends {@link SqlExpression} to support aliasing via {@link #as(String)}
 *     and usage in conditions and ordering.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 *
 * @see SqlExpression
 */
public interface SqlColumn<T> extends SqlExpression<T> {
	
	/**
	 * Returns a dialect-specific view of this column.<br>
	 *
	 * @param dialect The SQL dialect to use
	 * @param <TT> The dialect table type
	 * @param <CC> The dialect column type
	 * @return The dialect-specific column
	 */
	<TT, CC> @NonNull CC dialect(@NonNull SqlDialect<TT, CC> dialect);
	
	@Override
	@NonNull SqlCondition equalTo(@NonNull T value);
	
	@Override
	@NonNull SqlCondition notEqualTo(@NonNull T value);
	
	@Override
	@NonNull SqlCondition greaterThan(@NonNull T value);
	
	@Override
	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull T value);
	
	@Override
	@NonNull SqlCondition lessThan(@NonNull T value);
	
	@Override
	@NonNull SqlCondition lessThanOrEqualTo(@NonNull T value);
	
	@Override
	@NonNull SqlCondition between(@NonNull T start, @NonNull T end);
	
	@Override
	@NonNull SqlCondition isNull();
	
	@Override
	@NonNull SqlCondition isNotNull();
	
	/**
	 * Returns string-specific operations for this column.<br>
	 * @return String operations accessor
	 */
	@NonNull SqlStringOps string();
	
	/**
	 * Returns numeric-specific operations for this column.<br>
	 * @return Numeric operations accessor
	 */
	@NonNull SqlNumericOps numeric();
	
	/**
	 * Returns temporal-specific operations for this column.<br>
	 * @return Temporal operations accessor
	 */
	@NonNull SqlTemporalOps temporal();
	
	/**
	 * Creates an IN condition with the specified values.<br>
	 *
	 * @param values The values to check against
	 * @return A condition for IN comparison
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlCondition in(T @NonNull ... values);
	
	/**
	 * Creates an IN condition with the specified list of values.<br>
	 *
	 * @param values The list of values to check against
	 * @return A condition for IN comparison
	 */
	@NonNull SqlCondition in(@NonNull List<T> values);
	
	/**
	 * Creates an IN condition with a subquery.<br>
	 *
	 * @param subquery The subquery providing values
	 * @return A condition for IN comparison with subquery
	 */
	@NonNull SqlCondition in(@NonNull SqlSelectQuery<?> subquery);
	
	/**
	 * Creates a NOT IN condition with the specified values.<br>
	 *
	 * @param values The values to exclude
	 * @return A condition for NOT IN comparison
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlCondition notIn(T @NonNull ... values);
	
	/**
	 * Creates a NOT IN condition with the specified list of values.<br>
	 *
	 * @param values The list of values to exclude
	 * @return A condition for NOT IN comparison
	 */
	@NonNull SqlCondition notIn(@NonNull List<T> values);
	
	/**
	 * Creates an aliased version of this column for use in SELECT projections.<br>
	 * <p>
	 *     The alias is used in the generated SQL (e.g., {@code SELECT email AS user_email})
	 *     and for mapping results to target types in {@code fetchAs} methods.<br>
	 * </p>
	 *
	 * @param alias The alias name for this column
	 * @return An expression representing this column with the specified alias
	 */
	@Override
	@NonNull SqlExpression<T> as(@NonNull String alias);
	
	@Override
	@NonNull SqlColumn<T> asc();
	
	@Override
	@NonNull SqlColumn<T> desc();
	
	@Override
	@NonNull SqlColumn<T> nullsFirst();
	
	@Override
	@NonNull SqlColumn<T> nullsLast();
}
