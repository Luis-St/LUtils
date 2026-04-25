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

package net.luis.utils.io.database.function.functions.string;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record SqlConcatFunction<T extends CharSequence>(
	@NonNull @Unmodifiable List<SqlExpression<T>> values,
	@NonNull Optional<String> separator,
	boolean distinct,
	boolean ordered
) implements SqlStringFunction<T> {
	
	public SqlConcatFunction {
		Objects.requireNonNull(values, "Sql values expression list must not be null");
		Objects.requireNonNull(separator, "Separator must not be null");
		
		if (values.isEmpty()) {
			throw new IllegalArgumentException("Sql values expression list must not be empty");
		}
		if (values.contains(null)) {
			throw new IllegalArgumentException("Sql values expression list must not contain null");
		}
		if (separator.isPresent() && separator.get().isEmpty()) {
			throw new IllegalArgumentException("Separator must not be empty");
		}
		values = List.copyOf(values);
	}
}
