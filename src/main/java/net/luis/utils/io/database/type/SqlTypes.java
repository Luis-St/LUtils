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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.json.*;
import net.luis.utils.io.data.xml.*;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.type.parameter.*;
import net.luis.utils.io.network.address.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * A holder of the predefined standard {@link SqlType} constants supported by the database system.<br>
 * It provides ready-to-use types for booleans, numbers, strings, binary data, temporal values and common object types,
 * as well as factory methods for mapping enums to sql types.<br>
 *
 * @see SqlType
 *
 * @author Luis-St
 */
public final class SqlTypes {
	
	/**
	 * The sql type for a boolean value.
	 */
	public static final SqlScalarType<Boolean> BOOLEAN = new SqlScalarType<>(Types.BOOLEAN, Boolean.class);
	
	/**
	 * The sql type for a byte value, mapped to a tiny integer.
	 */
	public static final SqlScalarType<Byte> BYTE = new SqlScalarType<>(Types.TINYINT, Byte.class);
	/**
	 * The sql type for a short value, mapped to a small integer.
	 */
	public static final SqlScalarType<Short> SHORT = new SqlScalarType<>(Types.SMALLINT, Short.class);
	/**
	 * The sql type for an integer value.
	 */
	public static final SqlScalarType<Integer> INTEGER = new SqlScalarType<>(Types.INTEGER, Integer.class);
	/**
	 * The sql type for a long value, mapped to a big integer.
	 */
	public static final SqlScalarType<Long> LONG = new SqlScalarType<>(Types.BIGINT, Long.class);
	/**
	 * The sql type for a single-precision floating point value.
	 */
	public static final SqlScalarType<Float> REAL = new SqlScalarType<>(Types.REAL, Float.class);
	/**
	 * The sql type for a double-precision floating point value, mapped to a float.
	 */
	public static final SqlScalarType<Double> FLOAT = new SqlScalarType<>(Types.FLOAT, Double.class);
	/**
	 * The sql type for a double-precision floating point value.
	 */
	public static final SqlScalarType<Double> DOUBLE = new SqlScalarType<>(Types.DOUBLE, Double.class);
	/**
	 * The sql type for an exact numeric value with configurable precision and scale.
	 */
	public static final ParameterizableSqlType<BigDecimal, SqlPrecisionParameter> NUMERIC = new DirectParameterizableSqlType<>(Types.NUMERIC, BigDecimal.class, SqlPrecisionParameter.class);
	/**
	 * The sql type for an exact decimal value with configurable precision and scale.
	 */
	public static final ParameterizableSqlType<BigDecimal, SqlPrecisionParameter> DECIMAL = new DirectParameterizableSqlType<>(Types.DECIMAL, BigDecimal.class, SqlPrecisionParameter.class);
	/**
	 * The sql type for an arbitrary precision integer, mapped onto {@link #NUMERIC}.
	 */
	public static final ParameterizableSqlType<BigInteger, SqlPrecisionParameter> BIG_INTEGER = NUMERIC.map(BigInteger.class, nullable(BigDecimal::new), BigDecimal::toBigInteger);
	
	/**
	 * The sql type for a fixed-length character string with configurable length.
	 */
	public static final ParameterizableSqlType<String, SqlLengthParameter> FIXED_STRING = new DirectParameterizableSqlType<>(Types.CHAR, String.class, SqlLengthParameter.class);
	/**
	 * The sql type for a single character, mapped onto a fixed-length string of length one.
	 */
	public static final SqlType<Character> CHARACTER = FIXED_STRING.configure(SqlParameter.length(1)).map(Character.class, nullable(String::valueOf), s -> {
		if (s.isEmpty()) {
			throw new SqlClientException("Unable to read CHARACTER from an empty string value");
		}
		return s.charAt(0);
	});
	/**
	 * The sql type for a variable-length character string with configurable length.
	 */
	public static final ParameterizableSqlType<String, SqlLengthParameter> STRING = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
	/**
	 * The sql type for a large variable-length character string.
	 */
	public static final SqlScalarType<String> TEXT = new SqlScalarType<>(Types.LONGVARCHAR, String.class);
	/**
	 * The sql type for a fixed-length unicode character string with configurable length.
	 */
	public static final ParameterizableSqlType<String, SqlLengthParameter> UNICODE_FIXED_STRING = new DirectParameterizableSqlType<>(Types.NCHAR, String.class, SqlLengthParameter.class);
	/**
	 * The sql type for a variable-length unicode character string with configurable length.
	 */
	public static final ParameterizableSqlType<String, SqlLengthParameter> UNICODE_STRING = new DirectParameterizableSqlType<>(Types.NVARCHAR, String.class, SqlLengthParameter.class);
	/**
	 * The sql type for a large variable-length unicode character string.
	 */
	public static final SqlScalarType<String> UNICODE_TEXT = new SqlScalarType<>(Types.LONGNVARCHAR, String.class);
	/**
	 * The sql type for a character large object.
	 */
	public static final SqlScalarType<Clob> CLOB = new SqlScalarType<>(Types.CLOB, Clob.class);
	/**
	 * The sql type for a unicode character large object.
	 */
	public static final SqlScalarType<NClob> NCLOB = new SqlScalarType<>(Types.NCLOB, NClob.class);
	
