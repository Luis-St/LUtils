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

import net.luis.utils.io.database.dialect.rendering.base.*;
import net.luis.utils.io.database.dialect.rendering.base.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.dialect.rendering.sqlserver.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.parameter.SqlFractionalParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlServerDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.NO_WAIT
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH,
		SqlIndexMethod.CLUSTERED,
		SqlIndexMethod.NONCLUSTERED,
		SqlIndexMethod.COLUMNSTORE
	);
	
	@Override
	public @NonNull String name() {
		return "SQL Server";
	}
	
	@Override
	protected @NonNull SqlFunctionRenderer createFunctionRenderer() {
		return SqlFunctionRenderer.builder(this)
			.string(new SqlServerStringFunctionRenderer(this))
			.numeric(new SqlServerNumericFunctionRenderer(this))
			.temporal(new SqlServerTemporalFunctionRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull SqlConditionRenderer createConditionRenderer(@NonNull SqlTemporalFunctionRenderer temporalFunctionRenderer) {
		return SqlConditionRenderer.builder(this, temporalFunctionRenderer)
			.string(new SqlServerStringConditionRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BIT";
			case Types.TINYINT -> "TINYINT";
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCLOB -> "NVARCHAR(MAX)";
			case Types.CLOB -> "VARCHAR(MAX)";
			case Types.LONGVARBINARY, Types.BLOB -> "VARBINARY(MAX)";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlFractionalParameter fractional) {
			return switch (jdbcType) {
				case Types.TIMESTAMP -> "DATETIME2(" + fractional.digits() + ")";
				case Types.TIMESTAMP_WITH_TIMEZONE -> "DATETIMEOFFSET(" + fractional.digits() + ")";
				default -> super.getParameterizedTypeName(jdbcType, parameter);
			};
		}
		return super.getParameterizedTypeName(jdbcType, parameter);
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
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "[" + identifier.replace("]", "]]") + "]";
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("IDENTITY").openingBracket().literal("1").comma().literal("1").closingBracket();
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		if (index.method() == SqlIndexMethod.CLUSTERED) {
			renderer.keyword("CLUSTERED");
		} else if (index.method() == SqlIndexMethod.NONCLUSTERED) {
			renderer.keyword("NONCLUSTERED");
		}
		
		renderer.index().literal(this.quoteIdentifier(index.name()));
		renderer.on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		renderer.openingBracket();
		SqlRenderingHelper.renderList(renderer, index.columns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this));
		}
		return renderer.toSql();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlTable<?> owningTable, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(owningTable, "Sql index owning table must not be null");
		Objects.requireNonNull(indexName, "Sql index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(indexName)).on().literal(this.quoteIdentifier(owningTable.getName()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset) {
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.offset().literal(String.valueOf(offset)).rows();
		if (limit >= 0) {
			renderer.fetch().next().literal(String.valueOf(limit)).rows().only();
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException {
		return SqlRendered.of(value ? "1" : "0");
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("Upsert via INSERT is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("INSERT OR IGNORE is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("INSERT OR IGNORE is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(newType, "New type must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName)).literal(this.getTypeName(newType)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> columnType, boolean nullable) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(columnType, "Column type must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.quoteIdentifier(tableName)).alter().column().literal(this.quoteIdentifier(columnName)).literal(this.getTypeName(columnType));
		
		if (nullable) {
			return renderer.null_().toSql();
		} else {
			return renderer.not().null_().toSql();
		}
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull String tableName, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(indexName, "Index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(indexName)).on().literal(this.quoteIdentifier(tableName));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable String tableName, @NonNull String from, @NonNull String to) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("RENAME INDEX is not supported by dialect " + this.name());
	}
}
