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

import com.google.common.collect.Lists;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.data.xml.XmlReader;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.*;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.function.window.frame.*;
import net.luis.utils.io.database.function.window.frame.bound.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.SqlCheckConstraintInfo;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.*;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("SqlSourceToSinkFlow")
public abstract class AbstractSqlDialect implements SqlDialect {
	
	private static final String SCHEMA_COLUMNS_TABLE = "_sql_schema_columns";
	private static final String SCHEMA_CHECK_CONSTRAINTS_TABLE = "_sql_schema_check_constraints";
	private final SqlTypeRegistry typeRegistry;
	private final SqlDialectRenderer renderer;
	
	protected AbstractSqlDialect() {
		this.typeRegistry = this.createTypeRegistry();
		this.renderer = this.createRenderer();
	}
	
	protected static @Nullable XmlElement readXmlElement(@Nullable SQLXML xml) throws SQLException {
		if (xml == null) {
			return null;
		}
		
		String content = xml.getString();
		String document = content.stripLeading().startsWith("<?xml") ? content : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + content;
		try (XmlReader reader = new XmlReader(document)) {
			reader.readDeclaration();
			return reader.readXmlElement();
		}
	}
	
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return SqlTypeRegistry.empty();
	}
	
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this).build();
	}
	
	@Override
	public @NonNull SqlTableRenderer tableRenderer() {
		return this.renderer.tableRenderer();
	}
	
	@Override
	public @NonNull SqlIndexRenderer indexRenderer() {
		return this.renderer.indexRenderer();
	}
	
	@Override
	public @NonNull SqlColumnRenderer columnRenderer() {
		return this.renderer.columnRenderer();
	}
	
	@Override
	public @NonNull SqlMigrationOperationRenderer migrationRenderer() {
		return this.renderer.migrationRenderer();
	}
	
	@Override
	public @NonNull SqlSchemaRenderer schemaRenderer() {
		return this.renderer.schemaRenderer();
	}
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		if (this.typeRegistry.resolve(type).isPresent()) {
			return true;
		}
		
		return this.resolveTypeName(type.baseType()).isPresent();
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		
		Optional<SqlTypeMapping> mapping = this.typeRegistry.resolve(type);
		if (mapping.isPresent()) {
			return mapping.get().nativeTypeName();
		}
		
		return this.resolveTypeName(type.baseType()).orElseThrow(
			() -> new SqlDialectUnsupportedRenderingException("Sql type " + type + " is not supported by dialect " + this.name())
		);
	}
	
	private @NonNull Optional<String> resolveTypeName(@NonNull SqlType<?> baseType) {
		return switch (baseType) {
			case SqlScalarType<?> scalar -> this.getScalarTypeName(scalar.jdbcType());
			case ParameterizedSqlType<?, ?> parameterized -> this.getParameterizedTypeName(parameterized.jdbcType(), parameterized.parameter());
			default -> Optional.empty();
		};
	}
	
	@Override
	public @NonNull Optional<SqlValueBinder> bindingOverride(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return this.typeRegistry.resolve(type).map(SqlTypeMapping::binder);
	}
	
	@Override
	public @NonNull Optional<SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return this.typeRegistry.resolve(type).map(SqlTypeMapping::reader);
	}
	
	protected @NonNull Optional<String> getScalarTypeName(int jdbcType) {
		return Optional.ofNullable(switch (jdbcType) {
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
			default -> null;
		});
	}
	
	protected @NonNull Optional<String> getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		return switch (parameter) {
			case SqlLengthParameter length -> this.getLengthParameterizedTypeName(jdbcType, length);
			case SqlPrecisionParameter precision -> this.getPrecisionParameterizedTypeName(jdbcType, precision);
			case SqlFractionalParameter fractional -> this.getFractionalParameterizedTypeName(jdbcType, fractional);
			default -> Optional.empty();
		};
	}
	
	protected @NonNull Optional<String> getLengthParameterizedTypeName(int jdbcType, @NonNull SqlLengthParameter length) {
		Objects.requireNonNull(length, "Length parameter must not be null");
		
		return Optional.ofNullable(switch (jdbcType) {
			case Types.CHAR -> "CHAR(" + length.length() + ")";
			case Types.VARCHAR -> "VARCHAR(" + length.length() + ")";
			case Types.NCHAR -> "NCHAR(" + length.length() + ")";
			case Types.NVARCHAR -> "NVARCHAR(" + length.length() + ")";
			case Types.BINARY -> "BINARY(" + length.length() + ")";
			case Types.VARBINARY -> "VARBINARY(" + length.length() + ")";
			default -> null;
		});
	}
	
	protected @NonNull Optional<String> getPrecisionParameterizedTypeName(int jdbcType, @NonNull SqlPrecisionParameter precision) {
		Objects.requireNonNull(precision, "Precision parameter must not be null");
		
		return Optional.ofNullable(switch (jdbcType) {
			case Types.NUMERIC -> "NUMERIC(" + precision.precision() + ", " + precision.scale() + ")";
			case Types.DECIMAL -> "DECIMAL(" + precision.precision() + ", " + precision.scale() + ")";
			default -> null;
		});
	}
	
	protected @NonNull Optional<String> getFractionalParameterizedTypeName(int jdbcType, @NonNull SqlFractionalParameter fractional) {
		Objects.requireNonNull(fractional, "Fractional parameter must not be null");
		
		return Optional.ofNullable(switch (jdbcType) {
			case Types.TIME -> "TIME(" + fractional.digits() + ")";
			case Types.TIMESTAMP -> "TIMESTAMP(" + fractional.digits() + ")";
			case Types.TIME_WITH_TIMEZONE -> "TIME(" + fractional.digits() + ") WITH TIME ZONE";
			case Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP(" + fractional.digits() + ") WITH TIME ZONE";
			default -> null;
		});
	}
	
	@Override
	public @NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException {
		return this.renderer.expressionRenderer().render(expression);
	}
	
	@Override
	public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException {
		return this.renderer.functionRenderer().render(function);
	}
	
	@Override
	public @NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException {
		return this.renderer.conditionRenderer().render(condition);
	}
	
	@Override
	public @NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) throws SqlException {
		Objects.requireNonNull(clause, "Sql window clause must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		List<SqlColumn<?, ?>> partitions = clause.partitions();
		if (!partitions.isEmpty()) {
			renderer.partition().by();
			SqlRenderingHelper.renderList(renderer, partitions, (r, column) -> r.literal(this.quoteIdentifier(column.name())));
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
			
			default -> throw new SqlDialectUnknownConstructException("Unknown sql window frame type: " + frame.getClass().getName() + " in dialect " + this.name());
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
			default -> throw new SqlDialectUnknownConstructException("Unknown sql frame bound type: " + bound.getClass().getName() + " in dialect " + this.name());
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
	public @NonNull String renderValueLiteral(@NonNull Object value) throws SqlException {
		Objects.requireNonNull(value, "Value must not be null");
		if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof Boolean b) {
			return this.renderBooleanLiteral(b).sql();
		} else {
			return "'" + value.toString().replace("'", "''") + "'";
		}
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}
	
	@Override
	public void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException {
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
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) throws SqlException {
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (offset > 0 && limit < 0 && !this.isFeatureSupported(SqlFeature.OFFSET_WITHOUT_LIMIT)) {
			throw new SqlDialectFeatureException(SqlFeature.OFFSET_WITHOUT_LIMIT, this);
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
		throw new SqlDialectFeatureException(SqlFeature.RETURNING, this);
	}
	
	protected @NonNull SqlRendered renderStandardReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.returning();
		if (columns.isEmpty()) {
			renderer.literal("*");
		} else {
			SqlRenderingHelper.renderList(renderer, columns, (r, column) -> r.literal(this.quoteIdentifier(column.name())));
		}
		return renderer.toSql();
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
		throw new SqlDialectFeatureException(SqlFeature.LATERAL_JOIN, this);
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
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		Objects.requireNonNull(conflictColumn, "Sql conflict column must not be null");
		Objects.requireNonNull(updateColumns, "Sql update columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.on().literal("CONFLICT");
		renderer.openingBracket().literal(this.quoteIdentifier(conflictColumn.name())).closingBracket();
		renderer.literal("DO").update().set();
		
		for (int i = 0; i < updateColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			String quotedName = this.quoteIdentifier(updateColumns.get(i).name());
			renderer.literal(quotedName).literal("=").literal("EXCLUDED." + quotedName);
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.UPSERT, this);
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.INSERT_OR_IGNORE, this);
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException {
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.on().literal("CONFLICT");
		renderer.openingBracket();
		for (int i = 0; i < conflictColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(conflictColumns.get(i).name()));
		}
		renderer.closingBracket();
		renderer.literal("DO").literal("NOTHING");
		return renderer.toSql();
	}
	
	@Override
	public @NonNull List<SqlCheckConstraintInfo> getCheckConstraints(@NonNull Connection connection, @NonNull String schema, @NonNull String tableName) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(schema, "Sql schema must not be null");
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		
		String sqlQuery = this.getCheckConstraintsQueryString();
		if (sqlQuery == null) {
			return List.of();
		}
		
		List<SqlCheckConstraintInfo> results = Lists.newArrayList();
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
			statement.setString(1, schema);
			statement.setString(2, tableName);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					results.add(new SqlCheckConstraintInfo(resultSet.getString(1), resultSet.getString(2)));
				}
			}
		} catch (SQLException e) {
			throw new SqlSchemaIntrospectionException("Failed to query check constraints for table " + tableName, e);
		}
		return List.copyOf(results);
	}
	
	@Language("SQL")
	protected abstract @Nullable String getCheckConstraintsQueryString();
	
	@Override
	public @NonNull String getCreateSchemaColumnsTableSql() throws SqlException {
		String table = this.quoteIdentifier(SCHEMA_COLUMNS_TABLE);
		String version = this.quoteIdentifier("version");
		String tableName = this.quoteIdentifier("table_name");
		String columnName = this.quoteIdentifier("column_name");
		String jdbcType = this.quoteIdentifier("jdbc_type");
		String length = this.quoteIdentifier("length");
		String precision = this.quoteIdentifier("precision");
		String scale = this.quoteIdentifier("scale");
		String fractional = this.quoteIdentifier("fractional");
		String isNullable = this.quoteIdentifier("is_nullable");
		String isAutoIncrement = this.quoteIdentifier("is_auto_increment");
		String isPrimaryKey = this.quoteIdentifier("is_primary_key");
		String isUnique = this.quoteIdentifier("is_unique");
		String ordinalPosition = this.quoteIdentifier("ordinal_position");
		
		String varchar64 = this.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(64)));
		String varchar256 = this.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(256)));
		String intType = this.getTypeName(SqlTypes.INTEGER);
		String boolType = this.getTypeName(SqlTypes.BOOLEAN);
		
		return "CREATE TABLE IF NOT EXISTS " + table + " (" +
			version + " " + varchar64 + " NOT NULL, " +
			tableName + " " + varchar256 + " NOT NULL, " +
			columnName + " " + varchar256 + " NOT NULL, " +
			jdbcType + " " + intType + " NOT NULL, " +
			length + " " + intType + " NULL, " +
			precision + " " + intType + " NULL, " +
			scale + " " + intType + " NULL, " +
			fractional + " " + intType + " NULL, " +
			isNullable + " " + boolType + " NOT NULL, " +
			isAutoIncrement + " " + boolType + " NOT NULL, " +
			isPrimaryKey + " " + boolType + " NOT NULL, " +
			isUnique + " " + boolType + " NOT NULL, " +
			ordinalPosition + " " + intType + " NOT NULL, " +
			"PRIMARY KEY (" + version + ", " + tableName + ", " + columnName + ")" +
			")";
	}
	
	@Override
	public @NonNull String getCreateSchemaCheckConstraintsTableSql() throws SqlException {
		String table = this.quoteIdentifier(SCHEMA_CHECK_CONSTRAINTS_TABLE);
		String version = this.quoteIdentifier("version");
		String tableName = this.quoteIdentifier("table_name");
		String constraintName = this.quoteIdentifier("constraint_name");
		String checkClause = this.quoteIdentifier("check_clause");
		
		String varchar64 = this.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(64)));
		String varchar256 = this.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(256)));
		String textType = this.getTypeName(SqlTypes.TEXT);
		
		return "CREATE TABLE IF NOT EXISTS " + table + " (" +
			version + " " + varchar64 + " NOT NULL, " +
			tableName + " " + varchar256 + " NOT NULL, " +
			constraintName + " " + varchar256 + " NOT NULL, " +
			checkClause + " " + textType + " NOT NULL, " +
			"PRIMARY KEY (" + version + ", " + tableName + ", " + constraintName + ")" +
			")";
	}
	
	@Override
	public @NonNull String getInsertSchemaColumnSql() {
		String table = this.quoteIdentifier(SCHEMA_COLUMNS_TABLE);
		String version = this.quoteIdentifier("version");
		String tableName = this.quoteIdentifier("table_name");
		String columnName = this.quoteIdentifier("column_name");
		String jdbcType = this.quoteIdentifier("jdbc_type");
		String length = this.quoteIdentifier("length");
		String precision = this.quoteIdentifier("precision");
		String scale = this.quoteIdentifier("scale");
		String fractional = this.quoteIdentifier("fractional");
		String isNullable = this.quoteIdentifier("is_nullable");
		String isAutoIncrement = this.quoteIdentifier("is_auto_increment");
		String isPrimaryKey = this.quoteIdentifier("is_primary_key");
		String isUnique = this.quoteIdentifier("is_unique");
		String ordinalPosition = this.quoteIdentifier("ordinal_position");
		
		return "INSERT INTO " + table + " (" +
			version + ", " + tableName + ", " + columnName + ", " +
			jdbcType + ", " + length + ", " + precision + ", " + scale + ", " + fractional + ", " +
			isNullable + ", " + isAutoIncrement + ", " + isPrimaryKey + ", " + isUnique + ", " +
			ordinalPosition +
			") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	@Override
	public @NonNull String getInsertSchemaCheckConstraintSql() {
		String table = this.quoteIdentifier(SCHEMA_CHECK_CONSTRAINTS_TABLE);
		String version = this.quoteIdentifier("version");
		String tableName = this.quoteIdentifier("table_name");
		String constraintName = this.quoteIdentifier("constraint_name");
		String checkClause = this.quoteIdentifier("check_clause");
		
		return "INSERT INTO " + table + " (" +
			version + ", " + tableName + ", " + constraintName + ", " + checkClause +
			") VALUES (?, ?, ?, ?)";
	}
	
	@Override
	public @NonNull String getSelectSchemaColumnsSql() {
		String table = this.quoteIdentifier(SCHEMA_COLUMNS_TABLE);
		String version = this.quoteIdentifier("version");
		
		return "SELECT * FROM " + table + " WHERE " + version + " = ? ORDER BY " +
			this.quoteIdentifier("table_name") + ", " +
			this.quoteIdentifier("ordinal_position");
	}
	
	@Override
	public @NonNull String getSelectSchemaCheckConstraintsSql() {
		String table = this.quoteIdentifier(SCHEMA_CHECK_CONSTRAINTS_TABLE);
		String version = this.quoteIdentifier("version");
		
		return "SELECT * FROM " + table + " WHERE " + version + " = ?";
	}
	
	@Override
	public @NonNull String getDeleteSchemaColumnsSql() {
		String table = this.quoteIdentifier(SCHEMA_COLUMNS_TABLE);
		String version = this.quoteIdentifier("version");
		
		return "DELETE FROM " + table + " WHERE " + version + " = ?";
	}
	
	@Override
	public @NonNull String getDeleteSchemaCheckConstraintsSql() {
		String table = this.quoteIdentifier(SCHEMA_CHECK_CONSTRAINTS_TABLE);
		String version = this.quoteIdentifier("version");
		
		return "DELETE FROM " + table + " WHERE " + version + " = ?";
	}
}
