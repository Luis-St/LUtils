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
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record SqlConcatFunction<T extends CharSequence>(
	@NonNull @Unmodifiable List<SqlExpression<T>> expressions,
	@NonNull Optional<String> separator,
	boolean distinct,
	boolean ordered
) implements SqlStringFunction<T> {
	
	public SqlConcatFunction {
		Objects.requireNonNull(expressions, "Sql expression list must not be null");
		Objects.requireNonNull(separator, "Separator must not be null");
		
		if (expressions.isEmpty()) {
			throw new IllegalArgumentException("Sql expression list must not be empty");
		}
		if (expressions.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql expression list must not contain null sql expressions");
		}
		if (separator.isPresent() && separator.get().isEmpty()) {
			throw new IllegalArgumentException("Separator must not be empty");
		}
		
		SqlType<T> type = expressions.getFirst().type();
		for (int i = 1; i < expressions.size(); i++) {
			if (!expressions.get(i).type().javaType().isAssignableFrom(type.javaType()) && !type.javaType().isAssignableFrom(expressions.get(i).type().javaType())) {
				throw new IllegalArgumentException("All expressions must have compatible types, mismatch at " + i + " with " + expressions.get(i).type() + " expected " + type);
			}
		}
		
		expressions = List.copyOf(expressions);
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.expressions.getFirst().type();
	}
}
