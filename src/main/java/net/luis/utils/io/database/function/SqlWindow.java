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

	public static @NonNull Object rowNumber() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object rank() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object denseRank() {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object ntile(int buckets) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object lag(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object lead(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object over(@NonNull SqlWindow window) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object partitionBy(SqlColumn<?> @NonNull ... columns) {
		throw new UnsupportedOperationException();
	}

	public static @NonNull Object orderBy(SqlOrderable@NonNull ... orderables) {
		throw new UnsupportedOperationException();
	}
}
