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

import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFunctionException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.function.SqlDefaultFunctionType;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.parameter.SqlFractionalParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

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
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BIT";
			case Types.TINYINT -> "TINYINT";
			case Types.LONGVARCHAR -> "NVARCHAR(MAX)";
			case Types.LONGNVARCHAR -> "NVARCHAR(MAX)";
			case Types.CLOB -> "VARCHAR(MAX)";
			case Types.NCLOB -> "NVARCHAR(MAX)";
			case Types.LONGVARBINARY -> "VARBINARY(MAX)";
			case Types.BLOB -> "VARBINARY(MAX)";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
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
		Objects.requireNonNull(feature, "Feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "[" + identifier.replace("]", "]]") + "]";
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) {
		renderer.keyword("IDENTITY").openingBracket().literal("1").comma().literal("1").closingBracket();
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) {
		Objects.requireNonNull(index, "Index must not be null");
		SqlRenderer renderer = new SqlRenderer();
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
		List<? extends SqlColumn<?, ?>> columns = index.columns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(columns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this));
		}
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index) {
		Objects.requireNonNull(index, "Index must not be null");
		SqlRenderer renderer = new SqlRenderer();
		renderer.drop().index().literal(this.quoteIdentifier(index.name()))
			.on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = new SqlRenderer();
		renderer.offset().literal(String.valueOf(offset)).rows();
		if (limit > 0) {
			renderer.fetch().next().literal(String.valueOf(limit)).rows().only();
		}
		return renderer.toSql(this);
	}
	
	@Override
	public @NonNull String renderBooleanLiteral(boolean value) {
		return value ? "1" : "0";
	}
	
	@Override
	protected @NonNull SqlRendered renderDefaultFunction(@NonNull SqlDefaultFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException {
		return switch (type) {
			case RANDOM -> SimpleSqlRendered.of("RAND()");
			case CONCAT -> this.renderFunctionCall("CONCAT", arguments);
			case CONCAT_WITH_SEPARATOR -> this.renderFunctionCall("CONCAT_WS", arguments);
			case LENGTH -> this.renderFunctionCall("LEN", arguments);
			case TRUNCATE -> this.renderFunctionCall("ROUND", arguments);
			default -> super.renderDefaultFunction(type, arguments);
		};
	}
}
