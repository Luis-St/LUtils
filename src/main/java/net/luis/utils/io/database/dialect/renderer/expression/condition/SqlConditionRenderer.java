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

import net.luis.utils.io.database.condition.*;
import net.luis.utils.io.database.condition.conditions.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Renderer for boolean sql conditions into dialect-specific sql.<br>
 * Acts as the dispatcher that handles logical conditions such as negation, conjunction and disjunction directly,<br>
 * and delegates category-specific conditions to the matching comparison, numeric, string or temporal renderer.<br>
 *
 * @author Luis-St
 */

public final class SqlConditionRenderer {
	
	/**
	 * The sql dialect used to render the conditions.
	 */
	private final SqlDialect dialect;
	/**
	 * The renderer for comparison conditions.
	 */
	private final SqlComparisonConditionRenderer comparisonRenderer;
	/**
	 * The renderer for numeric conditions.
	 */
	private final SqlNumericConditionRenderer numericRenderer;
	/**
	 * The renderer for string conditions.
	 */
	private final SqlStringConditionRenderer stringRenderer;
	/**
	 * The renderer for temporal conditions.
	 */
	private final SqlTemporalConditionRenderer temporalRenderer;
	
	/**
	 * Constructs a new sql condition renderer for the given dialect and category renderers.<br>
	 *
	 * @param dialect The sql dialect used to render the conditions
	 * @param comparisonRenderer The renderer for comparison conditions
	 * @param numericRenderer The renderer for numeric conditions
	 * @param stringRenderer The renderer for string conditions
	 * @param temporalRenderer The renderer for temporal conditions
	 * @throws NullPointerException If any of the arguments is null
	 */
	public SqlConditionRenderer(
		@NonNull SqlDialect dialect,
		@NonNull SqlComparisonConditionRenderer comparisonRenderer,
		@NonNull SqlNumericConditionRenderer numericRenderer,
		@NonNull SqlStringConditionRenderer stringRenderer,
		@NonNull SqlTemporalConditionRenderer temporalRenderer
	) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.comparisonRenderer = Objects.requireNonNull(comparisonRenderer, "Sql comparison condition renderer must not be null");
		this.numericRenderer = Objects.requireNonNull(numericRenderer, "Sql numeric condition renderer must not be null");
		this.stringRenderer = Objects.requireNonNull(stringRenderer, "Sql string condition renderer must not be null");
		this.temporalRenderer = Objects.requireNonNull(temporalRenderer, "Sql temporal condition renderer must not be null");
	}
	
	/**
	 * Renders the given sql condition into dialect-specific sql.<br>
	 * Logical conditions are handled directly, while category-specific conditions are delegated to the matching renderer.<br>
	 *
	 * @param condition The sql condition to render
	 * @return The rendered sql
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered render(@NonNull SqlCondition condition) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		
		return switch (condition) {
			case SqlNegatedCondition cond -> renderer.not().openingBracket().rendered(this.render(cond.condition())).closingBracket().toSql();
			case SqlAlwaysCondition _ -> renderer.rendered(this.dialect.renderBooleanLiteral(true)).toSql();
			case SqlNeverCondition _ -> renderer.rendered(this.dialect.renderBooleanLiteral(false)).toSql();
			case SqlAllOfCondition cond -> {
				List<SqlCondition> conditions = cond.conditions();
				for (int i = 0; i < conditions.size(); i++) {
					if (i > 0) {
						renderer.and();
					}
					renderer.openingBracket().rendered(this.render(conditions.get(i))).closingBracket();
				}
				yield renderer.toSql();
			}
			case SqlAnyOfCondition cond -> {
				List<SqlCondition> conditions = cond.conditions();
				for (int i = 0; i < conditions.size(); i++) {
					if (i > 0) {
						renderer.or();
					}
					renderer.openingBracket().rendered(this.render(conditions.get(i))).closingBracket();
				}
				yield renderer.toSql();
			}
			case SqlComparisonCondition cond -> this.comparisonRenderer.render(cond);
			case SqlNumericCondition cond -> this.numericRenderer.render(cond);
			case SqlStringCondition cond -> this.stringRenderer.render(cond);
			case SqlTemporalCondition cond -> this.temporalRenderer.render(cond);
			
			case null -> throw new NullPointerException("Sql condition must not be null");
			default -> throw new SqlDialectUnknownConstructException("Unknown sql condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
}
