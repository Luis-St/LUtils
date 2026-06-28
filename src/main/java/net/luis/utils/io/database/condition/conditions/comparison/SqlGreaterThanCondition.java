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
 * A condition that checks whether a value is greater than the given threshold.<br>
 * If {@code equalTo} is set, the condition also matches when the value equals the threshold.<br>
 *
 * @author Luis-St
 *
 * @param value The expression to check
 * @param threshold The threshold to compare against
 * @param equalTo Whether the comparison is inclusive of the threshold
 */
public record SqlGreaterThanCondition(
	@NonNull SqlExpression<?> value,
	@NonNull SqlExpression<?> threshold,
	boolean equalTo
) implements SqlComparisonCondition {
	
	/**
	 * Constructs a new greater-than condition with the given value, threshold and inclusiveness.<br>
	 * @throws NullPointerException If the value or threshold expression is null
	 */
	public SqlGreaterThanCondition {
		Objects.requireNonNull(value, "Sql value expression must not be null");
		Objects.requireNonNull(threshold, "Sql threshold expression must not be null");
	}
}
