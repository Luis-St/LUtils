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
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.*;
import net.luis.utils.io.database.function.functions.generic.SqlCastFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlFunctionRenderer {
	
	private final SqlDialect dialect;
	private final SqlAggregateFunctionRenderer aggregateRenderer;
	private final SqlNumericFunctionRenderer numericRenderer;
	private final SqlStringFunctionRenderer stringRenderer;
	private final SqlTemporalFunctionRenderer temporalRenderer;
	private final SqlWindowFunctionRenderer windowRenderer;
	private final SqlGenericFunctionRenderer genericRenderer;
	
	public SqlFunctionRenderer(
		@NonNull SqlDialect dialect,
		@NonNull SqlAggregateFunctionRenderer aggregateRenderer,
		@NonNull SqlNumericFunctionRenderer numericRenderer,
		@NonNull SqlStringFunctionRenderer stringRenderer,
		@NonNull SqlTemporalFunctionRenderer temporalRenderer,
		@NonNull SqlWindowFunctionRenderer windowRenderer,
		@NonNull SqlGenericFunctionRenderer genericRenderer
	) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.aggregateRenderer = Objects.requireNonNull(aggregateRenderer, "Sql aggregate function renderer must not be null");
		this.numericRenderer = Objects.requireNonNull(numericRenderer, "Sql numeric function renderer must not be null");
		this.stringRenderer = Objects.requireNonNull(stringRenderer, "Sql string function renderer must not be null");
		this.temporalRenderer = Objects.requireNonNull(temporalRenderer, "Sql temporal function renderer must not be null");
		this.windowRenderer = Objects.requireNonNull(windowRenderer, "Sql window function renderer must not be null");
		this.genericRenderer = Objects.requireNonNull(genericRenderer, "Sql generic function renderer must not be null");
	}
	
	public @NonNull SqlRendered render(@NonNull SqlFunction<?> function) throws SqlException {
		if (function instanceof SqlCastFunction<?>(var value, var targetType)) {
			return SqlRenderer.empty().cast().openingBracket().rendered(value.toSql(this.dialect)).as().literal(this.dialect.getTypeName(targetType)).closingBracket().toSql();
		}
		
		SqlRendered rendered = switch (function) {
			case SqlAggregateFunction<?> func -> this.aggregateRenderer.render(func);
			case SqlNumericFunction<?> func -> this.numericRenderer.render(func);
			case SqlStringFunction<?> func -> this.stringRenderer.render(func);
			case SqlTemporalFunction<?> func -> this.temporalRenderer.render(func);
			case SqlWindowFunction<?> func -> this.windowRenderer.render(func);
			
			case null -> throw new NullPointerException("Sql function must not be null");
			default -> this.genericRenderer.render(function);
		};
		
		if (function.requiresCast()) {
			return SqlRenderingHelper.renderCast(this.dialect, rendered, function.type());
		}
		return rendered;
	}
	
}
