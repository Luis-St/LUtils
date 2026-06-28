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

package net.luis.utils.io.database.condition;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * A condition that is the logical conjunction of at least two conditions.<br>
 * It evaluates to {@code true} only if all the wrapped conditions are true.<br>
 *
 * @author Luis-St
 *
 * @param conditions The conditions to combine
 */
public record SqlAllOfCondition(@NonNull @Unmodifiable List<SqlCondition> conditions) implements SqlCondition {
	
	/**
	 * Constructs a new all-of condition with the given conditions.<br>
	 * The conditions list is copied to ensure immutability.<br>
	 *
	 * @throws NullPointerException If the conditions list is null
	 * @throws IllegalArgumentException If the conditions list is empty, contains fewer than two conditions or contains null conditions
	 */
	public SqlAllOfCondition {
		Objects.requireNonNull(conditions, "Sql conditions list must not be null");
		
		if (conditions.isEmpty()) {
			throw new IllegalArgumentException("Sql conditions list must not be empty");
		}
		if (conditions.size() == 1) {
			throw new IllegalArgumentException("Sql conditions list must contain at least 2 conditions");
		}
		if (conditions.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql conditions list must not contain null conditions");
		}
		conditions = List.copyOf(conditions);
	}
	
	@Override
	public @NonNull SqlCondition not() {
		return SqlCondition.anyOf(this.conditions.stream().map(SqlCondition::not).toList()); // De Morgan's law
	}
}
