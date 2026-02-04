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
 * Static utility class for SQL string functions.<br>
 *
 * @author Luis-St
 */
public class SqlString {

	public static @NonNull Object lower(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object upper(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object trim(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object length(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object substring(@NonNull SqlColumn<String> column, int start, int length) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object concat(@NonNull Object... values) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object replace(@NonNull SqlColumn<String> column, @NonNull String search, @NonNull String replacement) {
		throw new UnsupportedOperationException();
	}
}
