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
 * Represents the SQL {@code DATE_ADD} function.<br>
 * Adds a temporal interval to a temporal value.<br>
 *
 * @author Luis-St
 *
 * @param firstSummand The temporal expression to add the interval to
 * @param part The temporal part of the interval to add
 * @param secondSummand The expression providing the interval amount to add
 * @param type The sql type of the result
 * @param <T> The result type of the function
 */

public record SqlTemporalAddFunction<T>(
	@NonNull SqlExpression<?> firstSummand,
	@NonNull SqlTemporalPart part,
	@NonNull SqlExpression<?> secondSummand,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	/**
	 * Constructs a new temporal add function with the given summands, temporal part and sql type.<br>
	 * @throws NullPointerException If the first summand, temporal part, second summand or sql type is null
	 */
	public SqlTemporalAddFunction {
		Objects.requireNonNull(firstSummand, "Sql first summand expression must not be null");
		Objects.requireNonNull(part, "Sql temporal part must not be null");
		Objects.requireNonNull(secondSummand, "Sql second summand expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
