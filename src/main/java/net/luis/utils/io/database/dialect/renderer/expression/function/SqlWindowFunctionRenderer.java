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
 *
 * @author Luis-St
 *
 */

public class SqlWindowFunctionRenderer {
	
	private final SqlAggregateFunctionRenderer aggregateRenderer;
	protected final SqlDialect dialect;
	
	public SqlWindowFunctionRenderer(@NonNull SqlDialect dialect, @NonNull SqlAggregateFunctionRenderer aggregateRenderer) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.aggregateRenderer = Objects.requireNonNull(aggregateRenderer, "Sql aggregate function renderer must not be null");
	}
	
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
	
	protected @NonNull SqlRendered renderCumulativeDistribution(@NonNull SqlCumulativeDistributionFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("CUME_DIST", function.over());
	}
	
	protected @NonNull SqlRendered renderDenseRank(@NonNull SqlDenseRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("DENSE_RANK", function.over());
	}
	
	protected @NonNull SqlRendered renderFirstValue(@NonNull SqlFirstValueFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("FIRST_VALUE", function.over(), function.column());
	}
	
	protected @NonNull SqlRendered renderLag(@NonNull SqlLagFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LAG", function.over(), function.column(), function.offset(), function.defaultValue());
	}
	
	protected @NonNull SqlRendered renderLastValue(@NonNull SqlLastValueFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LAST_VALUE", function.over(), function.column());
	}
	
	protected @NonNull SqlRendered renderLead(@NonNull SqlLeadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("LEAD", function.over(), function.column(), function.offset(), function.defaultValue());
	}
	
	protected @NonNull SqlRendered renderPercentRank(@NonNull SqlPercentRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("PERCENT_RANK", function.over());
	}
	
	protected @NonNull SqlRendered renderRank(@NonNull SqlRankFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("RANK", function.over());
	}
	
	protected @NonNull SqlRendered renderRowNumber(@NonNull SqlRowNumberFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("ROW_NUMBER", function.over());
	}
	
	protected @NonNull SqlRendered renderTileBucket(@NonNull SqlTileBucketFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("NTILE", function.over(), function.buckets());
	}
	
	protected @NonNull SqlRendered renderValueAt(@NonNull SqlValueAtFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return this.renderWindowCall("NTH_VALUE", function.over(), function.column(), function.position());
	}
	
	protected @NonNull SqlRendered renderWindowedAggregate(@NonNull SqlWindowedAggregate<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(this.aggregateRenderer.render(function.aggregate()));
		renderer.over().openingBracket().rendered(function.over().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
}
