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

import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL window functions.<br>
 *
 * @author Luis-St
 */
public class SqlWindow {

	public static @NonNull SqlExpression<Long> rowNumber() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Long> rank() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Long> denseRank() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Long> ntile(int buckets) {
		throw new UnsupportedOperationException();
	}

	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}

	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<?> over(@NonNull SqlExpression<?> expression) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<?> partitionBy(SqlColumn<?> @NonNull ... columns) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<?> orderBy(SqlOrderable @NonNull ... orderables) {
		throw new UnsupportedOperationException();
	}
}
