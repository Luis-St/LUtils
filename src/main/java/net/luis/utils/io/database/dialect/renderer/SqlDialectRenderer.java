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
 * Aggregates all sub-renderers used to render SQL for a single dialect.<br>
 * It bundles the function, condition, expression, table, index, column, migration and schema renderers so that callers can access the complete rendering capability of a dialect through a single object.<br>
 * Instances are created through the {@link #builder(SqlDialect)} method.<br>
 *
 * @author Luis-St
 *
 * @param functionRenderer The renderer used to render function calls
 * @param conditionRenderer The renderer used to render conditions
 * @param expressionRenderer The renderer used to render expressions
 * @param tableRenderer The renderer used to render table definitions and operations
 * @param indexRenderer The renderer used to render index definitions and operations
 * @param columnRenderer The renderer used to render column alterations
 * @param migrationRenderer The renderer used to render migration operations
 * @param schemaRenderer The renderer used to render schema definitions and operations
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
	
	/**
	 * Constructs a new dialect renderer with the given sub-renderers.<br>
	 * @throws NullPointerException If any of the sub-renderers is null
	 */
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
	
	/**
	 * Creates a new builder for a dialect renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect the renderer should render SQL for
	 * @return The created builder
	 * @throws NullPointerException If the dialect is null
	 */
	public static @NonNull Builder builder(@NonNull SqlDialect dialect) {
		return new Builder(dialect);
	}
	
	/**
	 * Builder for a {@link SqlDialectRenderer}.<br>
	 * Each sub-renderer can be set explicitly, any sub-renderer that is not set is created with a default implementation for the configured dialect when {@link #build()} is called.<br>
	 *
	 * @author Luis-St
	 */
	public static final class Builder {
		
		/**
		 * The dialect the built renderer renders SQL for.
		 */
		private final SqlDialect dialect;
		
		/**
		 * The renderer used to render aggregate function calls.
		 */
		private SqlAggregateFunctionRenderer aggregateFunctionRenderer;
		/**
		 * The renderer used to render numeric function calls.
		 */
		private SqlNumericFunctionRenderer numericFunctionRenderer;
		/**
		 * The renderer used to render string function calls.
		 */
		private SqlStringFunctionRenderer stringFunctionRenderer;
		/**
		 * The renderer used to render temporal function calls.
		 */
		private SqlTemporalFunctionRenderer temporalFunctionRenderer;
		/**
		 * The renderer used to render window function calls.
		 */
		private SqlWindowFunctionRenderer windowFunctionRenderer;
		/**
		 * The renderer used to render generic function calls.
		 */
		private SqlGenericFunctionRenderer genericFunctionRenderer;
		
		/**
		 * The renderer used to render comparison conditions.
		 */
		private SqlComparisonConditionRenderer comparisonConditionRenderer;
		/**
		 * The renderer used to render numeric conditions.
		 */
		private SqlNumericConditionRenderer numericConditionRenderer;
		/**
		 * The renderer used to render string conditions.
		 */
		private SqlStringConditionRenderer stringConditionRenderer;
		/**
		 * The renderer used to render temporal conditions.
		 */
		private SqlTemporalConditionRenderer temporalConditionRenderer;
		
		/**
		 * The renderer used to render expressions.
		 */
		private SqlExpressionRenderer expressionRenderer;
		
		/**
		 * The renderer used to render table definitions and operations.
		 */
		private SqlTableRenderer tableRenderer;
		/**
		 * The renderer used to render index definitions and operations.
		 */
		private SqlIndexRenderer indexRenderer;
		/**
		 * The renderer used to render column alterations.
		 */
		private SqlColumnRenderer columnRenderer;
		/**
		 * The renderer used to render migration operations.
		 */
		private SqlMigrationOperationRenderer migrationRenderer;
		/**
		 * The renderer used to render schema definitions and operations.
		 */
		private SqlSchemaRenderer schemaRenderer;
		
		/**
		 * Constructs a new builder for the given dialect.<br>
		 *
		 * @param dialect The dialect the built renderer renders SQL for
		 * @throws NullPointerException If the dialect is null
		 */
		private Builder(@NonNull SqlDialect dialect) {
			this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		}
		
		/**
		 * Sets the renderer used to render aggregate function calls.<br>
		 *
		 * @param renderer The aggregate function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder aggregateFunctionRenderer(@NonNull SqlAggregateFunctionRenderer renderer) {
			this.aggregateFunctionRenderer = Objects.requireNonNull(renderer, "Sql aggregate function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render numeric function calls.<br>
		 *
		 * @param renderer The numeric function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder numericFunctionRenderer(@NonNull SqlNumericFunctionRenderer renderer) {
			this.numericFunctionRenderer = Objects.requireNonNull(renderer, "Sql numeric function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render string function calls.<br>
		 *
		 * @param renderer The string function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder stringFunctionRenderer(@NonNull SqlStringFunctionRenderer renderer) {
			this.stringFunctionRenderer = Objects.requireNonNull(renderer, "Sql string function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render temporal function calls.<br>
		 *
		 * @param renderer The temporal function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder temporalFunctionRenderer(@NonNull SqlTemporalFunctionRenderer renderer) {
			this.temporalFunctionRenderer = Objects.requireNonNull(renderer, "Sql temporal function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render window function calls.<br>
		 *
		 * @param renderer The window function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder windowFunctionRenderer(@NonNull SqlWindowFunctionRenderer renderer) {
			this.windowFunctionRenderer = Objects.requireNonNull(renderer, "Sql window function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render generic function calls.<br>
		 *
		 * @param renderer The generic function renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder genericFunctionRenderer(@NonNull SqlGenericFunctionRenderer renderer) {
			this.genericFunctionRenderer = Objects.requireNonNull(renderer, "Sql generic function renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render comparison conditions.<br>
		 *
		 * @param renderer The comparison condition renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder comparisonConditionRenderer(@NonNull SqlComparisonConditionRenderer renderer) {
			this.comparisonConditionRenderer = Objects.requireNonNull(renderer, "Sql comparison condition renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render numeric conditions.<br>
		 *
		 * @param renderer The numeric condition renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder numericConditionRenderer(@NonNull SqlNumericConditionRenderer renderer) {
			this.numericConditionRenderer = Objects.requireNonNull(renderer, "Sql numeric condition renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render string conditions.<br>
		 *
		 * @param renderer The string condition renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder stringConditionRenderer(@NonNull SqlStringConditionRenderer renderer) {
			this.stringConditionRenderer = Objects.requireNonNull(renderer, "Sql string condition renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render temporal conditions.<br>
		 *
		 * @param renderer The temporal condition renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder temporalConditionRenderer(@NonNull SqlTemporalConditionRenderer renderer) {
			this.temporalConditionRenderer = Objects.requireNonNull(renderer, "Sql temporal condition renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render expressions.<br>
		 *
		 * @param renderer The expression renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder expressionRenderer(@NonNull SqlExpressionRenderer renderer) {
			this.expressionRenderer = Objects.requireNonNull(renderer, "Sql expression renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render table definitions and operations.<br>
		 *
		 * @param renderer The table renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder tableRenderer(@NonNull SqlTableRenderer renderer) {
			this.tableRenderer = Objects.requireNonNull(renderer, "Sql table renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render index definitions and operations.<br>
		 *
		 * @param renderer The index renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder indexRenderer(@NonNull SqlIndexRenderer renderer) {
			this.indexRenderer = Objects.requireNonNull(renderer, "Sql index renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render column alterations.<br>
		 *
		 * @param renderer The column renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder columnRenderer(@NonNull SqlColumnRenderer renderer) {
			this.columnRenderer = Objects.requireNonNull(renderer, "Sql column renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render migration operations.<br>
		 *
		 * @param renderer The migration operation renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder migrationRenderer(@NonNull SqlMigrationOperationRenderer renderer) {
			this.migrationRenderer = Objects.requireNonNull(renderer, "Sql migration operation renderer must not be null");
			return this;
		}
		
		/**
		 * Sets the renderer used to render schema definitions and operations.<br>
		 *
		 * @param renderer The schema renderer to use
		 * @return This builder
		 * @throws NullPointerException If the renderer is null
		 */
		public @NonNull Builder schemaRenderer(@NonNull SqlSchemaRenderer renderer) {
			this.schemaRenderer = Objects.requireNonNull(renderer, "Sql schema renderer must not be null");
			return this;
		}
		
		/**
		 * Builds a new dialect renderer from the configured sub-renderers.<br>
		 * Any sub-renderer that was not set explicitly is created with a default implementation for the configured dialect.<br>
		 *
		 * @return The built dialect renderer
		 */
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
		
		/**
		 * Builds the condition renderer from the configured condition sub-renderers, creating default implementations for any sub-renderer that was not set explicitly.<br>
		 *
		 * @param temp The temporal function renderer used by the default temporal condition renderer
		 * @return The built condition renderer
		 */
		private @NonNull SqlConditionRenderer buildConditionRenderer(SqlTemporalFunctionRenderer temp) {
			SqlComparisonConditionRenderer comp = this.comparisonConditionRenderer != null ? this.comparisonConditionRenderer : new SqlComparisonConditionRenderer(this.dialect);
			SqlNumericConditionRenderer numCond = this.numericConditionRenderer != null ? this.numericConditionRenderer : new SqlNumericConditionRenderer(this.dialect);
			SqlStringConditionRenderer strCond = this.stringConditionRenderer != null ? this.stringConditionRenderer : new SqlStringConditionRenderer(this.dialect);
			SqlTemporalConditionRenderer tempCond = this.temporalConditionRenderer != null ? this.temporalConditionRenderer : new SqlTemporalConditionRenderer(this.dialect, temp);
			return new SqlConditionRenderer(this.dialect, comp, numCond, strCond, tempCond);
		}
	}
}
