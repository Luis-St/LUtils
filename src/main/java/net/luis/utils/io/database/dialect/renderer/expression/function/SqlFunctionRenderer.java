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
 * Renderer for sql functions into dialect-specific sql.<br>
 * Acts as the dispatcher that routes the given {@link SqlFunction} to the matching category renderer<br>
 * based on its concrete type and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlFunctionRenderer {
	
	/**
	 * The sql dialect used to render the functions.
	 */
	private final SqlDialect dialect;
	/**
	 * The renderer used for aggregate functions.
	 */
	private final SqlAggregateFunctionRenderer aggregateRenderer;
	/**
	 * The renderer used for numeric functions.
	 */
	private final SqlNumericFunctionRenderer numericRenderer;
	/**
	 * The renderer used for string functions.
	 */
	private final SqlStringFunctionRenderer stringRenderer;
	/**
	 * The renderer used for temporal functions.
	 */
	private final SqlTemporalFunctionRenderer temporalRenderer;
	/**
	 * The renderer used for window functions.
	 */
	private final SqlWindowFunctionRenderer windowRenderer;
	/**
	 * The renderer used for generic functions.
	 */
	private final SqlGenericFunctionRenderer genericRenderer;
	
	/**
	 * Constructs a new sql function renderer for the given dialect and category renderers.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @param aggregateRenderer The renderer used for aggregate functions
	 * @param numericRenderer The renderer used for numeric functions
	 * @param stringRenderer The renderer used for string functions
	 * @param temporalRenderer The renderer used for temporal functions
	 * @param windowRenderer The renderer used for window functions
	 * @param genericRenderer The renderer used for generic functions
	 * @throws NullPointerException If any of the given arguments is null
	 */
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
	
	/**
	 * Renders the given sql function into dialect-specific sql.<br>
	 * Cast functions are rendered directly, all other functions are dispatched to the matching category renderer<br>
	 * based on their concrete type, and an implicit cast is applied afterwards if the function requires one.<br>
	 *
	 * @param function The sql function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlFunction<?> function) throws SqlException {
		if (function instanceof SqlCastFunction<?>(var value, var targetType)) {
			return SqlRenderer.empty().cast().openingBracket().rendered(value.toSql(this.dialect)).as().literal(this.dialect.getCastTypeName(targetType)).closingBracket().toSql();
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
