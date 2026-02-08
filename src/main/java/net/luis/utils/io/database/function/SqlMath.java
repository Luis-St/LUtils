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

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL math functions.<br>
 *
 * @author Luis-St
 */
public class SqlMath {

	public static @NonNull SqlExpression<Number> abs(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> round(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> ceil(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> floor(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> mod(@NonNull SqlColumn<? extends Number> column, @NonNull Number divisor) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> power(@NonNull SqlColumn<? extends Number> column, @NonNull Number exponent) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> sqrt(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull SqlExpression<Number> random() {
		throw new UnsupportedOperationException();
	}
}
