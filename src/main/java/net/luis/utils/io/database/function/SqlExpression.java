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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL expression that can be used in selects, conditions, and ordering.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the expression result
 */
public interface SqlExpression<T> extends SqlOrderable {
	
	/**
	 * Creates a condition that checks if this expression is equal to the given value.<br>
	 * Generates SQL: {@code column = value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The equality condition
	 */
	@NonNull SqlCondition equalTo(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is not equal to the given value.<br>
	 * Generates SQL: {@code column <> value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The inequality condition
	 */
	@NonNull SqlCondition notEqualTo(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is greater than the given value.<br>
	 * Generates SQL: {@code column > value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The greater-than condition
	 */
	@NonNull SqlCondition greaterThan(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is greater than or equal to the given value.<br>
	 * Generates SQL: {@code column >= value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The greater-than-or-equal condition
	 */
	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is less than the given value.<br>
	 * Generates SQL: {@code column < value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The less-than condition
	 */
	@NonNull SqlCondition lessThan(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is less than or equal to the given value.<br>
	 * Generates SQL: {@code column <= value}.<br>
	 *
	 * @param value The value to compare to
	 * @return The less-than-or-equal condition
	 */
	@NonNull SqlCondition lessThanOrEqualTo(@NonNull T value);
	
	/**
	 * Creates a condition that checks if this expression is between the given values.<br>
	 * Generates SQL: {@code column BETWEEN start AND end}.<br>
	 *
	 * @param start The start value of the range
	 * @param end The end value of the range
	 * @return The between condition
	 */
	@NonNull SqlCondition between(@NonNull T start, @NonNull T end);
	
	/**
	 * Creates a condition that checks if this expression is null.<br>
	 * Generates SQL: {@code column IS NULL}.<br>
	 *
	 * @return The is-null condition
	 */
	@NonNull SqlCondition isNull();
	
	/**
	 * Creates a condition that checks if this expression is not null.<br>
	 * Generates SQL: {@code column IS NOT NULL}.<br>
	 *
	 * @return The is-not-null condition
	 */
	@NonNull SqlCondition isNotNull();
	
	/**
	 * Aliases this expression with the given name.<br>
	 * Generates SQL: {@code expression AS alias}.<br>
	 *
	 * @param alias The alias name
	 * @return This expression with the alias applied
	 */
	@NonNull SqlExpression<T> as(@NonNull String alias);
	
	@Override
	@NonNull SqlExpression<T> asc();
	
	@Override
	@NonNull SqlExpression<T> desc();
	
	@Override
	@NonNull SqlExpression<T> nullsFirst();
	
	@Override
	@NonNull SqlExpression<T> nullsLast();
}
