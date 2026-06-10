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
 *
 * @author Luis-St
 *
 */

public record SqlAnyOfCondition(@NonNull @Unmodifiable List<SqlCondition> conditions) implements SqlCondition {
	
	public SqlAnyOfCondition {
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
		return SqlCondition.allOf(this.conditions.stream().map(SqlCondition::not).toList()); // De Morgan's law
	}
}
