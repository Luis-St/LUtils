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

package net.luis.utils.io.database.util;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.expression.SqlExpression;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single {@code WHEN ... THEN ...} branch of a sql {@code CASE} expression.<br>
 * The condition is the {@code WHEN} predicate and the expression is the {@code THEN} result that is yielded when the condition is true.<br>
 *
 * @see SqlCondition
 * @see SqlExpression
 *
 * @author Luis-St
 *
 * @param <T> The type of the value the branch expression evaluates to
 */
public record SqlCaseWhenBranch<T>(
	@NonNull SqlCondition condition,
	@NonNull SqlExpression<T> expression
) {
	
	/**
	 * Constructs a new sql case-when branch with the given condition and expression.<br>
	 *
	 * @param condition The condition that acts as the {@code WHEN} predicate of the branch
	 * @param expression The expression that acts as the {@code THEN} result of the branch
	 * @throws NullPointerException If the condition or the expression is null
	 */
	public SqlCaseWhenBranch {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		Objects.requireNonNull(expression, "Sql expression must not be null");
	}
}
