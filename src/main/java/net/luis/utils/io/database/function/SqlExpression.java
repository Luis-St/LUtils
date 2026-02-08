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
	
	@NonNull SqlCondition equalTo(@NonNull T value);
	
	@NonNull SqlCondition notEqualTo(@NonNull T value);
	
	@NonNull SqlCondition greaterThan(@NonNull T value);
	
	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull T value);
	
	@NonNull SqlCondition lessThan(@NonNull T value);
	
	@NonNull SqlCondition lessThanOrEqualTo(@NonNull T value);
	
	@NonNull SqlCondition between(@NonNull T start, @NonNull T end);
	
	@NonNull SqlCondition isNull();
	
	@NonNull SqlCondition isNotNull();
	
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
