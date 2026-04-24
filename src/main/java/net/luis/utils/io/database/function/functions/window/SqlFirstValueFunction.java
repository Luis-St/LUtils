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

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlFirstValueFunction<T>(
	@NonNull SqlExpression<T> column,
	@NonNull SqlWindowClause over
) implements SqlWindowFunction<T> {
	
	public SqlFirstValueFunction {
		Objects.requireNonNull(column, "Sql Sql column expression must not be null");
		Objects.requireNonNull(over, "Sql window clause must not be null");
	}
}
