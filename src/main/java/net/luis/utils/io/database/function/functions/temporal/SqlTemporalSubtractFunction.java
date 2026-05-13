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
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlTemporalSubtractFunction<T>(
	@NonNull SqlExpression<?> minuend,
	@NonNull SqlTemporalPart part,
	@NonNull SqlExpression<?> subtrahend,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	public SqlTemporalSubtractFunction {
		Objects.requireNonNull(minuend, "Sql minuend expression must not be null");
		Objects.requireNonNull(part, "Sql temporal part must not be null");
		Objects.requireNonNull(subtrahend, "Sql subtrahend expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
