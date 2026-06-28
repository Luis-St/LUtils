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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.function.throwable.ThrowableBiConsumer;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Provides shared static helper methods for rendering sql.<br>
 * The helpers cover recurring rendering patterns such as infix operators, function calls,
 * comma-separated lists and casts, all producing dialect-specific {@link SqlRendered} output.<br>
 *
 * @author Luis-St
 */

public final class SqlRenderingHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static utility class.<br>
	 */
	private SqlRenderingHelper() {}
	
	/**
	 * Renders an infix expression with the given operator between the two operands.<br>
	 *
	 * @param dialect The sql dialect used to render the operands
	 * @param first The first operand
	 * @param operator The infix operator placed between the operands
	 * @param second The second operand
	 * @return The rendered infix expression
	 * @throws NullPointerException If the dialect, first operand, operator or second operand is null
	 * @throws SqlException If rendering fails
	 */
	public static @NonNull SqlRendered renderInfix(@NonNull SqlDialect dialect, @NonNull SqlExpression<?> first, @NonNull String operator, @NonNull SqlExpression<?> second) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(first, "First operand must not be null");
		Objects.requireNonNull(operator, "Operator must not be null");
		Objects.requireNonNull(second, "Second operand must not be null");
		return SqlRenderer.empty().rendered(first.toSql(dialect)).literal(operator).rendered(second.toSql(dialect)).toSql();
	}
	
	/**
	 * Renders a function call with the given name and a comma-separated list of argument expressions.<br>
	 *
	 * @param dialect The sql dialect used to render the arguments
	 * @param functionName The name of the function to call
	 * @param arguments The argument expressions passed to the function
	 * @return The rendered function call
	 * @throws NullPointerException If the dialect, function name or arguments is null
	 * @throws SqlException If rendering fails
	 */
	public static @NonNull SqlRendered renderFunctionCall(@NonNull SqlDialect dialect, @NonNull String functionName, SqlExpression<?> @NonNull ... arguments) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(functionName, "Function name must not be null");
		Objects.requireNonNull(arguments, "Arguments must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(functionName).openingBracket();
		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(arguments[i].toSql(dialect));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders a function call with the given name and a comma-separated list of value expressions.<br>
	 *
	 * @param dialect The sql dialect used to render the values
	 * @param functionName The name of the function to call
	 * @param values The value expressions passed to the function
	 * @return The rendered function call
	 * @throws NullPointerException If the dialect, function name or values is null
	 * @throws SqlException If rendering fails
	 */
	public static @NonNull SqlRendered renderFunctionCallWithList(@NonNull SqlDialect dialect, @NonNull String functionName, @NonNull List<? extends SqlExpression<?>> values) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(functionName, "Function name must not be null");
		Objects.requireNonNull(values, "Values list must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(functionName).openingBracket();
		renderList(renderer, values, (r, item) -> r.rendered(item.toSql(dialect)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given values into the renderer as a comma-separated list.<br>
	 * Each value is rendered using the supplied item renderer, with a comma inserted between consecutive values.<br>
	 *
	 * @param renderer The renderer to append the list to
	 * @param values The values to render as a list
	 * @param itemRenderer The function used to render a single value
	 * @param <T> The type of the values to render
	 * @throws NullPointerException If the renderer, values or item renderer is null
	 * @throws SqlException If rendering fails
	 */
	public static <T> void renderList(@NonNull SqlRenderer renderer, @NonNull List<T> values, @NonNull ThrowableBiConsumer<SqlRenderer, T, SqlException> itemRenderer) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(values, "Values list must not be null");
		Objects.requireNonNull(itemRenderer, "Item renderer function must not be null");
		
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			itemRenderer.accept(renderer, values.get(i));
		}
	}
	
	/**
	 * Renders a cast of the given inner rendered sql to the target type.<br>
	 *
	 * @param dialect The sql dialect used to resolve the cast type name
	 * @param inner The already rendered inner sql to cast
	 * @param type The target type to cast the inner sql to
	 * @return The rendered cast expression
	 * @throws NullPointerException If the dialect, inner rendered or type is null
	 * @throws SqlException If rendering fails
	 */
	public static @NonNull SqlRendered renderCast(@NonNull SqlDialect dialect, @NonNull SqlRendered inner, @NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(inner, "Inner rendered must not be null");
		Objects.requireNonNull(type, "Target type must not be null");
		
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal(dialect.getCastTypeName(type)).closingBracket().toSql();
	}
}
