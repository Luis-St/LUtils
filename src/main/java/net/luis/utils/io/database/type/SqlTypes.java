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
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.type.parameter.*;

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
 *
 * @author Luis-St
 *
 */

public final class SqlTypes {
	
	public static final SqlScalarType<Boolean> BOOLEAN = new SqlScalarType<>(Types.BOOLEAN, Boolean.class);
	
	public static final SqlScalarType<Byte> BYTE = new SqlScalarType<>(Types.TINYINT, Byte.class);
	public static final SqlScalarType<Short> SHORT = new SqlScalarType<>(Types.SMALLINT, Short.class);
	public static final SqlScalarType<Integer> INTEGER = new SqlScalarType<>(Types.INTEGER, Integer.class);
	public static final SqlScalarType<Long> LONG = new SqlScalarType<>(Types.BIGINT, Long.class);
	public static final SqlScalarType<Float> REAL = new SqlScalarType<>(Types.REAL, Float.class);
	public static final SqlScalarType<Double> FLOAT = new SqlScalarType<>(Types.FLOAT, Double.class);
	public static final SqlScalarType<Double> DOUBLE = new SqlScalarType<>(Types.DOUBLE, Double.class);
	public static final ParameterizableSqlType<BigDecimal, SqlPrecisionParameter> NUMERIC = new DirectParameterizableSqlType<>(Types.NUMERIC, BigDecimal.class, SqlPrecisionParameter.class);
	public static final ParameterizableSqlType<BigDecimal, SqlPrecisionParameter> DECIMAL = new DirectParameterizableSqlType<>(Types.DECIMAL, BigDecimal.class, SqlPrecisionParameter.class);
	public static final ParameterizableSqlType<BigInteger, SqlPrecisionParameter> BIG_INTEGER = NUMERIC.map(BigInteger.class, nullable(BigDecimal::new), BigDecimal::toBigInteger);
	
	public static final ParameterizableSqlType<String, SqlLengthParameter> FIXED_STRING = new DirectParameterizableSqlType<>(Types.CHAR, String.class, SqlLengthParameter.class);
	public static final SqlType<Character> CHARACTER = FIXED_STRING.configure(SqlParameter.length(1)).map(Character.class, nullable(String::valueOf), s -> s.charAt(0));
	public static final ParameterizableSqlType<String, SqlLengthParameter> STRING = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
	public static final SqlScalarType<String> TEXT = new SqlScalarType<>(Types.LONGVARCHAR, String.class);
	public static final ParameterizableSqlType<String, SqlLengthParameter> UNICODE_FIXED_STRING = new DirectParameterizableSqlType<>(Types.NCHAR, String.class, SqlLengthParameter.class);
	public static final ParameterizableSqlType<String, SqlLengthParameter> UNICODE_STRING = new DirectParameterizableSqlType<>(Types.NVARCHAR, String.class, SqlLengthParameter.class);
	public static final SqlScalarType<String> UNICODE_TEXT = new SqlScalarType<>(Types.LONGNVARCHAR, String.class);
	public static final SqlScalarType<Clob> CLOB = new SqlScalarType<>(Types.CLOB, Clob.class);
	public static final SqlScalarType<NClob> NCLOB = new SqlScalarType<>(Types.NCLOB, NClob.class);
	
	public static final ParameterizableSqlType<byte[], SqlLengthParameter> FIXED_BYTES = new DirectParameterizableSqlType<>(Types.BINARY, byte[].class, SqlLengthParameter.class);
	public static final ParameterizableSqlType<byte[], SqlLengthParameter> BYTES = new DirectParameterizableSqlType<>(Types.VARBINARY, byte[].class, SqlLengthParameter.class);
	public static final SqlScalarType<byte[]> LARGE_BYTES = new SqlScalarType<>(Types.LONGVARBINARY, byte[].class);
	public static final SqlScalarType<Blob> BLOB = new SqlScalarType<>(Types.BLOB, Blob.class);
	
	public static final SqlScalarType<LocalDate> LOCAL_DATE = new SqlScalarType<>(Types.DATE, LocalDate.class);
	public static final ParameterizableSqlType<LocalTime, SqlFractionalParameter> LOCAL_TIME = new DirectParameterizableSqlType<>(Types.TIME, LocalTime.class, SqlFractionalParameter.class);
	public static final ParameterizableSqlType<LocalDateTime, SqlFractionalParameter> LOCAL_DATE_TIME = new DirectParameterizableSqlType<>(Types.TIMESTAMP, LocalDateTime.class, SqlFractionalParameter.class);
	public static final ParameterizableSqlType<OffsetTime, SqlFractionalParameter> OFFSET_TIME = new DirectParameterizableSqlType<>(Types.TIME_WITH_TIMEZONE, OffsetTime.class, SqlFractionalParameter.class);
	public static final ParameterizableSqlType<OffsetDateTime, SqlFractionalParameter> OFFSET_DATE_TIME = new DirectParameterizableSqlType<>(Types.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class, SqlFractionalParameter.class);
	public static final ParameterizableSqlType<ZonedDateTime, SqlFractionalParameter> ZONED_DATE_TIME = OFFSET_DATE_TIME.map(ZonedDateTime.class, nullable(ZonedDateTime::toOffsetDateTime), OffsetDateTime::toZonedDateTime);
	public static final ParameterizableSqlType<Instant, SqlFractionalParameter> INSTANT = OFFSET_DATE_TIME.map(Instant.class, nullable(instant -> instant.atOffset(ZoneOffset.UTC)), OffsetDateTime::toInstant);
	public static final SqlType<Year> YEAR = INTEGER.map(Year.class, nullable(Year::getValue), Year::of);
	public static final SqlType<Month> MONTH = INTEGER.map(Month.class, nullable(Month::getValue), Month::of);
	public static final SqlType<DayOfWeek> DAY_OF_WEEK = INTEGER.map(DayOfWeek.class, nullable(DayOfWeek::getValue), DayOfWeek::of);
	public static final SqlType<Duration> DURATION = LONG.map(Duration.class, nullable(Duration::toNanos), Duration::ofNanos);
	
	public static final SqlType<UUID> UUID = FIXED_STRING.configure(SqlParameter.length(36)).map(UUID.class, nullable(java.util.UUID::toString), java.util.UUID::fromString);
	
	private SqlTypes() {}
	
	private static <I, O, X extends Throwable> @NonNull ThrowableFunction<@Nullable I, @Nullable O, X> nullable(@NonNull Function<@NonNull I, @Nullable O> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> value == null ? null : function.apply(value);
	}
	
	public static <E extends Enum<E>> @NonNull SqlType<E> enumName(@NonNull Class<E> clazz) {
		Objects.requireNonNull(clazz, "Enum class must not be null");
		if (clazz.getEnumConstants() == null) {
			throw new IllegalArgumentException("Provided class is not an enum type");
		}
		return TEXT.map(clazz, nullable(Enum::name), name -> Enum.valueOf(clazz, name));
	}
	
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
