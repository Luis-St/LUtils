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
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.query.SqlSelectQuery;
import net.luis.utils.io.database.table.ops.*;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL column.<br>
 * Extends {@link SqlExpression} to support aliasing via {@link #as(String)} and usage in conditions and ordering.<br>
 * <p>
 *     Type-specific operations are accessed via {@link #string()}, {@link #numeric()}, and {@link #temporal()} accessors.<br>
 * </p>
 *
 * @see SqlExpression
 * @see SqlStringOps
 * @see SqlNumericOps
 * @see SqlTemporalOps
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface SqlColumn<T> extends SqlExpression<T> {

	/**
	 * Returns the name of this column.<br>
	 * @return The column name
	 */
	@NonNull String getName();

	/**
	 * Returns the table this column belongs to.<br>
	 * @return The owning table
	 */
	@NonNull SqlTable<?> getTable();

	/**
	 * Returns string-specific operations for this column.<br>
	 *
	 * @return The string operations holder
	 */
	@NonNull SqlStringOps string();
	
	/**
	 * Returns numeric-specific operations for this column.<br>
	 *
	 * @return The numeric operations holder
	 */
	@NonNull SqlNumericOps<T> numeric();
	
	/**
	 * Returns temporal-specific operations for this column.<br>
	 *
	 * @return The temporal operations holder
	 */
	@NonNull SqlTemporalOps temporal();
	
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
	 * Creates a NOT IN condition with a subquery.<br>
	 * Generates SQL: {@code column NOT IN (subquery)}.<br>
	 *
	 * @param subquery The subquery providing values to exclude
	 * @return A condition for NOT IN comparison with subquery
	 */
	@NonNull SqlCondition notIn(@NonNull SqlSelectQuery<?> subquery);
	
	/**
	 * Creates an {@code IFNULL} expression that returns the column value or a default if null.<br>
	 * Generates SQL: {@code IFNULL(column, defaultValue)} if supported by the dialect, otherwise uses a {@code COALESCE(column, defaultValue)}.<br>
	 *
	 * @param defaultValue The default value to use when the column is null
	 * @return The if-null expression of the same type as the column
	 */
	@NonNull SqlExpression<T> ifNull(@NonNull T defaultValue);
	
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
