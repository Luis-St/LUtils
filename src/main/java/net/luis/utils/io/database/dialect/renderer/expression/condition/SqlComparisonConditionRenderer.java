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

package net.luis.utils.io.database.dialect.renderer.expression.condition;

import net.luis.utils.io.database.condition.conditions.SqlComparisonCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Renderer for comparison sql conditions into dialect-specific sql.<br>
 * Handles conditions such as equality, ordering comparisons, {@code BETWEEN}, {@code IN}, {@code IS NULL} and {@code IS DISTINCT FROM}.<br>
 *
 * @author Luis-St
 */

public class SqlComparisonConditionRenderer {
	
	/**
	 * The maximum number of options that may appear in a single {@code IN} list before they are split into multiple groups.
	 */
	protected static final int MAX_IN_LIST_SIZE = 1000;
	
	/**
	 * The sql dialect used to render the conditions.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql comparison condition renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the conditions
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders the given comparison condition into dialect-specific sql.<br>
	 * The condition is dispatched to the matching render method based on its concrete type.<br>
	 *
	 * @param condition The comparison condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlComparisonCondition condition) throws SqlException {
		return switch (condition) {
			case SqlBetweenCondition cond -> this.renderBetween(cond);
			case SqlEqualToCondition cond -> this.renderEqualTo(cond);
			case SqlGreaterThanCondition cond -> this.renderGreaterThan(cond);
			case SqlInListCondition cond -> this.renderInList(cond);
			case SqlInQueryCondition cond -> this.renderInQuery(cond);
			case SqlIsDistinctFromCondition cond -> this.renderIsDistinctFrom(cond);
			case SqlIsNullCondition cond -> this.renderIsNull(cond);
			case SqlLessThanCondition cond -> this.renderLessThan(cond);
			
			case null -> throw new NullPointerException("Sql comparison condition must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql comparison condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	/**
	 * Renders the given between condition into dialect-specific sql.<br>
	 * Produces a {@code value BETWEEN lower AND upper} expression.<br>
	 *
	 * @param condition The between condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderBetween(@NonNull SqlBetweenCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).between().rendered(condition.lower().toSql(this.dialect)).and().rendered(condition.upper().toSql(this.dialect));
		return renderer.toSql();
	}
	
	/**
	 * Renders the given equal-to condition into dialect-specific sql.<br>
	 * Produces a {@code first = second} expression.<br>
	 *
	 * @param condition The equal-to condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderEqualTo(@NonNull SqlEqualToCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.first(), "=", condition.second());
	}
	
	/**
	 * Renders the given greater-than condition into dialect-specific sql.<br>
	 * Produces a {@code value > threshold} or {@code value >= threshold} expression depending on whether equality is included.<br>
	 *
	 * @param condition The greater-than condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderGreaterThan(@NonNull SqlGreaterThanCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), condition.equalTo() ? ">=" : ">", condition.threshold());
	}
	
	/**
	 * Renders the given in-list condition into dialect-specific sql.<br>
	 * Produces a {@code value IN (...)} expression, splitting the options into multiple {@code OR}-combined groups when the option count exceeds {@link #MAX_IN_LIST_SIZE}.<br>
	 *
	 * @param condition The in-list condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderInList(@NonNull SqlInListCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		List<SqlExpression<?>> options = condition.options();
		if (options.size() <= MAX_IN_LIST_SIZE) {
			return this.renderInListGroup(condition.value(), options);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.openingBracket();
		for (int i = 0; i < options.size(); i += MAX_IN_LIST_SIZE) {
			if (i > 0) {
				renderer.or();
			}
			
			renderer.rendered(this.renderInListGroup(condition.value(), options.subList(i, Math.min(i + MAX_IN_LIST_SIZE, options.size()))));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders a single in-list group into dialect-specific sql.<br>
	 * Produces a {@code value IN (option, ...)} expression for the given options.<br>
	 *
	 * @param value The value expression to test
	 * @param options The options the value is tested against
	 * @return The rendered sql
	 * @throws NullPointerException If the value or options is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderInListGroup(@NonNull SqlExpression<?> value, @NonNull List<SqlExpression<?>> options) throws SqlException {
		Objects.requireNonNull(value, "Sql value expression must not be null");
		Objects.requireNonNull(options, "Sql options list must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(value.toSql(this.dialect)).in().openingBracket();
		SqlRenderingHelper.renderList(renderer, options, (r, item) -> r.rendered(item.toSql(this.dialect)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given in-query condition into dialect-specific sql.<br>
	 * Produces a {@code value IN (subquery)} expression.<br>
	 *
	 * @param condition The in-query condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderInQuery(@NonNull SqlInQueryCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).in().openingBracket().rendered(condition.query().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given is-distinct-from condition into dialect-specific sql.<br>
	 * Produces a {@code first IS DISTINCT FROM second} expression.<br>
	 *
	 * @param condition The is-distinct-from condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderIsDistinctFrom(@NonNull SqlIsDistinctFromCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.first().toSql(this.dialect)).is().keyword("DISTINCT").from().rendered(condition.second().toSql(this.dialect));
		return renderer.toSql();
	}
	
	/**
	 * Renders the given is-null condition into dialect-specific sql.<br>
	 * Produces a {@code value IS NULL} expression.<br>
	 *
	 * @param condition The is-null condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderIsNull(@NonNull SqlIsNullCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).is().null_();
		return renderer.toSql();
	}
	
	/**
	 * Renders the given less-than condition into dialect-specific sql.<br>
	 * Produces a {@code value < threshold} or {@code value <= threshold} expression depending on whether equality is included.<br>
	 *
	 * @param condition The less-than condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderLessThan(@NonNull SqlLessThanCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, condition.value(), condition.equalTo() ? "<=" : "<", condition.threshold());
	}
}
