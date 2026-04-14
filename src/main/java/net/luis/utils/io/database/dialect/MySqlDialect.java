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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.numeric.SqlRandomFunction;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
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
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCLOB, Types.CLOB -> "LONGTEXT";
			case Types.LONGVARBINARY, Types.BLOB -> "LONGBLOB";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
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
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderConcatFunction(@NonNull SqlConcatFunction<?> function) {
		Objects.requireNonNull(function, "Concat function must not be null");
		
		return renderer -> {
			List<? extends SqlExpression<? extends CharSequence>> values = function.values();
			Optional<String> separator = function.separator();
			boolean distinct = function.distinct();
			boolean ordered = function.ordered();
			
			if (distinct || ordered) {
				renderer.literal(separator.isEmpty() ? "CONCAT" : "CONCAT_WS").openingBracket();
				
				if (distinct) {
					renderer.distinct();
				}
				
				SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
				if (first != null) {
					renderer.rendered(first.toSql(this));
				}
				
				renderer.comma().parameter(separator.orElse(""));
				
				if (ordered && first != null) {
					renderer.orderBy().rendered(first.toSql(this));
				}
				renderer.closingBracket();
			} else {
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						renderer.literal("||");
						separator.ifPresent(s -> renderer.parameter(s).literal("||"));
					}
					
					renderer.rendered(values.get(i).toSql(this));
				}
			}
			
			return renderer.toSql();
		};
	}
	
	@Override
	public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException {
		if (function instanceof SqlRandomFunction) {
			return this.renderLiteral("RAND()").apply(SqlRenderer.empty());
		}
		return super.renderFunction(function);
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
		return "`" + identifier.replace("`", "``") + "`";
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.literal("AUTO_INCREMENT");
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(index.name())).on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		return renderer.toSql();
	}
}
