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

import net.luis.utils.io.data.json.*;
import net.luis.utils.io.data.xml.XmlConfig;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.condition.conditions.comparison.SqlInListCondition;
import net.luis.utils.io.database.condition.conditions.string.SqlEqualsIgnoreCaseCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlComparisonConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlStringConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlNumericFunctionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class PostgresSqlDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.RETURNING,
		SqlFeature.LATERAL_JOIN,
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT,
		SqlFeature.TRUNCATE_CASCADE,
		SqlFeature.IS_DISTINCT_FROM,
		SqlFeature.UPSERT_SUFFIX,
		SqlFeature.TRANSACTIONAL_DDL,
		SqlFeature.ROW_LOCKING,
		SqlFeature.INSERT_OR_IGNORE,
		SqlFeature.RENAME_INDEX,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT,
		SqlFeature.ARRAY_TYPE
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH,
		SqlIndexMethod.GIN,
		SqlIndexMethod.GIST,
		SqlIndexMethod.BRIN,
		SqlIndexMethod.SPGIST
	);
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.UUID, "UUID",
			(statement, index, value) -> statement.setObject(index, value, Types.OTHER),
			(resultSet, index) -> resultSet.getObject(index, UUID.class)
		)
		.register(SqlTypes.JSON, "JSONB",
			(statement, index, value) -> statement.setObject(index, value == null ? null : ((JsonElement) value).toString(JsonConfig.DEFAULT), Types.OTHER),
			(resultSet, index) -> {
				String json = resultSet.getString(index);
				if (json == null) {
					return null;
				}
				
				try (JsonReader reader = new JsonReader(json)) {
					return reader.readJson();
				}
			})
		.register(SqlTypes.XML, "XML", (statement, index, value) -> {
			if (value == null) {
				statement.setNull(index, Types.SQLXML);
				return;
			}
			
			SQLXML xml = statement.getConnection().createSQLXML();
			xml.setString(((XmlElement) value).toString(XmlConfig.DEFAULT));
			statement.setSQLXML(index, xml);
		}, (resultSet, index) -> readXmlElement(resultSet.getSQLXML(index)))
		.register(SqlTypes.IP_ADDRESS, "INET", (statement, index, value) -> statement.setObject(index, value == null ? null : value.toString(), Types.OTHER))
		.register(SqlTypes.IP_NETWORK, "CIDR", (statement, index, value) -> statement.setObject(index, value == null ? null : value.toString(), Types.OTHER))
		.build();
	
	@Override
	public @NonNull String name() {
		return "PostgreSQL";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.temporalFunctionRenderer(new PostgresSqlTemporalFunctionRenderer(this))
			.numericFunctionRenderer(new PostgresSqlNumericFunctionRenderer(this))
			.stringConditionRenderer(new PostgresSqlStringConditionRenderer(this))
			.comparisonConditionRenderer(new PostgresSqlComparisonConditionRenderer(this))
			.tableRenderer(new PostgresSqlTableRenderer(this))
			.build();
	}
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return true;
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		
		if (type.baseType() instanceof SqlArrayType<?> arrayType) {
			return this.getTypeName(arrayType.elementType()) + "[]";
		}
		return super.getTypeName(type);
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.LONGVARBINARY, Types.BLOB -> "BYTEA";
			case Types.LONGNVARCHAR, Types.NCLOB, Types.CLOB -> "TEXT";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getLengthParameterizedTypeName(int jdbcType, @NonNull SqlLengthParameter length) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.NCHAR -> "CHAR(" + length.length() + ")";
			case Types.NVARCHAR -> "VARCHAR(" + length.length() + ")";
			default -> super.getLengthParameterizedTypeName(jdbcType, length);
		};
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		return this.renderStandardReturning(columns);
	}
	
	@Override
	public @NonNull SqlRendered renderLateralJoin() {
		return SqlRendered.of("LATERAL");
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "SELECT con.conname, pg_get_constraintdef(con.oid) FROM pg_constraint con " +
			"JOIN pg_class rel ON rel.oid = con.conrelid " +
			"JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace " +
			"WHERE con.contype = 'c' AND nsp.nspname = ? AND rel.relname = ?";
	}
}

class PostgresSqlComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
	PostgresSqlComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	//region Static helper methods
	
	private static @Nullable SqlArrayType<?> arrayTypeOf(@NonNull List<SqlExpression<?>> options) {
		SqlType<?> elementType = null;
		for (SqlExpression<?> option : options) {
			if (!(option instanceof SqlValueExpression<?> value)) {
				return null;
			}
			
			if (elementType == null) {
				elementType = value.type();
			} else if (!elementType.equals(value.type())) {
				return null;
			}
		}
		
		return switch (elementType) {
			case SqlScalarType<?> scalar -> scalar.array();
			case ParameterizedSqlType<?, ?> parameterized -> parameterized.array();
			case null, default -> null;
		};
	}
	
	private static <E> void bindArray(@NonNull SqlRenderer renderer, @NonNull SqlArrayType<E> arrayType, @NonNull List<SqlExpression<?>> options) {
		Class<E> elementClass = arrayType.elementType().javaType();
		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(elementClass, options.size());
		for (int i = 0; i < options.size(); i++) {
			array[i] = elementClass.cast(((SqlValueExpression<?>) options.get(i)).value());
		}
		renderer.parameter(arrayType, array);
	}
	//endregion
	
	@Override
	protected @NonNull SqlRendered renderInList(@NonNull SqlInListCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlArrayType<?> arrayType = arrayTypeOf(condition.options());
		if (arrayType == null) {
			return super.renderInList(condition);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).literal("=").any().openingBracket();
		bindArray(renderer, arrayType, condition.options());
		renderer.closingBracket();
		return renderer.toSql();
	}
}

class PostgresSqlTableRenderer extends SqlTableRenderer {
	
	PostgresSqlTableRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("BY").default_().as().keyword("IDENTITY");
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.dialect.quoteIdentifier(table.name())).cascade();
		return renderer.toSql();
	}
}

class PostgresSqlTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	PostgresSqlTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TO_TIMESTAMP", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeDate(@NonNull SqlMakeDateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAKE_DATE", function.year(), function.month(), function.day());
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeTime(@NonNull SqlMakeTimeFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAKE_TIME", function.hour(), function.minute(), function.second());
	}
}

class PostgresSqlStringConditionRenderer extends SqlStringConditionRenderer {
	
	PostgresSqlStringConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderEqualsIgnoreCase(@NonNull SqlEqualsIgnoreCaseCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.first().toSql(this.dialect)).literal("ILIKE").rendered(condition.second().toSql(this.dialect));
		return renderer.toSql();
	}
}

class PostgresSqlNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	PostgresSqlNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<Integer> zero = new SqlValueExpression<>(0, SqlTypes.INTEGER);
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRUNC", function.expression(), zero);
	}
}
