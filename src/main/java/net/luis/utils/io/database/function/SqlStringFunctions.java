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
import net.luis.utils.io.database.condition.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlStringFunctions {
	
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull String prefix) {
		return null;
	}
	
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> prefix) {
		return null;
	}
	
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull String substring) {
		return null;
	}
	
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring) {
		return null;
	}
	
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull String suffix) {
		return null;
	}
	
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> suffix) {
		return null;
	}
	
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull String pattern) {
		return null;
	}
	
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> pattern) {
		return null;
	}
	
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull String value) {
		return null;
	}
	
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> value) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> lower(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> upper(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> trim(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> leftTrim(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> rightTrim(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expression, @NonNull String characters) {
		return null;
	}
	
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expression, int start, int length) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> concat(SqlExpression<?> @NonNull ... values) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> concatWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> concatDistinctWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> concatOrderedWithSeparator(@NonNull String separator, SqlExpression<?> @NonNull ... values) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expression, @NonNull String search, @NonNull String replacement) {
		return null;
	}
	
	public static @NonNull SqlExpression<Integer> position(@NonNull SqlExpression<String> expression, @NonNull String substring) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expression, int n) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expression, int n) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<String> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<String> unhex(@NonNull SqlExpression<String> expression) {
		return null;
	}
}
