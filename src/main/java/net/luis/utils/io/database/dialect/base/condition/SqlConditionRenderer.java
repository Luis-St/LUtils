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

package net.luis.utils.io.database.dialect.base.condition;

import net.luis.utils.io.database.condition.*;
import net.luis.utils.io.database.condition.conditions.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.base.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
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
	
	private SqlConditionRenderer(
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
	
	public static @NonNull Builder builder(@NonNull SqlDialect dialect, @NonNull SqlTemporalFunctionRenderer temporalFunctionRenderer) {
		return new Builder(dialect, temporalFunctionRenderer);
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
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql condition type: " + condition.getClass().getName() + " in dialect " + this.dialect.name());
		};
	}
	
	public static final class Builder {
		
		private final SqlDialect dialect;
		private final SqlTemporalFunctionRenderer temporalFunctionRenderer;
		private SqlComparisonConditionRenderer comparisonRenderer;
		private SqlNumericConditionRenderer numericRenderer;
		private SqlStringConditionRenderer stringRenderer;
		private SqlTemporalConditionRenderer temporalRenderer;
		
		private Builder(@NonNull SqlDialect dialect, @NonNull SqlTemporalFunctionRenderer temporalFunctionRenderer) {
			this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
			this.temporalFunctionRenderer = Objects.requireNonNull(temporalFunctionRenderer, "Sql temporal function renderer must not be null");
		}
		
		public @NonNull Builder comparison(@NonNull SqlComparisonConditionRenderer renderer) {
			this.comparisonRenderer = Objects.requireNonNull(renderer, "Sql comparison condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder numeric(@NonNull SqlNumericConditionRenderer renderer) {
			this.numericRenderer = Objects.requireNonNull(renderer, "Sql numeric condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder string(@NonNull SqlStringConditionRenderer renderer) {
			this.stringRenderer = Objects.requireNonNull(renderer, "Sql string condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder temporal(@NonNull SqlTemporalConditionRenderer renderer) {
			this.temporalRenderer = Objects.requireNonNull(renderer, "Sql temporal condition renderer must not be null");
			return this;
		}
		
		public @NonNull SqlConditionRenderer build() {
			SqlComparisonConditionRenderer comp = this.comparisonRenderer != null ? this.comparisonRenderer : new SqlComparisonConditionRenderer(this.dialect);
			SqlNumericConditionRenderer num = this.numericRenderer != null ? this.numericRenderer : new SqlNumericConditionRenderer(this.dialect);
			SqlStringConditionRenderer str = this.stringRenderer != null ? this.stringRenderer : new SqlStringConditionRenderer(this.dialect);
			SqlTemporalConditionRenderer temp = this.temporalRenderer != null ? this.temporalRenderer : new SqlTemporalConditionRenderer(this.dialect, this.temporalFunctionRenderer);
			return new SqlConditionRenderer(this.dialect, comp, num, str, temp);
		}
	}
}
