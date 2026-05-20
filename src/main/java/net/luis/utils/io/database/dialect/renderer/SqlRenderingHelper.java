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
 *
 * @author Luis-St
 *
 */

public final class SqlRenderingHelper {
	
	private SqlRenderingHelper() {}
	
	public static @NonNull SqlRendered renderInfix(@NonNull SqlDialect dialect, @NonNull SqlExpression<?> first, @NonNull String operator, @NonNull SqlExpression<?> second) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(first, "First operand must not be null");
		Objects.requireNonNull(operator, "Operator must not be null");
		Objects.requireNonNull(second, "Second operand must not be null");
		return SqlRenderer.empty().rendered(first.toSql(dialect)).literal(operator).rendered(second.toSql(dialect)).toSql();
	}
	
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
	
	public static <T> void renderList(@NonNull SqlRenderer renderer, @NonNull List<T> values, @NonNull ThrowableBiConsumer<SqlRenderer, T, SqlException> itemRenderer) throws SqlException {
		Objects.requireNonNull(values, "Values list must not be null");
		Objects.requireNonNull(itemRenderer, "Item renderer function must not be null");
		
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			itemRenderer.accept(renderer, values.get(i));
		}
	}
	
	public static @NonNull SqlRendered renderCast(@NonNull SqlDialect dialect, @NonNull SqlRendered inner, @NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(inner, "Inner rendered must not be null");
		Objects.requireNonNull(type, "Target type must not be null");
		
		return SqlRenderer.empty().cast().openingBracket().rendered(inner).as().literal(dialect.getTypeName(type)).closingBracket().toSql();
	}
}