	/**
	 * The sql type for a fixed-length byte array with configurable length.
	 */
	public static final ParameterizableSqlType<byte[], SqlLengthParameter> FIXED_BYTES = new DirectParameterizableSqlType<>(Types.BINARY, byte[].class, SqlLengthParameter.class);
	/**
	 * The sql type for a variable-length byte array with configurable length.
	 */
	public static final ParameterizableSqlType<byte[], SqlLengthParameter> BYTES = new DirectParameterizableSqlType<>(Types.VARBINARY, byte[].class, SqlLengthParameter.class);
	/**
	 * The sql type for a large variable-length byte array.
	 */
	public static final SqlScalarType<byte[]> LARGE_BYTES = new SqlScalarType<>(Types.LONGVARBINARY, byte[].class);
	/**
	 * The sql type for a binary large object.
	 */
	public static final SqlScalarType<Blob> BLOB = new SqlScalarType<>(Types.BLOB, Blob.class);
	
	/**
	 * The sql type for a date without time, mapped to a {@link LocalDate}.
	 */
	public static final SqlScalarType<LocalDate> LOCAL_DATE = new SqlScalarType<>(Types.DATE, LocalDate.class);
	/**
	 * The sql type for a time without date or zone, mapped to a {@link LocalTime}.
	 */
	public static final ParameterizableSqlType<LocalTime, SqlFractionalParameter> LOCAL_TIME = new DirectParameterizableSqlType<>(Types.TIME, LocalTime.class, SqlFractionalParameter.class);
	/**
	 * The sql type for a timestamp without zone, mapped to a {@link LocalDateTime}.
	 */
	public static final ParameterizableSqlType<LocalDateTime, SqlFractionalParameter> LOCAL_DATE_TIME = new DirectParameterizableSqlType<>(Types.TIMESTAMP, LocalDateTime.class, SqlFractionalParameter.class);
	/**
	 * The sql type for a time with a zone offset, mapped to an {@link OffsetTime}.
	 */
	public static final ParameterizableSqlType<OffsetTime, SqlFractionalParameter> OFFSET_TIME = new DirectParameterizableSqlType<>(Types.TIME_WITH_TIMEZONE, OffsetTime.class, SqlFractionalParameter.class);
	/**
	 * The sql type for a timestamp with a zone offset, mapped to an {@link OffsetDateTime}.
	 */
	public static final ParameterizableSqlType<OffsetDateTime, SqlFractionalParameter> OFFSET_DATE_TIME = new DirectParameterizableSqlType<>(Types.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class, SqlFractionalParameter.class);
	/**
	 * The sql type for a zoned timestamp, mapped onto {@link #OFFSET_DATE_TIME}.
	 */
	public static final ParameterizableSqlType<ZonedDateTime, SqlFractionalParameter> ZONED_DATE_TIME = OFFSET_DATE_TIME.map(ZonedDateTime.class, nullable(ZonedDateTime::toOffsetDateTime), OffsetDateTime::toZonedDateTime);
	/**
	 * The sql type for an instant, mapped onto {@link #OFFSET_DATE_TIME} in the UTC zone.
	 */
	public static final ParameterizableSqlType<Instant, SqlFractionalParameter> INSTANT = OFFSET_DATE_TIME.map(Instant.class, nullable(instant -> instant.atOffset(ZoneOffset.UTC)), OffsetDateTime::toInstant);
	/**
	 * The sql type for a year, mapped onto {@link #INTEGER}.
	 */
	public static final SqlType<Year> YEAR = INTEGER.map(Year.class, nullable(Year::getValue), Year::of);
	/**
	 * The sql type for a month, mapped onto {@link #INTEGER} using the month number.
	 */
	public static final SqlType<Month> MONTH = INTEGER.map(Month.class, nullable(Month::getValue), Month::of);
	/**
	 * The sql type for a day of the week, mapped onto {@link #INTEGER} using the day number.
	 */
	public static final SqlType<DayOfWeek> DAY_OF_WEEK = INTEGER.map(DayOfWeek.class, nullable(DayOfWeek::getValue), DayOfWeek::of);
	/**
	 * The sql type for a duration, mapped onto {@link #LONG} as a nanosecond count.
	 */
	public static final SqlType<Duration> DURATION = LONG.map(Duration.class, nullable(Duration::toNanos), Duration::ofNanos);
	
