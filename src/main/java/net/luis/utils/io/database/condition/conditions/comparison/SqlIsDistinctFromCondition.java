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
 * A condition that checks whether two expressions are distinct from each other.<br>
 * Unlike a plain inequality, this comparison treats {@code null} values as comparable, so two nulls are not distinct.<br>
 *
 * @author Luis-St
 *
 * @param first The first expression to compare
 * @param second The second expression to compare
 */
public record SqlIsDistinctFromCondition(
	@NonNull SqlExpression<?> first,
	@NonNull SqlExpression<?> second
) implements SqlComparisonCondition {
	
	/**
	 * Constructs a new is-distinct-from condition with the given expressions.<br>
	 * @throws NullPointerException If the first or second expression is null
	 */
	public SqlIsDistinctFromCondition {
		Objects.requireNonNull(first, "Sql first expression must not be null");
		Objects.requireNonNull(second, "Sql second expression must not be null");
	}
}
