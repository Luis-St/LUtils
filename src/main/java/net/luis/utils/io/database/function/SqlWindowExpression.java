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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.function.window.SqlWindowClause;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL expression that can be used with a window specification via {@code OVER(...)}.<br>
 * Extends {@link SqlExpression} so it can also be used directly without a window clause.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the expression result
 */
public interface SqlWindowExpression<T> extends SqlExpression<T> {
	
	/**
	 * Applies the given window specification to this expression.<br>
	 * Generates SQL: {@code expression OVER(clause)}.<br>
	 *
	 * @param clause The window clause specification
	 * @return The windowed expression
	 */
	@NonNull SqlExpression<T> over(@NonNull SqlWindowClause clause);
	
	/**
	 * Applies an empty window specification to this expression.<br>
	 * Generates SQL: {@code expression OVER()}.<br>
	 *
	 * @return The windowed expression with an empty over clause
	 */
	@NonNull SqlExpression<T> over();
}
