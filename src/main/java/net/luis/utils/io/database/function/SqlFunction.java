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
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for general SQL functions.<br>
 *
 * @author Luis-St
 */
public class SqlFunction {
	
	public static @NonNull SqlExpression<Object> coalesce(@NonNull SqlExpression<?>... values) {
		throw new UnsupportedOperationException();
	}
	
	public static <T> @NonNull SqlExpression<T> nullif(@NonNull SqlExpression<T> value1, @NonNull T value2) {
		throw new UnsupportedOperationException();
	}
	
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> value, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<Object> greatest(SqlExpression<?> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<Object> least(SqlExpression<?> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<Object> caseWhen(@NonNull SqlCondition condition, @NonNull Object thenValue, @NonNull Object elseValue) {
		throw new UnsupportedOperationException();
	}
	
	public static <T> @NonNull SqlExpression<T> of(@NonNull String functionName, @NonNull Class<T> resultType, SqlExpression<?> @NonNull ... args) {
		throw new UnsupportedOperationException();
	}
}
