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

package net.luis.utils.io.database.function.functions.window;

import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents an aggregate function evaluated as a window function.<br>
 * It wraps an existing aggregate function and applies it over the rows defined by the window clause.<br>
 *
 * @author Luis-St
 *
 * @param aggregate The aggregate function to evaluate over the window
 * @param over The window clause defining the partitioning and ordering
 * @param <T> The type of the resulting value
 */
public record SqlWindowedAggregate<T>(
	@NonNull SqlAggregateFunction<T> aggregate,
	@NonNull SqlWindowClause over
) implements SqlWindowFunction<T> {
	
	/**
	 * Constructs a new sql windowed aggregate with the given aggregate function and window clause.<br>
	 * @throws NullPointerException If the aggregate function or window clause is null
	 */
	public SqlWindowedAggregate {
		Objects.requireNonNull(aggregate, "Sql aggregate function must not be null");
		Objects.requireNonNull(over, "Sql window clause must not be null");
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.aggregate.type();
	}
}
