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

package net.luis.utils.io.database.expression;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
public final class SqlStringExpressions {
	
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull String prefix) {
		return new SqlStartsWithCondition(expression, SqlExpression.of(prefix));
	}
	
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> prefix) {
		return new SqlStartsWithCondition(expression, prefix);
	}
	
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull String substring) {
		return new SqlContainsCondition(expression, SqlExpression.of(substring));
	}
	
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring) {
		return new SqlContainsCondition(expression, substring);
	}
	
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull String suffix) {
		return new SqlEndsWithCondition(expression, SqlExpression.of(suffix));
	}
	
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> suffix) {
		return new SqlEndsWithCondition(expression, suffix);
	}
	
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull String pattern) {
		return new SqlLikeCondition(expression, SqlExpression.of(pattern));
	}
	
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> pattern) {
		return new SqlLikeCondition(expression, pattern);
	}
	
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull String value) {
		return new SqlEqualsIgnoreCaseCondition(expression, SqlExpression.of(value));
	}
	
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> value) {
		return new SqlEqualsIgnoreCaseCondition(expression, value);
	}
	
	public static @NonNull SqlExpression<String> lower(@NonNull SqlExpression<String> expression) {
		return new SqlLowerFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> upper(@NonNull SqlExpression<String> expression) {
		return new SqlUpperFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> trim(@NonNull SqlExpression<String> expression) {
		return new SqlTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> leftTrim(@NonNull SqlExpression<String> expression) {
		return new SqlLeftTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> rightTrim(@NonNull SqlExpression<String> expression) {
		return new SqlRightTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expression, @NonNull String characters) {
		return new SqlTrimCharsFunction<>(expression, SqlExpression.of(characters));
	}
	
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<String> expression) {
		return new SqlLengthFunction<>(expression, SqlTypes.INTEGER);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> length(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlLengthFunction<>(expression, type);
	}
	
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expression, int start, int length) {
		return new SqlSubstringFunction<>(expression, SqlExpression.of(start), SqlExpression.of(length));
	}
	
	public static @NonNull SqlExpression<String> concat(SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.empty(), false, false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull SqlExpression<String> concatWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull SqlExpression<String> concatDistinctWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), true, false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull SqlExpression<String> concatOrderedWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, true);
	}
	
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expression, @NonNull String search, @NonNull String replacement) {
		return new SqlReplaceFunction<>(expression, SqlExpression.of(search), SqlExpression.of(replacement));
	}
	
	public static @NonNull SqlExpression<Integer> position(@NonNull SqlExpression<String> expression, @NonNull String substring) {
		return new SqlPositionFunction<>(SqlExpression.of(substring), expression, SqlTypes.INTEGER);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> position(@NonNull SqlExpression<String> expression, @NonNull String substring, @NonNull SqlType<T> type) {
		return new SqlPositionFunction<>(SqlExpression.of(substring), expression, type);
	}
	
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expression, int n) {
		return new SqlLeftFunction<>(expression, SqlExpression.of(n));
	}
	
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expression, int n) {
		return new SqlRightFunction<>(expression, SqlExpression.of(n));
	}
	
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return new SqlLeftPadFunction<>(expression, SqlExpression.of(length), SqlExpression.of(fill));
	}
	
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return new SqlRightPadFunction<>(expression, SqlExpression.of(length), SqlExpression.of(fill));
	}
	
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<String> expression) {
		return new SqlHexFunction(expression);
	}
	
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<String> expression, @NonNull SqlType<String> type) {
		return new SqlHexFunction(expression, type);
	}
	
	public static @NonNull SqlExpression<byte[]> unhex(@NonNull SqlExpression<String> expression) {
		return new SqlUnhexFunction<>(expression, SqlTypes.LARGE_BYTES);
	}
	
	public static <T> @NonNull SqlExpression<T> unhex(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlUnhexFunction<>(expression, type);
	}
}
