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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A condition that negates the wrapped condition.<br>
 * Negating this condition again yields the wrapped condition.<br>
 *
 * @author Luis-St
 *
 * @param condition The condition to negate
 */
public record SqlNegatedCondition(@NonNull SqlCondition condition) implements SqlCondition {
	
	/**
	 * Constructs a new negated condition wrapping the given condition.<br>
	 * @throws NullPointerException If the condition is null
	 */
	public SqlNegatedCondition {
		Objects.requireNonNull(condition, "Sql condition must not be null");
	}
	
	@Override
	public @NonNull SqlCondition not() {
		return this.condition;
	}
}
