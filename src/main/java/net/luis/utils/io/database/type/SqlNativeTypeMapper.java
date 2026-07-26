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

package net.luis.utils.io.database.type;

import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.Objects;
import java.util.Optional;

/**
 * Maps a {@link SqlNativeType} reported by a jdbc driver back to the matching {@link SqlType} using only portable jdbc type codes.<br>
 * This is the dialect-independent fallback of the type resolution, dialect specific native types are resolved by the dialect itself before this mapper is consulted.<br>
 *
 * @see SqlNativeType
 * @see net.luis.utils.io.database.dialect.SqlDialect#resolveType(SqlNativeType)
 *
 * @author Luis-St
 */
public final class SqlNativeTypeMapper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static utility class.<br>
	 */
	private SqlNativeTypeMapper() {}
	
	/**
	 * Maps the given native type to the matching sql type using its jdbc type code.<br>
	 * The reported column size and decimal digits are used to configure parameterized types such as numeric, string or temporal types.<br>
	 *
	 * @param nativeType The native type to map
	 * @return An optional containing the mapped sql type or an empty optional if the jdbc type code is not supported
	 * @throws NullPointerException If the native type is null
	 */
	public static @NonNull Optional<SqlType<?>> mapNativeType(@NonNull SqlNativeType nativeType) {
		Objects.requireNonNull(nativeType, "Sql native type must not be null");
		
		int columnSize = nativeType.columnSize();
		int decimalDigits = nativeType.decimalDigits();
		
		return Optional.ofNullable(switch (nativeType.jdbcType()) {
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
			case Types.CHAR -> SqlTypes.FIXED_STRING.configure(SqlParameter.length(Math.max(columnSize, 1)));
			case Types.NCHAR -> SqlTypes.UNICODE_FIXED_STRING.configure(SqlParameter.length(Math.max(columnSize, 1)));
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
			case Types.SQLXML -> SqlTypes.XML;
			case Types.DATE -> SqlTypes.LOCAL_DATE;
			case Types.TIME -> SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIMESTAMP -> SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIME_WITH_TIMEZONE -> SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			case Types.TIMESTAMP_WITH_TIMEZONE -> SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(Math.max(decimalDigits, 0)));
			default -> null;
		});
	}
}
