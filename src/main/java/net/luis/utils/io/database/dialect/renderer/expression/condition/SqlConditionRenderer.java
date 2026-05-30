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
 *
 * @author Luis-St
 *
 */

public final class SqlConditionRenderer {
	
	private final SqlDialect dialect;
	private final SqlComparisonConditionRenderer comparisonRenderer;
	private final SqlNumericConditionRenderer numericRenderer;
	private final SqlStringConditionRenderer stringRenderer;
	private final SqlTemporalConditionRenderer temporalRenderer;
	
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
