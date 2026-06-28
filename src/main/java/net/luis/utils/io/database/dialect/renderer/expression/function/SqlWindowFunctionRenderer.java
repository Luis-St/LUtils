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

import com.google.common.collect.Lists;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer for window sql functions into dialect-specific sql.<br>
 * Dispatches the given {@link SqlWindowFunction} to the matching render method based on its concrete type<br>
 * and produces the resulting {@link SqlRendered} using the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlWindowFunctionRenderer {
	
	/**
	 * The renderer used for the aggregate of windowed aggregate functions.
	 */
	private final SqlAggregateFunctionRenderer aggregateRenderer;
	/**
	 * The sql dialect used to render the functions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new window sql function renderer for the given dialect and aggregate renderer.<br>
	 *
	 * @param dialect The sql dialect used to render the functions
	 * @param aggregateRenderer The renderer used for the aggregate of windowed aggregate functions
	 * @throws NullPointerException If the dialect or the aggregate renderer is null
	 */
	public SqlWindowFunctionRenderer(@NonNull SqlDialect dialect, @NonNull SqlAggregateFunctionRenderer aggregateRenderer) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.aggregateRenderer = Objects.requireNonNull(aggregateRenderer, "Sql aggregate function renderer must not be null");
	}
	
	/**
	 * Renders the given window sql function into dialect-specific sql.<br>
	 * The function is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param function The window function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlWindowFunction<?> function) throws SqlException {
		return switch (function) {
			case SqlCumulativeDistributionFunction<?> func -> this.renderCumulativeDistribution(func);
			case SqlDenseRankFunction<?> func -> this.renderDenseRank(func);
			case SqlFirstValueFunction<?> func -> this.renderFirstValue(func);
			case SqlLagFunction<?> func -> this.renderLag(func);
			case SqlLastValueFunction<?> func -> this.renderLastValue(func);
			case SqlLeadFunction<?> func -> this.renderLead(func);
			case SqlPercentRankFunction<?> func -> this.renderPercentRank(func);
			case SqlRankFunction<?> func -> this.renderRank(func);
			case SqlRowNumberFunction<?> func -> this.renderRowNumber(func);
			case SqlTileBucketFunction<?> func -> this.renderTileBucket(func);
			case SqlValueAtFunction<?> func -> this.renderValueAt(func);
			case SqlWindowedAggregate<?> func -> this.renderWindowedAggregate(func);
			
			case null -> throw new NullPointerException("Sql window function must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql window function type: " + function.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders a window function call into dialect-specific sql.<br>
	 * The function name is rendered with the non-null arguments as a comma-separated list,<br>
	 * followed by an {@code OVER (...)} clause built from the given window clause.<br>
	 *
	 * @param functionName The name of the window function
	 * @param over The window clause rendered into the over clause
	 * @param arguments The function arguments, null entries are skipped
	 * @return The rendered sql
	 * @throws NullPointerException If the function name, the window clause or the arguments array is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderWindowCall(@NonNull String functionName, @NonNull SqlWindowClause over, SqlExpression<?> @NonNull ... arguments) throws SqlException {
		Objects.requireNonNull(functionName, "Sql function name must not be null");
		Objects.requireNonNull(over, "Sql window clause must not be null");
		Objects.requireNonNull(arguments, "Function arguments must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(functionName).openingBracket();
		SqlExpression<?>[] filteredArgs = Lists.newArrayList(arguments).stream().filter(Objects::nonNull).toArray(SqlExpression[]::new);
		for (int i = 0; i < filteredArgs.length; i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(filteredArgs[i].toSql(this.dialect));
		}
		renderer.closingBracket().over().openingBracket().rendered(over.toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given cumulative-distribution function into dialect-specific sql.<br>
	 * The function is rendered as a {@code CUME_DIST()} window call over its window clause.<br>
	 *
	 * @param function The cumulative-distribution function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderCumulativeDistribution(@NonNull SqlCumulativeDistributionFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("CUME_DIST", function.over());
	}
	
	/**
	 * Renders the given dense-rank function into dialect-specific sql.<br>
	 * The function is rendered as a {@code DENSE_RANK()} window call over its window clause.<br>
	 *
	 * @param function The dense-rank function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderDenseRank(@NonNull SqlDenseRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("DENSE_RANK", function.over());
	}
	
	/**
	 * Renders the given first-value function into dialect-specific sql.<br>
	 * The function is rendered as a {@code FIRST_VALUE(column)} window call over its window clause.<br>
	 *
	 * @param function The first-value function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderFirstValue(@NonNull SqlFirstValueFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("FIRST_VALUE", function.over(), function.column());
	}
	
	/**
	 * Renders the given lag function into dialect-specific sql.<br>
	 * The function is rendered as a {@code LAG(column, offset, defaultValue)} window call over its window clause.<br>
	 *
	 * @param function The lag function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLag(@NonNull SqlLagFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LAG", function.over(), function.column(), function.offset(), function.defaultValue());
	}
	
	/**
	 * Renders the given last-value function into dialect-specific sql.<br>
	 * The function is rendered as a {@code LAST_VALUE(column)} window call over its window clause.<br>
	 *
	 * @param function The last-value function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLastValue(@NonNull SqlLastValueFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LAST_VALUE", function.over(), function.column());
	}
	
	/**
	 * Renders the given lead function into dialect-specific sql.<br>
	 * The function is rendered as a {@code LEAD(column, offset, defaultValue)} window call over its window clause.<br>
	 *
	 * @param function The lead function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLead(@NonNull SqlLeadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LEAD", function.over(), function.column(), function.offset(), function.defaultValue());
	}
	
	/**
	 * Renders the given percent-rank function into dialect-specific sql.<br>
	 * The function is rendered as a {@code PERCENT_RANK()} window call over its window clause.<br>
	 *
	 * @param function The percent-rank function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderPercentRank(@NonNull SqlPercentRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("PERCENT_RANK", function.over());
	}
	
	/**
	 * Renders the given rank function into dialect-specific sql.<br>
	 * The function is rendered as a {@code RANK()} window call over its window clause.<br>
	 *
	 * @param function The rank function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderRank(@NonNull SqlRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("RANK", function.over());
	}
	
	/**
	 * Renders the given row-number function into dialect-specific sql.<br>
	 * The function is rendered as a {@code ROW_NUMBER()} window call over its window clause.<br>
	 *
	 * @param function The row-number function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderRowNumber(@NonNull SqlRowNumberFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("ROW_NUMBER", function.over());
	}
	
	/**
	 * Renders the given tile-bucket function into dialect-specific sql.<br>
	 * The function is rendered as an {@code NTILE(buckets)} window call over its window clause.<br>
	 *
	 * @param function The tile-bucket function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderTileBucket(@NonNull SqlTileBucketFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("NTILE", function.over(), function.buckets());
	}
	
	/**
	 * Renders the given value-at function into dialect-specific sql.<br>
	 * The function is rendered as an {@code NTH_VALUE(column, position)} window call over its window clause.<br>
	 *
	 * @param function The value-at function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderValueAt(@NonNull SqlValueAtFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("NTH_VALUE", function.over(), function.column(), function.position());
	}
	
	/**
	 * Renders the given windowed aggregate function into dialect-specific sql.<br>
	 * The underlying aggregate is rendered first, followed by an {@code OVER (...)} clause built from its window clause.<br>
	 *
	 * @param function The windowed aggregate function to render
	 * @return The rendered sql
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderWindowedAggregate(@NonNull SqlWindowedAggregate<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(this.aggregateRenderer.render(function.aggregate()));
		renderer.over().openingBracket().rendered(function.over().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
}
