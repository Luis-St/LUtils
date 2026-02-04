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
import org.jspecify.annotations.Nullable;

/**
 * Static utility class for general SQL functions.<br>
 *
 * @author Luis-St
 */
public class SqlFunction {

	public static @NonNull Object coalesce(@NonNull Object... values) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object nullif(@NonNull Object value1, @Nullable Object value2) {
		throw new UnsupportedOperationException();
	}

	public static <T> @NonNull Object cast(@NonNull Object value, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object greatest(@NonNull Object... values) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object least(@NonNull Object... values) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object caseWhen(@NonNull SqlCondition condition, @NonNull Object thenValue, @NonNull Object elseValue) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object of(@NonNull String functionName, @NonNull Object... args) {
		throw new UnsupportedOperationException();
	}
}
