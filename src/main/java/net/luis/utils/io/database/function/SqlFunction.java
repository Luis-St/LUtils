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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a sql function call that can be rendered into a sql fragment.<br>
 * Functions are a kind of {@link SqlExpression} and are grouped by category through dedicated marker sub-interfaces.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value the function evaluates to
 */
@FunctionalInterface
public interface SqlFunction<T> extends SqlExpression<T> {
	
	/**
	 * Returns whether the result of this function requires an explicit cast when used in a sql statement.<br>
	 * @return {@code true} if a cast is required, {@code false} otherwise
	 */
	default boolean requiresCast() {
		return false;
	}
	
	@Override
	default @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderFunction(this);
	}
}
