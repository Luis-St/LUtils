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

import net.luis.utils.io.database.condition.conditions.SqlComparisonCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.dialect.renderer.expression.SqlExpressionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.condition.*;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlCountFunction;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDialectRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlDialectRendererTest {
	
	private static SqlDialectRenderer reference() {
		return SqlDialectRenderer.builder(SqlDialects.DEFAULT).build();
	}
	
	@Test
	void constructWithAllRenderers() {
		SqlDialectRenderer r = reference();
		SqlDialectRenderer renderer = new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer());
		assertNotNull(renderer);
		assertSame(r.functionRenderer(), renderer.functionRenderer());
		assertSame(r.schemaRenderer(), renderer.schemaRenderer());
	}
	
	@Test
	void constructWithNullFunctionRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(null, r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullConditionRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), null, r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullExpressionRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), null, r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullTableRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), null, r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullIndexRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), null, r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullColumnRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), null, r.migrationRenderer(), r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullMigrationRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), null, r.schemaRenderer()));
	}
	
	@Test
	void constructWithNullSchemaRenderer() {
		SqlDialectRenderer r = reference();
		assertThrows(NullPointerException.class, () -> new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), null));
	}
	
	@Test
	void builderWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(null));
	}
	
	@Test
	void builderAggregateFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).aggregateFunctionRenderer(null));
	}
	
	@Test
	void builderNumericFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).numericFunctionRenderer(null));
	}
	
	@Test
	void builderStringFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).stringFunctionRenderer(null));
	}
	
	@Test
	void builderTemporalFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).temporalFunctionRenderer(null));
	}
	
	@Test
	void builderWindowFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).windowFunctionRenderer(null));
	}
	
	@Test
	void builderGenericFunctionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).genericFunctionRenderer(null));
	}
	
	@Test
	void builderComparisonConditionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).comparisonConditionRenderer(null));
	}
	
	@Test
	void builderNumericConditionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).numericConditionRenderer(null));
	}
	
	@Test
	void builderStringConditionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).stringConditionRenderer(null));
	}
	
	@Test
	void builderTemporalConditionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).temporalConditionRenderer(null));
	}
	
	@Test
	void builderExpressionRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).expressionRenderer(null));
	}
	
	@Test
	void builderTableRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).tableRenderer(null));
	}
	
	@Test
	void builderIndexRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).indexRenderer(null));
	}
	
	@Test
	void builderColumnRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).columnRenderer(null));
	}
	
	@Test
	void builderMigrationRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).migrationRenderer(null));
	}
	
	@Test
	void builderSchemaRendererWithNull() {
		assertThrows(NullPointerException.class, () -> SqlDialectRenderer.builder(SqlDialects.DEFAULT).schemaRenderer(null));
	}
	
	@Test
	void buildWithAllDefaults() {
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).build();
		assertNotNull(renderer);
		assertNotNull(renderer.functionRenderer());
		assertNotNull(renderer.conditionRenderer());
		assertNotNull(renderer.expressionRenderer());
		assertNotNull(renderer.tableRenderer());
		assertNotNull(renderer.indexRenderer());
		assertNotNull(renderer.columnRenderer());
		assertNotNull(renderer.migrationRenderer());
		assertNotNull(renderer.schemaRenderer());
	}
	
	@Test
	void buildWithProvidedTableRenderer() {
		SqlTableRenderer custom = new SqlTableRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).tableRenderer(custom).build();
		assertSame(custom, renderer.tableRenderer());
	}
	
	@Test
	void buildWithProvidedIndexRenderer() {
		SqlIndexRenderer custom = new SqlIndexRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).indexRenderer(custom).build();
		assertSame(custom, renderer.indexRenderer());
	}
	
	@Test
	void buildWithProvidedColumnRenderer() {
		SqlColumnRenderer custom = new SqlColumnRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).columnRenderer(custom).build();
		assertSame(custom, renderer.columnRenderer());
	}
	
	@Test
	void buildWithProvidedMigrationRenderer() {
		SqlMigrationOperationRenderer custom = new SqlMigrationOperationRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).migrationRenderer(custom).build();
		assertSame(custom, renderer.migrationRenderer());
	}
	
	@Test
	void buildWithProvidedSchemaRenderer() {
		SqlSchemaRenderer custom = new SqlSchemaRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).schemaRenderer(custom).build();
		assertSame(custom, renderer.schemaRenderer());
	}
	
	@Test
	void buildWithProvidedExpressionRenderer() {
		SqlExpressionRenderer custom = new SqlExpressionRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).expressionRenderer(custom).build();
		assertSame(custom, renderer.expressionRenderer());
	}
	
	@Test
	void buildWithProvidedFunctionAndConditionSubRenderers() {
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT)
			.numericFunctionRenderer(new SqlNumericFunctionRenderer(SqlDialects.DEFAULT))
			.stringFunctionRenderer(new SqlStringFunctionRenderer(SqlDialects.DEFAULT))
			.temporalFunctionRenderer(new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT))
			.windowFunctionRenderer(new SqlWindowFunctionRenderer(SqlDialects.DEFAULT, new SqlAggregateFunctionRenderer(SqlDialects.DEFAULT)))
			.genericFunctionRenderer(new SqlGenericFunctionRenderer(SqlDialects.DEFAULT))
			.numericConditionRenderer(new SqlNumericConditionRenderer(SqlDialects.DEFAULT))
			.stringConditionRenderer(new SqlStringConditionRenderer(SqlDialects.DEFAULT))
			.temporalConditionRenderer(new SqlTemporalConditionRenderer(SqlDialects.DEFAULT, new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT)))
			.build();
		assertNotNull(renderer);
		assertNotNull(renderer.functionRenderer());
		assertNotNull(renderer.conditionRenderer());
	}
	
	@Test
	void accessorsReturnConstructorArguments() {
		SqlDialectRenderer r = reference();
		SqlDialectRenderer renderer = new SqlDialectRenderer(r.functionRenderer(), r.conditionRenderer(), r.expressionRenderer(), r.tableRenderer(), r.indexRenderer(), r.columnRenderer(), r.migrationRenderer(), r.schemaRenderer());
		assertSame(r.functionRenderer(), renderer.functionRenderer());
		assertSame(r.conditionRenderer(), renderer.conditionRenderer());
		assertSame(r.expressionRenderer(), renderer.expressionRenderer());
		assertSame(r.tableRenderer(), renderer.tableRenderer());
		assertSame(r.indexRenderer(), renderer.indexRenderer());
		assertSame(r.columnRenderer(), renderer.columnRenderer());
		assertSame(r.migrationRenderer(), renderer.migrationRenderer());
		assertSame(r.schemaRenderer(), renderer.schemaRenderer());
	}
	
	@Test
	void builderSettersReturnSameBuilder() {
		SqlDialectRenderer.Builder builder = SqlDialectRenderer.builder(SqlDialects.DEFAULT);
		assertSame(builder, builder.tableRenderer(new SqlTableRenderer(SqlDialects.DEFAULT)));
		assertSame(builder, builder.schemaRenderer(new SqlSchemaRenderer(SqlDialects.DEFAULT)));
	}
	
	@Test
	void buildWithMixOfProvidedAndDefaultRenderers() {
		SqlTableRenderer customTable = new SqlTableRenderer(SqlDialects.DEFAULT);
		SqlSchemaRenderer customSchema = new SqlSchemaRenderer(SqlDialects.DEFAULT);
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).tableRenderer(customTable).schemaRenderer(customSchema).build();
		assertSame(customTable, renderer.tableRenderer());
		assertSame(customSchema, renderer.schemaRenderer());
		assertNotNull(renderer.columnRenderer());
		assertNotSame(customTable, renderer.indexRenderer());
	}
	
	@Test
	void buildIsRepeatableFromSameBuilder() {
		SqlDialectRenderer.Builder builder = SqlDialectRenderer.builder(SqlDialects.DEFAULT);
		SqlDialectRenderer first = builder.build();
		SqlDialectRenderer second = builder.build();
		assertNotNull(first.functionRenderer());
		assertNotNull(second.functionRenderer());
		assertNotNull(first.schemaRenderer());
		assertNotNull(second.schemaRenderer());
	}
	
	@Test
	void buildWiresProvidedSubRendererIntoFunctionRenderer() throws SqlException {
		MarkerAggregateRenderer custom = new MarkerAggregateRenderer();
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).aggregateFunctionRenderer(custom).build();
		SqlRendered rendered = renderer.functionRenderer().render(new SqlCountFunction(new SqlValueExpression<>(1)));
		assertTrue(rendered.sql().contains("__MARKER__"));
	}
	
	@Test
	void buildWiresProvidedSubRendererIntoConditionRenderer() throws SqlException {
		MarkerComparisonRenderer custom = new MarkerComparisonRenderer();
		SqlDialectRenderer renderer = SqlDialectRenderer.builder(SqlDialects.DEFAULT).comparisonConditionRenderer(custom).build();
		SqlRendered rendered = renderer.conditionRenderer().render(new SqlEqualToCondition(new SqlValueExpression<>(1), new SqlValueExpression<>(1)));
		assertTrue(rendered.sql().contains("__MARKER__"));
	}
	
	private static final class MarkerAggregateRenderer extends SqlAggregateFunctionRenderer {
		
		private MarkerAggregateRenderer() {
			super(SqlDialects.DEFAULT);
		}
		
		@Override
		public @NonNull SqlRendered render(@NonNull SqlAggregateFunction<?> function) {
			return SqlRendered.of("__MARKER__");
		}
	}
	
	private static final class MarkerComparisonRenderer extends SqlComparisonConditionRenderer {
		
		private MarkerComparisonRenderer() {
			super(SqlDialects.DEFAULT);
		}
		
		@Override
		public @NonNull SqlRendered render(@NonNull SqlComparisonCondition condition) {
			return SqlRendered.of("__MARKER__");
		}
	}
}
