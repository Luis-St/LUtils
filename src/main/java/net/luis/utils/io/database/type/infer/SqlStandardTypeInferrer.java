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

package net.luis.utils.io.database.type.infer;

import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpNetwork;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.NClob;
import java.time.*;
import java.util.UUID;

/**
 * A {@link SqlTypeInferrer} that recognizes the built-in java, time and library types supported by {@link SqlTypes}.<br>
 * The runtime class of the value is matched against the known types, applying a default {@link SqlParameter} configuration where one is required, for example a length for strings or a fractional precision for temporal types.<br>
 *
 * @see SqlTypeInferrer
 * @see SqlTypes
 *
 * @author Luis-St
 */
public class SqlStandardTypeInferrer implements SqlTypeInferrer {
	
	/**
	 * The singleton instance of the standard type inferrer.<br>
	 */
	static final SqlStandardTypeInferrer INSTANCE = new SqlStandardTypeInferrer();
	
	/**
	 * Constructs a new standard type inferrer.<br>
	 */
	protected SqlStandardTypeInferrer() {}
	
	/**
	 * Infers the sql type for an enum value based on its declaring class.<br>
	 * The enum is stored using its constant name.<br>
	 *
	 * @param value The enum value to infer the sql type for
	 * @return The sql type that stores the enum by its constant name
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static @NonNull SqlType<?> inferEnumType(@NonNull Enum value) {
		return SqlTypes.enumName(value.getDeclaringClass());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull <T> SqlType<T> inferType(@NonNull T value) throws SqlTypeNotFoundException {
		return (SqlType<T>) this.inferTypeInternal(value);
	}
	
	/**
	 * Resolves the sql type for the given value by matching its runtime type against the known built-in types.<br>
	 *
	 * @param value The value to infer the sql type for
	 * @return The sql type matching the value
	 * @throws NullPointerException If the value is null
	 * @throws SqlTypeNotFoundException If no sql type is known for the value's java type
	 */
	private @NonNull SqlType<?> inferTypeInternal(@NonNull Object value) throws SqlTypeNotFoundException {
		return switch (value) {
			case Boolean _ -> SqlTypes.BOOLEAN;
			case Byte _ -> SqlTypes.BYTE;
			case Short _ -> SqlTypes.SHORT;
			case Integer _ -> SqlTypes.INTEGER;
			case Long _ -> SqlTypes.LONG;
			case BigInteger _ -> SqlTypes.BIG_INTEGER.configure(SqlParameter.precision(38, 0));
			case Float _ -> SqlTypes.REAL;
			case Double _ -> SqlTypes.DOUBLE;
			case BigDecimal _ -> SqlTypes.DECIMAL.configure(SqlParameter.precision(38, 18));
			case Character _ -> SqlTypes.CHARACTER;
			case String _ -> SqlTypes.STRING.configure(SqlParameter.length(255));
			case NClob _ -> SqlTypes.NCLOB;
			case Clob _ -> SqlTypes.CLOB;
			case byte[] _ -> SqlTypes.BYTES.configure(SqlParameter.length(255));
			case UUID _ -> SqlTypes.UUID;
			case LocalDate _ -> SqlTypes.LOCAL_DATE;
			case LocalTime _ -> SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(6));
			case LocalDateTime _ -> SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6));
			case OffsetTime _ -> SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(6));
			case OffsetDateTime _ -> SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6));
			case ZonedDateTime _ -> SqlTypes.ZONED_DATE_TIME.configure(SqlParameter.fractional(6));
			case Instant _ -> SqlTypes.INSTANT.configure(SqlParameter.fractional(6));
			case Year _ -> SqlTypes.YEAR;
			case Month _ -> SqlTypes.MONTH;
			case DayOfWeek _ -> SqlTypes.DAY_OF_WEEK;
			case Duration _ -> SqlTypes.DURATION;
			case JsonElement _ -> SqlTypes.JSON;
			case XmlElement _ -> SqlTypes.XML;
			case IpAddress<?> _ -> SqlTypes.IP_ADDRESS;
			case IpNetwork<?, ?> _ -> SqlTypes.IP_NETWORK;
			case Enum<?> e -> inferEnumType(e);
			
			case null -> throw new NullPointerException("Value must not be null");
			default -> throw new SqlTypeNotFoundException("No SQL type found for Java type: " + value.getClass().getName());
		};
	}
}
