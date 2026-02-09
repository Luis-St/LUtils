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
import net.luis.utils.io.database.query.SqlSelectQuery;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
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
	 * Creates a case-insensitive equality condition.<br>
	 *
	 * @param value The value to compare (case-insensitive)
	 * @return A condition for case-insensitive equality
	 */
	@NonNull SqlCondition equalToIgnoreCase(@NonNull String value);
	
	/**
	 * Creates a condition checking if the column value is within the specified duration from now.<br>
	 * Useful for temporal columns to check recent records.<br>
	 *
	 * @param duration The duration to check within
	 * @return A condition for temporal comparison
	 */
	@NonNull SqlCondition withinLast(@NonNull Duration duration);
	
	/**
	 * Creates a LIKE condition with the specified pattern.<br>
	 *
	 * @param pattern The LIKE pattern (use % for wildcards)
	 * @return A condition for pattern matching
	 */
	@NonNull SqlCondition like(@NonNull String pattern);
	
	/**
	 * Creates a condition checking if the column value starts with the specified prefix.<br>
	 *
	 * @param prefix The prefix to check
	 * @return A condition for prefix matching
	 */
	@NonNull SqlCondition startsWith(@NonNull String prefix);
	
	/**
	 * Creates a condition checking if the column value contains the specified substring.<br>
	 *
	 * @param substring The substring to check
	 * @return A condition for substring matching
	 */
	@NonNull SqlCondition contains(@NonNull String substring);
	
	/**
	 * Creates a condition checking if the column value ends with the specified suffix.<br>
	 *
	 * @param suffix The suffix to check
	 * @return A condition for suffix matching
	 */
	@NonNull SqlCondition endsWith(@NonNull String suffix);
	
	/**
	 * Creates a condition checking if the column value length is greater than the specified length.<br>
	 *
	 * @param length The minimum length
	 * @return A condition for length comparison
	 */
	@NonNull SqlCondition lengthGreaterThan(int length);
	
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
