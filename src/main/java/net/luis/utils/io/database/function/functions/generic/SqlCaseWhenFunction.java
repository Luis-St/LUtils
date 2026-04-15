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

package net.luis.utils.io.database.function.functions.generic;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCaseWhenFunction<T>(
	@NonNull List<SqlCaseWhenBranch<T>> branches,
	@Nullable SqlExpression<T> elseValue
) implements SqlFunction<T> {
	
	public SqlCaseWhenFunction {
		Objects.requireNonNull(branches, "Branches list must not be null");
		
		if (branches.isEmpty()) {
			throw new IllegalArgumentException("Branches list must not be empty");
		}
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
