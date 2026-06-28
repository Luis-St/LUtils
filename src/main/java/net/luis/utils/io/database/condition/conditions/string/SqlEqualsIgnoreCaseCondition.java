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
 * A condition that checks whether two string expressions are equal, ignoring case.<br>
 *
 * @author Luis-St
 *
 * @param first The first expression to compare
 * @param second The second expression to compare
 */
public record SqlEqualsIgnoreCaseCondition(
	@NonNull SqlExpression<?> first,
	@NonNull SqlExpression<?> second
) implements SqlStringCondition {
	
	/**
	 * Constructs a new case-insensitive equals condition with the given expressions.<br>
	 * @throws NullPointerException If the first or second expression is null
	 */
	public SqlEqualsIgnoreCaseCondition {
		Objects.requireNonNull(first, "Sql first expression must not be null");
		Objects.requireNonNull(second, "Sql second expression must not be null");
	}
}
