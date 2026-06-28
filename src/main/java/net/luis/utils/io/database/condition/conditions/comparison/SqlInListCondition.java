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
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * A condition that checks whether a value is contained in the given list of options.<br>
 *
 * @author Luis-St
 *
 * @param value The expression to check
 * @param options The options the value is tested against
 */
public record SqlInListCondition(
	@NonNull SqlExpression<?> value,
	@NonNull @Unmodifiable List<SqlExpression<?>> options
) implements SqlComparisonCondition {
	
	/**
	 * Constructs a new in-list condition with the given value and options.<br>
	 * The options list is copied to ensure immutability.<br>
	 *
	 * @throws NullPointerException If the value expression or options list is null
	 * @throws IllegalArgumentException If the options list is empty or contains null values
	 */
	public SqlInListCondition {
		Objects.requireNonNull(value, "Sql value expression must not be null");
		Objects.requireNonNull(options, "Sql options list must not be null");
		
		if (options.isEmpty()) {
			throw new IllegalArgumentException("Sql options list must not be empty");
		}
		if (options.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql options list must not contain null values");
		}
		options = List.copyOf(options);
	}
}
