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

package net.luis.utils.io.database.dialect.renderer.expression.function;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Renderer for aggregate sql functions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlAggregateFunction} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlAggregateFunctionRenderer {
	
	/**
	 * The sql dialect used to render the functions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new aggregate sql function renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlAggregateFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Checks whether the given sql type maps to an integral java type.<br>
	 *
	 * @param type The sql type to check
	 * @return True if the type maps to an integral java type, otherwise false
	 */
	private static boolean isIntegralType(@NonNull SqlType<?> type) {
		Class<?> javaType = type.javaType();
		return javaType == Integer.class || javaType == Long.class || javaType == Short.class || javaType == Byte.class || javaType == BigInteger.class;
	}
	
	/**
	 * Renders the given aggregate sql function into dialect-specific sql.<br>
	 * The function is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param function The aggregate function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlAggregateFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlAverageFunction<?> func -> this.renderAverage(func);
			case SqlCountFunction func -> this.renderCount(func);
			case SqlCountDistinctFunction func -> this.renderCountDistinct(func);
			case SqlMaxFunction<?> func -> this.renderMax(func);
			case SqlMinFunction<?> func -> this.renderMin(func);
			case SqlSumFunction<?> func -> this.renderSum(func);
			
			case null -> throw new NullPointerException("Sql aggregate function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql aggregate function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given average function into dialect-specific sql.<br>
	 * Integral expressions are wrapped in a {@code ROUND(AVG(... * 1.0), 0)} call to force floating-point division,<br>
	 * all other expressions are rendered as a plain {@code AVG} call.<br>
	 *
	 * @param function The average function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderAverage(@NonNull SqlAverageFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (isIntegralType(function.expression().type())) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.literal("ROUND").openingBracket();
			renderer.literal("AVG").openingBracket().rendered(function.expression().toSql(this.dialect)).literal("*").literal("1.0").closingBracket();
			renderer.comma().literal("0").closingBracket();
			return renderer.toSql();
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "AVG", function.expression());
	}
	
	/**
	 * Renders the given count function into dialect-specific sql.<br>
	 * The function is rendered as a {@code COUNT} call over the expression, or as {@code COUNT(*)} if no expression is set.<br>
	 *
	 * @param function The count function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCount(@NonNull SqlCountFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (function.expression() != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "COUNT", function.expression());
		}
		return SqlRendered.of("COUNT(*)");
	}
	
	/**
	 * Renders the given count-distinct function into dialect-specific sql.<br>
	 * The function is rendered as a {@code COUNT(DISTINCT ...)} call over the expression.<br>
	 *
	 * @param function The count-distinct function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCountDistinct(@NonNull SqlCountDistinctFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("COUNT").openingBracket().distinct();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given max function into dialect-specific sql.<br>
	 * The function is rendered as a {@code MAX} call over the expression.<br>
	 *
	 * @param function The max function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderMax(@NonNull SqlMaxFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAX", function.expression());
	}
	
	/**
	 * Renders the given min function into dialect-specific sql.<br>
	 * The function is rendered as a {@code MIN} call over the expression.<br>
	 *
	 * @param function The min function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderMin(@NonNull SqlMinFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MIN", function.expression());
	}
	
	/**
	 * Renders the given sum function into dialect-specific sql.<br>
	 * The function is rendered as a {@code SUM} call over the expression.<br>
	 *
	 * @param function The sum function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderSum(@NonNull SqlSumFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SUM", function.expression());
	}
}
