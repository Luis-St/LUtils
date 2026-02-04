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
 * Static utility class for SQL date functions.<br>
 *
 * @author Luis-St
 */
public class SqlDate {

	public static @NonNull Object now() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object currentDate() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object year(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object month(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object day(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object dateTrunc(@NonNull SqlColumn<?> column, @NonNull String unit) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object addDays(@NonNull SqlColumn<?> column, int days) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object dateDiff(@NonNull SqlColumn<?> column1, @NonNull SqlColumn<?> column2) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object toChar(@NonNull SqlColumn<?> column, @NonNull String format) {
		throw new UnsupportedOperationException();
	}
}
