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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.rendering.base.*;
import net.luis.utils.io.database.dialect.rendering.base.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.function.window.frame.*;
import net.luis.utils.io.database.function.window.frame.bound.*;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public abstract class AbstractSqlDialect implements SqlDialect {
	
	private final SqlFunctionRenderer functionRenderer;
	private final SqlConditionRenderer conditionRenderer;
	private final SqlExpressionRenderer expressionRenderer;
	
	protected AbstractSqlDialect() {
		this.functionRenderer = this.createFunctionRenderer();
		this.conditionRenderer = this.createConditionRenderer(this.functionRenderer.temporalRenderer());
		this.expressionRenderer = this.createExpressionRenderer();
	}
	
	protected @NonNull SqlFunctionRenderer createFunctionRenderer() {
		return SqlFunctionRenderer.builder(this).build();
	}
	
	protected @NonNull SqlConditionRenderer createConditionRenderer(@NonNull SqlTemporalFunctionRenderer temporalFunctionRenderer) {
		return SqlConditionRenderer.builder(this, temporalFunctionRenderer).build();
	}
	
	protected @NonNull SqlExpressionRenderer createExpressionRenderer() {
		return new SqlExpressionRenderer(this);
	}
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return !(type.baseType() instanceof SqlArrayType<?>);
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		if (!this.isTypeSupported(type)) {
			throw new SqlDialectUnsupportedRenderingException("Sql type " + type + " is not supported by dialect " + this.name());
		}
		
		return switch (type.baseType()) {
			case SqlScalarType<?> scalar -> this.getScalarTypeName(scalar.jdbcType());
			case ParameterizedSqlType<?, ?> parameterized -> this.getParameterizedTypeName(parameterized.jdbcType(), parameterized.parameter());
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql type structure: " + type);
		};
	}
	
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BOOLEAN";
			case Types.TINYINT -> "TINYINT";
			case Types.SMALLINT -> "SMALLINT";
			case Types.INTEGER -> "INTEGER";
			case Types.BIGINT -> "BIGINT";
			case Types.REAL -> "REAL";
			case Types.FLOAT -> "FLOAT";
			case Types.DOUBLE -> "DOUBLE PRECISION";
			case Types.LONGVARCHAR -> "TEXT";
			case Types.LONGNVARCHAR, Types.NCLOB -> "NCLOB";
			case Types.CLOB -> "CLOB";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			case Types.DATE -> "DATE";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported JDBC scalar type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		return switch (parameter) {
			case SqlLengthParameter length -> this.getLengthParameterizedTypeName(jdbcType, length);
			case SqlPrecisionParameter precision -> this.getPrecisionParameterizedTypeName(jdbcType, precision);
			case SqlFractionalParameter fractional -> this.getFractionalParameterizedTypeName(jdbcType, fractional);
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported sql parameter type: " + parameter.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getLengthParameterizedTypeName(int jdbcType, @NonNull SqlLengthParameter length) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(length, "Length parameter must not be null");
		
		return switch (jdbcType) {
			case Types.CHAR -> "CHAR(" + length.length() + ")";
			case Types.VARCHAR -> "VARCHAR(" + length.length() + ")";
			case Types.NCHAR -> "NCHAR(" + length.length() + ")";
			case Types.NVARCHAR -> "NVARCHAR(" + length.length() + ")";
			case Types.BINARY -> "BINARY(" + length.length() + ")";
			case Types.VARBINARY -> "VARBINARY(" + length.length() + ")";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported length-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getPrecisionParameterizedTypeName(int jdbcType, @NonNull SqlPrecisionParameter precision) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(precision, "Precision parameter must not be null");
		
		return switch (jdbcType) {
			case Types.NUMERIC -> "NUMERIC(" + precision.precision() + ", " + precision.scale() + ")";
			case Types.DECIMAL -> "DECIMAL(" + precision.precision() + ", " + precision.scale() + ")";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported precision-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getFractionalParameterizedTypeName(int jdbcType, @NonNull SqlFractionalParameter fractional) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(fractional, "Fractional parameter must not be null");
		
		return switch (jdbcType) {
			case Types.TIME -> "TIME(" + fractional.digits() + ")";
			case Types.TIMESTAMP -> "TIMESTAMP(" + fractional.digits() + ")";
			case Types.TIME_WITH_TIMEZONE -> "TIME(" + fractional.digits() + ") WITH TIME ZONE";
			case Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP(" + fractional.digits() + ") WITH TIME ZONE";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported fractional-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	public @NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException {
		return this.expressionRenderer.render(expression);
	}
	
	@Override
	public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException {
		return this.functionRenderer.render(function);
	}
	
	@Override
	public @NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException {
		return this.conditionRenderer.render(condition);
	}
	
	@Override
	public @NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) throws SqlException {
		Objects.requireNonNull(clause, "Sql window clause must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		List<SqlColumn<?, ?>> partitions = clause.partitions();
		if (!partitions.isEmpty()) {
			renderer.partition().by();
			SqlRenderingHelper.renderList(renderer, partitions, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		}
		
		List<SqlOrderable<?>> orderings = clause.orderings();
		if (!orderings.isEmpty()) {
			renderer.orderBy();
			SqlRenderingHelper.renderList(renderer, orderings, this::renderOrderingItem);
		}
		
		SqlWindowFrame frame = clause.frame();
		if (frame != null) {
			renderer.rendered(this.renderWindowFrame(frame));
		}
		return renderer.toSql();
	}
	
	protected void renderOrderingItem(@NonNull SqlRenderer renderer, @NonNull SqlOrderable<?> orderable) throws SqlException {
		Objects.requireNonNull(renderer, "Renderer must not be null");
		Objects.requireNonNull(orderable, "Orderable must not be null");
		
		if (orderable instanceof OrderedSqlExpression<?> ordered) {
			renderer.rendered(this.renderExpression(ordered.expression()));
			renderer.rendered(this.renderOrdering(ordered.ordering(), ordered.nullOrdering()));
		} else if (orderable instanceof SqlExpression<?> expression) {
			renderer.rendered(this.renderExpression(expression));
		} else {
			throw new SqlDialectException("Unknown orderable type: " + orderable.getClass().getName() + " in dialect " + this.name());
		}
	}
	
	@Override
	public @NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) throws SqlException {
		Objects.requireNonNull(frame, "Sql window frame must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		switch (frame) {
			case RowsWindowFrame _ -> renderer.rows();
			case RangeWindowFrame _ -> renderer.range();
			case GroupsWindowFrame _ -> renderer.groups();
			
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql window frame type: " + frame.getClass().getName() + " in dialect " + this.name());
		}
		
		renderer.between().rendered(this.renderFrameBound(frame.start())).and().rendered(this.renderFrameBound(frame.end()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		
		return (switch (bound) {
			case UnboundedPrecedingFrameBound _ -> renderer.unbounded().preceding();
			case PrecedingFrameBound(int offset) -> renderer.parameter(SqlTypes.INTEGER, offset).preceding();
			case CurrentRowFrameBound _ -> renderer.currentRow();
			case FollowingFrameBound(int offset) -> renderer.parameter(SqlTypes.INTEGER, offset).following();
			case UnboundedFollowingFrameBound _ -> renderer.unbounded().following();
			
			case null -> throw new NullPointerException("Sql frame bound must not be null");
			default -> throw new SqlDialectUnsupportedRenderingException("Unknown sql frame bound type: " + bound.getClass().getName() + " in dialect " + this.name());
		}).toSql();
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return false;
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return method == SqlIndexMethod.BTREE;
	}
	
	@Override
	public @NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlException {
		Objects.requireNonNull(method, "Sql index method must not be null");
		
		if (!this.isIndexMethodSupported(method)) {
			throw new SqlDialectUnsupportedRenderingException("Sql index method " + method + " is not supported by dialect " + this.name());
		}
		return method.name();
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}
	
	@Override
	public @NonNull SqlRendered renderQualifiedName(@NonNull String schema, @NonNull String name) throws SqlException {
		Objects.requireNonNull(schema, "Schema must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		
		return SqlRendered.of(this.quoteIdentifier(schema) + "." + this.quoteIdentifier(name));
	}
	
	@Override
	public @NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().table();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		
		renderer.literal(this.quoteIdentifier(table.getName()));
		renderer.openingBracket();
		
		boolean hasCompositeKey = table.getCompositePrimaryKey().isPresent();
		List<? extends SqlColumn<?, ?>> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(this.renderColumnForTable(columns.get(i), hasCompositeKey));
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			renderer.comma().rendered(tableConstraints);
		}
		
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().table();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderColumnDefinition(@NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		return this.renderColumnForTable(column, false);
	}
	
	protected void renderForeignKey(@NonNull SqlRenderer renderer, @NonNull SqlForeignKey<?, ?> fk) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(fk, "Sql foreign key must not be null");
		
		renderer.literal(this.quoteIdentifier(fk.getReferencedTable().getName()));
		renderer.openingBracket();
		
		List<? extends SqlColumn<?, ?>> referencedColumns = fk.getReferencedColumns();
		for (int i = 0; i < referencedColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(referencedColumns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (fk.getOnUpdate() != SqlReferentialAction.NO_ACTION) {
			renderer.on().update();
			this.renderReferentialAction(renderer, fk.getOnUpdate());
		}
		
		if (fk.getOnDelete() != SqlReferentialAction.NO_ACTION) {
			renderer.on().delete();
			this.renderReferentialAction(renderer, fk.getOnDelete());
		}
	}
	
	protected <E, C> @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<E, C> column, boolean skipPrimaryKey) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(this.quoteIdentifier(column.getName()));
		
		try {
			renderer.literal(this.getTypeName(column.getType()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Column type is not supported by dialect " + this.name(), e);
		}
		
		if (!column.isNullable()) {
			renderer.not().null_();
		}
		if (column.getDefaultValue().isPresent()) {
			renderer.default_().parameter(column.getType(), column.getDefaultValue().get());
		}
		if (column.isAutoIncrement()) {
			this.renderAutoIncrement(renderer, column);
		}
		if (column.isPrimaryKey() && !skipPrimaryKey) {
			renderer.primary().key();
		}
		if (column.isUnique()) {
			renderer.unique();
		}
		
		if (column.getForeignKey().isPresent()) {
			renderer.references();
			this.renderForeignKey(renderer, column.getForeignKey().get());
		}
		
		for (SqlCondition check : column.getChecks()) {
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
		}
		return renderer.toSql();
	}
	
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	protected void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(action, "Sql referential action must not be null");
		
		switch (action) {
			case NO_ACTION -> renderer.noAction();
			case RESTRICT -> renderer.restrict();
			case CASCADE -> renderer.cascade();
			case SET_NULL -> renderer.setNull();
			case SET_DEFAULT -> renderer.setDefault();
		}
	}
	
	protected @NonNull SqlRendered renderTableConstraints(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		boolean first = true;
		
		if (table.getCompositePrimaryKey().isPresent()) {
			SqlCompositePrimaryKey<?> pk = table.getCompositePrimaryKey().get();
			
			renderer.primary().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, pk.columns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlForeignKey<?, ?> fk : table.getForeignKeys()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.foreign().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, fk.getReferencingColumns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket().references();
			this.renderForeignKey(renderer, fk);
			
			first = false;
		}
		
		for (List<? extends SqlColumn<?, ?>> uniqueColumns : table.getUniqueConstraints()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.unique().openingBracket();
			SqlRenderingHelper.renderList(renderer, uniqueColumns, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlCondition check : table.getCheckConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
			first = false;
		}
		
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		renderer.index().literal(this.quoteIdentifier(index.name()));
		renderer.on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		
		try {
			renderer.using().literal(this.getIndexMethodName(index.method()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Index method is not supported by dialect " + this.name(), e);
		}
		
		renderer.openingBracket();
		SqlRenderingHelper.renderList(renderer, index.columns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlTable<?> owningTable, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(owningTable, "Sql index owning table must not be null");
		Objects.requireNonNull(indexName, "Sql index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(indexName));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset) throws SqlException {
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (limit >= 0) {
			renderer.limit().literal(String.valueOf(limit));
		}
		if (offset > 0) {
			renderer.offset().literal(String.valueOf(offset));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("RETURNING clause is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql lock mode must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		switch (mode) {
			case FOR_UPDATE -> renderer.for_().update();
			case FOR_SHARE -> renderer.for_().share();
		}
		
		if (skipLocked) {
			renderer.skip().locked();
		}
		
		if (noWait) {
			renderer.nowait();
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) throws SqlException {
		Objects.requireNonNull(operation, "Sql set operation must not be null");
		
		return SqlRendered.of(switch (operation) {
			case UNION -> "UNION";
			case UNION_ALL -> "UNION ALL";
			case INTERSECT -> "INTERSECT";
			case EXCEPT -> "EXCEPT";
		});
	}
	
	@Override
	public @NonNull SqlRendered renderLateralJoin() throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("LATERAL join is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException {
		return SqlRendered.of(value ? "TRUE" : "FALSE");
	}
	
	@Override
	public @NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) throws SqlException {
		Objects.requireNonNull(ordering, "Sql ordering must not be null");
		Objects.requireNonNull(nullOrdering, "Sql null ordering must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		switch (ordering) {
			case ASCENDING -> renderer.asc();
			case DESCENDING -> renderer.desc();
			case DEFAULT -> {}
		}
		
		switch (nullOrdering) {
			case NULLS_FIRST -> renderer.nulls().first();
			case NULLS_LAST -> renderer.nulls().last();
			case DEFAULT -> {}
		}
		
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(name, "Schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().schema();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		return renderer.literal(this.quoteIdentifier(name)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropSchema(@NonNull String name, boolean ifExists, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().schema();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.quoteIdentifier(name));
		
		if (cascade) {
			renderer.cascade();
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException {
		return SqlRenderer.empty().literal("GENERATED").literal("ALWAYS").as().literal("IDENTITY").toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		Objects.requireNonNull(conflictColumn, "Conflict column must not be null");
		Objects.requireNonNull(updateColumns, "Update columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.on().literal("CONFLICT");
		renderer.openingBracket().literal(this.quoteIdentifier(conflictColumn.getName())).closingBracket();
		renderer.literal("DO").update().set();
		
		for (int i = 0; i < updateColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			String quotedName = this.quoteIdentifier(updateColumns.get(i).getName());
			renderer.literal(quotedName).literal("=").literal("EXCLUDED." + quotedName);
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("INSERT OR IGNORE modifier is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException {
		Objects.requireNonNull(conflictColumns, "Conflict columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.on().literal("CONFLICT");
		renderer.openingBracket();
		for (int i = 0; i < conflictColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(conflictColumns.get(i).getName()));
		}
		renderer.closingBracket();
		renderer.literal("DO").literal("NOTHING");
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(newType, "New type must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName)).type().literal(this.getTypeName(newType)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> columnType, boolean nullable) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(columnType, "Column type must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName));
		
		if (nullable) {
			return renderer.drop().not().null_().toSql();
		} else {
			return renderer.set().not().null_().toSql();
		}
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnSetDefault(@NonNull String tableName, @NonNull String columnName, @NonNull String renderedDefault) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(renderedDefault, "Rendered default value must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName)).set().default_().literal(renderedDefault).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnDropDefault(@NonNull String tableName, @NonNull String columnName) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName)).drop().default_().toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull String tableName, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(indexName, "Index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(indexName));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable String tableName, @NonNull String from, @NonNull String to) throws SqlException {
		Objects.requireNonNull(from, "Source index name must not be null");
		Objects.requireNonNull(to, "Target index name must not be null");
		
		return SqlRenderer.empty().alter().index().literal(this.quoteIdentifier(from)).literal("RENAME").to().literal(this.quoteIdentifier(to)).toSql();
	}
}
