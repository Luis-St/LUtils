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

package net.luis.utils.io.database.function.functions.generic;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents the SQL {@code CASE WHEN} function.<br>
 * It evaluates a list of conditional branches in order and returns the value of the first matching branch,<br>
 * or the else value if no branch matches.<br>
 *
 * @author Luis-St
 *
 * @param branches The list of conditional branches to evaluate in order
 * @param elseValue The value to return if no branch matches, may be null
 * @param <T> The type of the resulting value
 */
public record SqlCaseWhenFunction<T>(
	@NonNull @Unmodifiable List<SqlCaseWhenBranch<T>> branches,
	@Nullable SqlExpression<T> elseValue
) implements SqlFunction<T> {
	
	/**
	 * Constructs a new sql case when function with the given branches and else value.<br>
	 * The branches list is copied to ensure immutability.<br>
	 *
	 * @throws NullPointerException If the branches list is null
	 * @throws IllegalArgumentException If the branches list is empty, contains null branches, the branches have differing types or the else value type does not match the branch type
	 */
	public SqlCaseWhenFunction {
		Objects.requireNonNull(branches, "Sql branches list must not be null");
		
		if (branches.isEmpty()) {
			throw new IllegalArgumentException("Sql branches list must not be empty");
		}
		if (branches.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql branches list must not contain null sql case when branches");
		}
		
		SqlType<T> type = branches.getFirst().expression().type();
		for (int i = 1; i < branches.size(); i++) {
			if (!branches.get(i).expression().type().equals(type)) {
				throw new IllegalArgumentException("All sql case when branches must have the same type, mismatch at " + i + " with " + branches.get(i).expression().type() + " expected " + type);
			}
		}
		
		if (elseValue != null && !elseValue.type().equals(branches.getFirst().expression().type())) {
			throw new IllegalArgumentException("Sql else value type " + elseValue.type() + " does not match branch type " + branches.getFirst().expression().type());
		}
		
		branches = List.copyOf(branches);
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.branches.getFirst().expression().type();
	}
}
