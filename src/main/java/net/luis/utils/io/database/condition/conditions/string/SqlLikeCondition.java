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

package net.luis.utils.io.database.condition.conditions.string;

import net.luis.utils.io.database.condition.conditions.SqlStringCondition;
import net.luis.utils.io.database.expression.SqlExpression;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A condition that checks whether a string value matches the given sql {@code LIKE} pattern.<br>
 *
 * @author Luis-St
 *
 * @param value The expression to check
 * @param pattern The pattern to match against
 */
public record SqlLikeCondition(
	@NonNull SqlExpression<?> value,
	@NonNull SqlExpression<?> pattern
) implements SqlStringCondition {
	
	/**
	 * Constructs a new like condition with the given value and pattern expressions.<br>
	 * @throws NullPointerException If the value or pattern expression is null
	 */
	public SqlLikeCondition {
		Objects.requireNonNull(value, "Sql value expression must not be null");
		Objects.requireNonNull(pattern, "Sql pattern expression must not be null");
	}
}
