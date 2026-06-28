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
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code TO_EPOCH} function.<br>
 * Converts a temporal value into epoch seconds.<br>
 *
 * @author Luis-St
 *
 * @param expression The temporal expression to convert into epoch seconds
 * @param type The sql type of the result
 * @param <T> The numeric result type of the function
 */

public record SqlToEpochFunction<T extends Number>(
	@NonNull SqlExpression<?> expression,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	/**
	 * Constructs a new to epoch function with the given expression and sql type.<br>
	 * @throws NullPointerException If the expression or sql type is null
	 */
	public SqlToEpochFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
	
	@Override
	public boolean requiresCast() {
		return true;
	}
}
