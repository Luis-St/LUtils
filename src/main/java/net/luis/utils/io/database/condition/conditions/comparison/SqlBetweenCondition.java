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

package net.luis.utils.io.database.condition.conditions.comparison;

import net.luis.utils.io.database.condition.conditions.SqlComparisonCondition;
import net.luis.utils.io.database.expression.SqlExpression;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A condition that checks whether a value lies between the given lower and upper bounds, inclusive.<br>
 *
 * @author Luis-St
 *
 * @param value The expression to check
 * @param lower The lower bound of the range
 * @param upper The upper bound of the range
 */
public record SqlBetweenCondition(
	@NonNull SqlExpression<?> value,
	@NonNull SqlExpression<?> lower,
	@NonNull SqlExpression<?> upper
) implements SqlComparisonCondition {
	
	/**
	 * Constructs a new between condition with the given value, lower and upper bound expressions.<br>
	 * @throws NullPointerException If the value, lower or upper bound expression is null
	 */
	public SqlBetweenCondition {
		Objects.requireNonNull(value, "Sql value expression must not be null");
		Objects.requireNonNull(lower, "Sql lower bound expression must not be null");
		Objects.requireNonNull(upper, "Sql upper bound expression must not be null");
	}
}
