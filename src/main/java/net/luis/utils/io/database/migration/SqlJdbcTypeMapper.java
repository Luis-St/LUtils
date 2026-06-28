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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;

/**
 * Maps JDBC type codes to the library's {@link SqlType}s during schema introspection.<br>
 * Used to reconstruct the appropriate {@link SqlType} from the metadata reported by a database driver.<br>
 *
 * @author Luis-St
 */

final class SqlJdbcTypeMapper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static utility class.<br>
	 */
	private SqlJdbcTypeMapper() {}
	
	/**
	 * Maps the given JDBC type code to the matching {@link SqlType}.<br>
	 * The column size and decimal digits are used to configure parameterized types such as numeric, string or temporal types.<br>
	 *
	 * @param jdbcType The JDBC type code as defined in {@link Types}
	 * @param columnSize The column size used to configure length or precision
	 * @param decimalDigits The number of decimal digits used to configure scale or fractional precision
	 * @return The mapped sql type
	 * @throws SqlSchemaIntrospectionException If the JDBC type code is not supported
	 */
	static @NonNull SqlType<?> mapJdbcType(int jdbcType, int columnSize, int decimalDigits) throws SqlSchemaIntrospectionException {
		return switch (jdbcType) {
			case Types.BIT, Types.BOOLEAN -> SqlTypes.BOOLEAN;
			case Types.TINYINT -> SqlTypes.BYTE;
			case Types.SMALLINT -> SqlTypes.SHORT;
			case Types.INTEGER -> SqlTypes.INTEGER;
			case Types.BIGINT -> SqlTypes.LONG;
			case Types.REAL -> SqlTypes.REAL;
			case Types.FLOAT -> SqlTypes.FLOAT;
			case Types.DOUBLE -> SqlTypes.DOUBLE;
			case Types.NUMERIC -> SqlTypes.NUMERIC.configure(SqlParameter.precision(Math.max(columnSize, 1), Math.max(decimalDigits, 0)));
			case Types.DECIMAL -> SqlTypes.DECIMAL.configure(SqlParameter.precision(Math.max(columnSize, 1), Math.max(decimalDigits, 0)));
			case Types.CHAR, Types.NCHAR -> {
				int length = Math.max(columnSize, 1);
				yield (jdbcType == Types.CHAR ? SqlTypes.FIXED_STRING : SqlTypes.UNICODE_FIXED_STRING).configure(SqlParameter.length(length));
			}
			case Types.VARCHAR -> SqlTypes.STRING.configure(SqlParameter.length(Math.max(columnSize, 1)));
			case Types.NVARCHAR -> SqlTypes.UNICODE_STRING.configure(SqlParameter.length(Math.max(columnSize, 1)));
			case Types.LONGVARCHAR -> SqlTypes.TEXT;
			case Types.LONGNVARCHAR -> SqlTypes.UNICODE_TEXT;
			case Types.BINARY -> SqlTypes.FIXED_BYTES.configure(SqlParameter.length(Math.max(columnSize, 1)));
			case Types.VARBINARY -> SqlTypes.BYTES.configure(SqlParameter.length(Math.max(columnSize, 1)));
			case Types.LONGVARBINARY -> SqlTypes.LARGE_BYTES;
			case Types.CLOB -> SqlTypes.CLOB;
			case Types.NCLOB -> SqlTypes.NCLOB;
			case Types.BLOB -> SqlTypes.BLOB;
			case Types.DATE -> SqlTypes.LOCAL_DATE;
			case Types.TIME -> SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIMESTAMP -> SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIME_WITH_TIMEZONE -> SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIMESTAMP_WITH_TIMEZONE -> SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			default -> throw new SqlSchemaIntrospectionException("Unsupported JDBC type code: " + jdbcType);
		};
	}
	
	/**
	 * Reconstructs a {@link SqlType} from the given JDBC type code and the optional stored parameter.<br>
	 * If the parameter is {@code null} the type is reconstructed as a scalar type, otherwise the parameter is applied to the matching parameterizable type.<br>
	 *
	 * @param jdbcType The JDBC type code as defined in {@link Types}
	 * @param parameter The parameter to apply to the type or {@code null} for a scalar type
	 * @return The reconstructed sql type
	 * @throws SqlSchemaIntrospectionException If the JDBC type code is not supported for the given parameter
	 */
	static @NonNull SqlType<?> reconstructType(int jdbcType, @Nullable SqlParameter parameter) throws SqlSchemaIntrospectionException {
		if (parameter == null) {
			return switch (jdbcType) {
				case Types.BIT, Types.BOOLEAN -> SqlTypes.BOOLEAN;
				case Types.TINYINT -> SqlTypes.BYTE;
				case Types.SMALLINT -> SqlTypes.SHORT;
				case Types.INTEGER -> SqlTypes.INTEGER;
				case Types.BIGINT -> SqlTypes.LONG;
				case Types.REAL -> SqlTypes.REAL;
				case Types.FLOAT -> SqlTypes.FLOAT;
				case Types.DOUBLE -> SqlTypes.DOUBLE;
				case Types.LONGVARCHAR -> SqlTypes.TEXT;
				case Types.LONGNVARCHAR -> SqlTypes.UNICODE_TEXT;
				case Types.LONGVARBINARY -> SqlTypes.LARGE_BYTES;
				case Types.CLOB -> SqlTypes.CLOB;
				case Types.NCLOB -> SqlTypes.NCLOB;
				case Types.BLOB -> SqlTypes.BLOB;
				case Types.DATE -> SqlTypes.LOCAL_DATE;
				default -> throw new SqlSchemaIntrospectionException("Unsupported scalar JDBC type code: " + jdbcType);
			};
		}
		
		ParameterizableSqlType<?, ?> base = switch (jdbcType) {
			case Types.NUMERIC -> SqlTypes.NUMERIC;
			case Types.DECIMAL -> SqlTypes.DECIMAL;
			case Types.CHAR -> SqlTypes.FIXED_STRING;
			case Types.NCHAR -> SqlTypes.UNICODE_FIXED_STRING;
			case Types.VARCHAR -> SqlTypes.STRING;
			case Types.NVARCHAR -> SqlTypes.UNICODE_STRING;
			case Types.BINARY -> SqlTypes.FIXED_BYTES;
			case Types.VARBINARY -> SqlTypes.BYTES;
			case Types.TIME -> SqlTypes.LOCAL_TIME;
			case Types.TIMESTAMP -> SqlTypes.LOCAL_DATE_TIME;
			case Types.TIME_WITH_TIMEZONE -> SqlTypes.OFFSET_TIME;
			case Types.TIMESTAMP_WITH_TIMEZONE -> SqlTypes.OFFSET_DATE_TIME;
			default -> throw new SqlSchemaIntrospectionException("Unsupported parameterized JDBC type code: " + jdbcType);
		};
		return configureUnsafe(base, parameter);
	}
	
	/**
	 * Configures the given parameterizable type with the given parameter using an unchecked cast.<br>
	 * The cast is required because the concrete parameter type of the base type is not statically known.<br>
	 *
	 * @param base The parameterizable type to configure
	 * @param parameter The parameter to apply to the type
	 * @param <T> The value type of the parameterizable type
	 * @param <P> The parameter type accepted by the parameterizable type
	 * @return The configured sql type
	 */
	@SuppressWarnings("unchecked")
	private static <T, P extends SqlParameter> @NonNull SqlType<T> configureUnsafe(@NonNull ParameterizableSqlType<T, ?> base, @NonNull SqlParameter parameter) {
		return ((ParameterizableSqlType<T, P>) base).configure((P) parameter);
	}
}
