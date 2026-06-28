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

import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code DENSE_RANK} window function.<br>
 * It assigns a rank to each row within its window partition, without leaving gaps in the ranking after ties.<br>
 *
 * @author Luis-St
 *
 * @param over The window clause defining the partitioning and ordering
 * @param type The type of the resulting value
 * @param <T> The numeric type of the resulting value
 */
public record SqlDenseRankFunction<T extends Number>(
	@NonNull SqlWindowClause over,
	@NonNull SqlType<T> type
) implements SqlWindowFunction<T> {
	
	/**
	 * Constructs a new sql dense rank function with the given window clause and type.<br>
	 * @throws NullPointerException If the window clause or type is null
	 */
	public SqlDenseRankFunction {
		Objects.requireNonNull(over, "Sql window clause must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