	/**
	 * The sql type for a uuid, mapped onto a fixed-length string of length 36.
	 */
	public static final SqlType<UUID> UUID = FIXED_STRING.configure(SqlParameter.length(36)).map(UUID.class, nullable(java.util.UUID::toString), java.util.UUID::fromString);
	
	/**
	 * The sql type for a json element, stored as its textual representation.
	 */
	public static final SqlType<JsonElement> JSON = TEXT.map(JsonElement.class, nullable(element -> element.toString(JsonConfig.DEFAULT)), string -> new JsonReader(string).readJson());
	/**
	 * The sql type for an xml element, stored as its textual representation.
	 */
	public static final SqlType<XmlElement> XML = TEXT.map(XmlElement.class, nullable(element -> element.toString(XmlConfig.DEFAULT)), SqlTypes::readXml);
	
	/**
	 * The sql type for an ip address, stored as its string representation in a string of length 64.
	 */
	@SuppressWarnings("unchecked")
	public static final SqlType<IpAddress<?>> IP_ADDRESS = STRING.configure(SqlParameter.length(64)).map((Class<IpAddress<?>>) (Class<?>) IpAddress.class, nullable(IpAddress::toString), IpAddresses::parse);
	/**
	 * The sql type for an ip network, stored as its string representation in a string of length 64.
	 */
	@SuppressWarnings("unchecked")
	public static final SqlType<IpNetwork<?, ?>> IP_NETWORK = STRING.configure(SqlParameter.length(64)).map((Class<IpNetwork<?, ?>>) (Class<?>) IpNetwork.class, nullable(IpNetwork::toString), IpAddresses::parseNetwork);
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static holder class.<br>
	 */
	private SqlTypes() {}
	
	/**
	 * Wraps the given function into a throwable function that passes {@code null} values through unchanged.<br>
	 * The wrapped function only applies the given function to non-null values.<br>
	 *
	 * @param function The function to wrap
	 * @return A null-safe throwable function
	 * @param <I> The input type of the function
	 * @param <O> The output type of the function
	 * @param <X> The type of throwable the resulting function may throw
	 * @throws NullPointerException If the function is null
	 */
	private static <I, O, X extends Throwable> @NonNull ThrowableFunction<@Nullable I, @Nullable O, X> nullable(@NonNull Function<@NonNull I, @Nullable O> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> value == null ? null : function.apply(value);
	}
	
	/**
	 * Reads an xml element from the given xml string.<br>
	 * A default xml declaration is prepended if the string does not already start with one.<br>
	 *
	 * @param xml The xml string to read
	 * @return The read xml element
	 */
	private static @NonNull XmlElement readXml(@NonNull String xml) {
		String content = xml.stripLeading().startsWith("<?xml") ? xml : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
		
		try (XmlReader reader = new XmlReader(content)) {
			reader.readDeclaration();
			return reader.readXmlElement();
		}
	}
	
	/**
	 * Creates a sql type that stores the given enum class by its constant name.<br>
	 * The enum is mapped onto {@link #TEXT} using the constant name as the stored value.<br>
	 *
	 * @param clazz The enum class to create the type for
	 * @return A sql type that stores the enum by name
	 * @param <E> The enum type
	 * @throws NullPointerException If the enum class is null
	 * @throws IllegalArgumentException If the provided class is not an enum type
	 */
	public static <E extends Enum<E>> @NonNull SqlType<E> enumName(@NonNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		if (clazz.getEnumConstants() == null) {
			throw new IllegalArgumentException("Provided class is not an enum type");
		}
		return TEXT.map(clazz, nullable(Enum::name), name -> Enum.valueOf(clazz, name));
	}
	
	/**
	 * Creates a sql type that stores the given enum class by its constant ordinal.<br>
	 * The enum is mapped onto {@link #INTEGER} using the constant ordinal as the stored value.<br>
	 *
	 * @param clazz The enum class to create the type for
	 * @return A sql type that stores the enum by ordinal
	 * @param <E> The enum type
	 * @throws NullPointerException If the enum class is null
	 * @throws IllegalArgumentException If the provided class is not an enum type
	 */
	public static <E extends Enum<E>> @NonNull SqlType<E> enumOrdinal(@NonNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		if (clazz.getEnumConstants() == null) {
			throw new IllegalArgumentException("Provided class is not an enum type");
		}
		
		return INTEGER.map(clazz, nullable(Enum::ordinal), ordinal -> {
			E[] constants = clazz.getEnumConstants();
			if (ordinal < 0 || ordinal >= constants.length) {
				throw new SqlClientException("Invalid ordinal value for enum " + clazz.getName() + ": " + ordinal);
			}
			return constants[ordinal];
		});
	}
}
