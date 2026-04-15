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

package net.luis.utils.io.database.function.functions.temporal;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlMakeTimeFunction<T>(
	@NonNull SqlExpression<? extends Number> hour,
	@NonNull SqlExpression<? extends Number> minute,
	@NonNull SqlExpression<? extends Number> second,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	public SqlMakeTimeFunction {
		Objects.requireNonNull(hour, "Hour expression must not be null");
		Objects.requireNonNull(minute, "Minute expression must not be null");
		Objects.requireNonNull(second, "Second expression must not be null");
		Objects.requireNonNull(type, "Type must not be null");
	}
	
	@Override
	public @NonNull SqlExpression<T> as(@NonNull SqlAlias alias) {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<T> ascending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<T> descending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<T> nullsFirst() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<T> nullsLast() {
		return null;
	}
}
