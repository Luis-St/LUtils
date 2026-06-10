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
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCoalesceFunction<T>(@NonNull @Unmodifiable List<SqlExpression<T>> expressions) implements SqlFunction<T> {
	
	public SqlCoalesceFunction {
		Objects.requireNonNull(expressions, "Sql expression list must not be null");
		
		if (expressions.isEmpty()) {
			throw new IllegalArgumentException("Sql expression list must not be empty");
		}
		if (expressions.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql expression list must not contain null sql expressions");
		}
		
		SqlType<T> type = expressions.getFirst().type();
		for (int i = 1; i < expressions.size(); i++) {
			if (!expressions.get(i).type().equals(type)) {
				throw new IllegalArgumentException("All expressions must have the same type, mismatch at " + i + " with " + expressions.get(i).type() + " expected " + type);
			}
		}
		
		expressions = List.copyOf(expressions);
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.expressions.getFirst().type();
	}
}
