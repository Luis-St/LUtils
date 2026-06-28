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

import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * Represents the SQL {@code DATE_TRUNC} function.<br>
 * Truncates a temporal value to the given temporal part.<br>
 *
 * @author Luis-St
 *
 * @param expression The temporal expression to truncate
 * @param part The temporal part to truncate to
 * @param type The sql type of the result
 * @param <T> The temporal result type of the function
 */

public record SqlTemporalTruncateFunction<T extends Temporal>(
	@NonNull SqlExpression<?> expression,
	@NonNull SqlTemporalPart part,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	/**
	 * Constructs a new temporal truncate function with the given expression, temporal part and sql type.<br>
	 * @throws NullPointerException If the expression, temporal part or sql type is null
	 */
	public SqlTemporalTruncateFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(part, "Sql temporal part must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
