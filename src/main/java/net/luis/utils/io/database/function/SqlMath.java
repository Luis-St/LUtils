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

	public static @NonNull Object abs(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object round(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object ceil(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object floor(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object mod(@NonNull SqlColumn<? extends Number> column, @NonNull Number divisor) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object power(@NonNull SqlColumn<? extends Number> column, @NonNull Number exponent) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object sqrt(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object random() {
		throw new UnsupportedOperationException();
	}
}
