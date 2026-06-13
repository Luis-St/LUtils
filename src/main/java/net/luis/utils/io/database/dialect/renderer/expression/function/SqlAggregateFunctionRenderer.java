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
 *
 * @author Luis-St
 *
 */

public class SqlAggregateFunctionRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlAggregateFunctionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
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
	
	private static boolean isIntegralType(@NonNull SqlType<?> type) {
		Class<?> javaType = type.javaType();
		return javaType == Integer.class || javaType == Long.class || javaType == Short.class || javaType == Byte.class || javaType == BigInteger.class;
	}
	
	protected @NonNull SqlRendered renderCount(@NonNull SqlCountFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (function.expression() != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "COUNT", function.expression());
		}
		return SqlRendered.of("COUNT(*)");
	}
	
	protected @NonNull SqlRendered renderCountDistinct(@NonNull SqlCountDistinctFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("COUNT").openingBracket().distinct();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderMax(@NonNull SqlMaxFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAX", function.expression());
	}
	
	protected @NonNull SqlRendered renderMin(@NonNull SqlMinFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MIN", function.expression());
	}
	
	protected @NonNull SqlRendered renderSum(@NonNull SqlSumFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "SUM", function.expression());
	}
}
