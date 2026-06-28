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

package net.luis.utils.io.database.function.functions.string;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlTypedNestedExpression;
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code REPLACE} function.<br>
 * Replaces all occurrences of the search string with the replacement string in the given string expression.<br>
 *
 * @author Luis-St
 *
 * @param expression The string expression to replace within
 * @param search The substring expression to search for
 * @param replacement The substring expression to replace the search string with
 * @param <T> The character sequence type of the expression
 */

public record SqlReplaceFunction<T extends CharSequence>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<? extends CharSequence> search,
	@NonNull SqlExpression<? extends CharSequence> replacement
) implements SqlStringFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new replace function with the given expression, search and replacement.<br>
	 * @throws NullPointerException If the expression, search expression or replacement expression is null
	 */
	public SqlReplaceFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(search, "Sql search expression must not be null");
		Objects.requireNonNull(replacement, "Sql replacement expression must not be null");
	}
}
