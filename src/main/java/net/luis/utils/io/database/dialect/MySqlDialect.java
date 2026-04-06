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
import net.luis.utils.io.database.rendering.SimpleSqlRendered;
import net.luis.utils.io.database.rendering.SqlRendered;
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

public class MySqlDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH
	);
	
	@Override
	public @NonNull String name() {
		return "MySQL";
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "TINYINT(1)";
			case Types.TINYINT -> "TINYINT";
			case Types.LONGVARCHAR -> "LONGTEXT";
			case Types.LONGNVARCHAR -> "LONGTEXT";
			case Types.CLOB -> "LONGTEXT";
			case Types.NCLOB -> "LONGTEXT";
			case Types.LONGVARBINARY -> "LONGBLOB";
			case Types.BLOB -> "LONGBLOB";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		if (parameter instanceof SqlFractionalParameter fractional) {
			return switch (jdbcType) {
				case Types.TIMESTAMP_WITH_TIMEZONE -> "DATETIME(" + fractional.digits() + ")";
				case Types.TIME_WITH_TIMEZONE -> "TIME(" + fractional.digits() + ")";
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
		return "`" + identifier.replace("`", "``") + "`";
	}
	
	@Override
	protected @NonNull String renderAutoIncrement(@NonNull SqlColumn<?, ?> column) {
		return " AUTO_INCREMENT";
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index) {
		Objects.requireNonNull(index, "Index must not be null");
		
		return SimpleSqlRendered.of(
			"DROP INDEX " + this.quoteIdentifier(index.name()) + " ON " + this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName())
		);
	}
	
	@Override
	protected @NonNull SqlRendered renderDefaultFunction(@NonNull SqlDefaultFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException {
		return switch (type) {
			case RANDOM -> SimpleSqlRendered.of("RAND()");
			case CONCAT -> this.renderFunctionCall("CONCAT", arguments);
			case CONCAT_WITH_SEPARATOR -> this.renderFunctionCall("CONCAT_WS", arguments);
			default -> super.renderDefaultFunction(type, arguments);
		};
	}
}
