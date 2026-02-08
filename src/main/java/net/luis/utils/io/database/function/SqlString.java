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
	
	public static @NonNull SqlExpression<String> lower(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<String> upper(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<String> trim(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<String> substring(@NonNull SqlColumn<String> column, int start, int length) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<String> concat(SqlExpression<?> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	public static @NonNull SqlExpression<String> replace(@NonNull SqlColumn<String> column, @NonNull String search, @NonNull String replacement) {
		throw new UnsupportedOperationException();
	}
}
