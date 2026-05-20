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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.expression.SqlExpressionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.condition.*;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlDialectRenderer(
	@NonNull SqlFunctionRenderer functionRenderer,
	@NonNull SqlConditionRenderer conditionRenderer,
	@NonNull SqlExpressionRenderer expressionRenderer,
	@NonNull SqlTableRenderer tableRenderer,
	@NonNull SqlIndexRenderer indexRenderer,
	@NonNull SqlColumnRenderer columnRenderer,
	@NonNull SqlMigrationOperationRenderer migrationRenderer,
	@NonNull SqlSchemaRenderer schemaRenderer
) {
	
	public SqlDialectRenderer {
		Objects.requireNonNull(functionRenderer, "Sql function renderer must not be null");
		Objects.requireNonNull(conditionRenderer, "Sql condition renderer must not be null");
		Objects.requireNonNull(expressionRenderer, "Sql expression renderer must not be null");
		Objects.requireNonNull(tableRenderer, "Sql table renderer must not be null");
		Objects.requireNonNull(indexRenderer, "Sql index renderer must not be null");
		Objects.requireNonNull(columnRenderer, "Sql column renderer must not be null");
		Objects.requireNonNull(migrationRenderer, "Sql migration operation renderer must not be null");
		Objects.requireNonNull(schemaRenderer, "Sql schema renderer must not be null");
	}
	
	public static @NonNull Builder builder(@NonNull SqlDialect dialect) {
		return new Builder(dialect);
	}
	
	public static final class Builder {
		
		private final SqlDialect dialect;
		
		private SqlAggregateFunctionRenderer aggregateFunctionRenderer;
		private SqlNumericFunctionRenderer numericFunctionRenderer;
		private SqlStringFunctionRenderer stringFunctionRenderer;
		private SqlTemporalFunctionRenderer temporalFunctionRenderer;
		private SqlWindowFunctionRenderer windowFunctionRenderer;
		private SqlGenericFunctionRenderer genericFunctionRenderer;
		
		private SqlComparisonConditionRenderer comparisonConditionRenderer;
		private SqlNumericConditionRenderer numericConditionRenderer;
		private SqlStringConditionRenderer stringConditionRenderer;
		private SqlTemporalConditionRenderer temporalConditionRenderer;
		
		private SqlExpressionRenderer expressionRenderer;
		
		private SqlTableRenderer tableRenderer;
		private SqlIndexRenderer indexRenderer;
		private SqlColumnRenderer columnRenderer;
		private SqlMigrationOperationRenderer migrationRenderer;
		private SqlSchemaRenderer schemaRenderer;
		
		private Builder(@NonNull SqlDialect dialect) {
			this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		}
		
		public @NonNull Builder aggregateFunctionRenderer(@NonNull SqlAggregateFunctionRenderer renderer) {
			this.aggregateFunctionRenderer = Objects.requireNonNull(renderer, "Sql aggregate function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder numericFunctionRenderer(@NonNull SqlNumericFunctionRenderer renderer) {
			this.numericFunctionRenderer = Objects.requireNonNull(renderer, "Sql numeric function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder stringFunctionRenderer(@NonNull SqlStringFunctionRenderer renderer) {
			this.stringFunctionRenderer = Objects.requireNonNull(renderer, "Sql string function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder temporalFunctionRenderer(@NonNull SqlTemporalFunctionRenderer renderer) {
			this.temporalFunctionRenderer = Objects.requireNonNull(renderer, "Sql temporal function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder windowFunctionRenderer(@NonNull SqlWindowFunctionRenderer renderer) {
			this.windowFunctionRenderer = Objects.requireNonNull(renderer, "Sql window function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder genericFunctionRenderer(@NonNull SqlGenericFunctionRenderer renderer) {
			this.genericFunctionRenderer = Objects.requireNonNull(renderer, "Sql generic function renderer must not be null");
			return this;
		}
		
		public @NonNull Builder comparisonConditionRenderer(@NonNull SqlComparisonConditionRenderer renderer) {
			this.comparisonConditionRenderer = Objects.requireNonNull(renderer, "Sql comparison condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder numericConditionRenderer(@NonNull SqlNumericConditionRenderer renderer) {
			this.numericConditionRenderer = Objects.requireNonNull(renderer, "Sql numeric condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder stringConditionRenderer(@NonNull SqlStringConditionRenderer renderer) {
			this.stringConditionRenderer = Objects.requireNonNull(renderer, "Sql string condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder temporalConditionRenderer(@NonNull SqlTemporalConditionRenderer renderer) {
			this.temporalConditionRenderer = Objects.requireNonNull(renderer, "Sql temporal condition renderer must not be null");
			return this;
		}
		
		public @NonNull Builder expressionRenderer(@NonNull SqlExpressionRenderer renderer) {
			this.expressionRenderer = Objects.requireNonNull(renderer, "Sql expression renderer must not be null");
			return this;
		}
		
		public @NonNull Builder tableRenderer(@NonNull SqlTableRenderer renderer) {
			this.tableRenderer = Objects.requireNonNull(renderer, "Sql table renderer must not be null");
			return this;
		}
		
		public @NonNull Builder indexRenderer(@NonNull SqlIndexRenderer renderer) {
			this.indexRenderer = Objects.requireNonNull(renderer, "Sql index renderer must not be null");
			return this;
		}
		
		public @NonNull Builder columnRenderer(@NonNull SqlColumnRenderer renderer) {
			this.columnRenderer = Objects.requireNonNull(renderer, "Sql column renderer must not be null");
			return this;
		}
		
		public @NonNull Builder migrationRenderer(@NonNull SqlMigrationOperationRenderer renderer) {
			this.migrationRenderer = Objects.requireNonNull(renderer, "Sql migration operation renderer must not be null");
			return this;
		}
		
		public @NonNull Builder schemaRenderer(@NonNull SqlSchemaRenderer renderer) {
			this.schemaRenderer = Objects.requireNonNull(renderer, "Sql schema renderer must not be null");
			return this;
		}
		
		public @NonNull SqlDialectRenderer build() {
			SqlAggregateFunctionRenderer agg = this.aggregateFunctionRenderer != null ? this.aggregateFunctionRenderer : new SqlAggregateFunctionRenderer(this.dialect);
			SqlNumericFunctionRenderer num = this.numericFunctionRenderer != null ? this.numericFunctionRenderer : new SqlNumericFunctionRenderer(this.dialect);
			SqlStringFunctionRenderer str = this.stringFunctionRenderer != null ? this.stringFunctionRenderer : new SqlStringFunctionRenderer(this.dialect);
			SqlTemporalFunctionRenderer temp = this.temporalFunctionRenderer != null ? this.temporalFunctionRenderer : new SqlTemporalFunctionRenderer(this.dialect);
			SqlWindowFunctionRenderer win = this.windowFunctionRenderer != null ? this.windowFunctionRenderer : new SqlWindowFunctionRenderer(this.dialect, agg);
			SqlGenericFunctionRenderer gen = this.genericFunctionRenderer != null ? this.genericFunctionRenderer : new SqlGenericFunctionRenderer(this.dialect);
			SqlFunctionRenderer function = new SqlFunctionRenderer(this.dialect, agg, num, str, temp, win, gen);
			
			SqlConditionRenderer condition = this.buildConditionRenderer(temp);
			SqlExpressionRenderer expression = this.expressionRenderer != null ? this.expressionRenderer : new SqlExpressionRenderer(this.dialect);
			
			SqlTableRenderer table = this.tableRenderer != null ? this.tableRenderer : new SqlTableRenderer(this.dialect);
			SqlIndexRenderer index = this.indexRenderer != null ? this.indexRenderer : new SqlIndexRenderer(this.dialect);
			SqlColumnRenderer column = this.columnRenderer != null ? this.columnRenderer : new SqlColumnRenderer(this.dialect);
			SqlMigrationOperationRenderer migration = this.migrationRenderer != null ? this.migrationRenderer : new SqlMigrationOperationRenderer(this.dialect);
			SqlSchemaRenderer schema = this.schemaRenderer != null ? this.schemaRenderer : new SqlSchemaRenderer(this.dialect);
			
			return new SqlDialectRenderer(
				function,
				condition,
				expression,
				table,
				index,
				column,
				migration,
				schema
			);
		}
		
		private @NonNull SqlConditionRenderer buildConditionRenderer(SqlTemporalFunctionRenderer temp) {
			SqlComparisonConditionRenderer comp = this.comparisonConditionRenderer != null ? this.comparisonConditionRenderer : new SqlComparisonConditionRenderer(this.dialect);
			SqlNumericConditionRenderer numCond = this.numericConditionRenderer != null ? this.numericConditionRenderer : new SqlNumericConditionRenderer(this.dialect);
			SqlStringConditionRenderer strCond = this.stringConditionRenderer != null ? this.stringConditionRenderer : new SqlStringConditionRenderer(this.dialect);
			SqlTemporalConditionRenderer tempCond = this.temporalConditionRenderer != null ? this.temporalConditionRenderer : new SqlTemporalConditionRenderer(this.dialect, temp);
			return new SqlConditionRenderer(this.dialect, comp, numCond, strCond, tempCond);
		}
	}
}
