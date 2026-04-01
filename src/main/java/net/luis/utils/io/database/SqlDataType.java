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

package net.luis.utils.io.database;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.*;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Luis-St
 *
 */

public record SqlDataType<T>(
	@NonNull String name,
	@NonNull Class<T> javaType,
	int columnType
) {

	public static final SqlDataType<String> STRING = new SqlDataType<>("VARCHAR", String.class, Types.VARCHAR);
	public static final SqlDataType<Boolean> BOOLEAN = new SqlDataType<>("BOOLEAN", Boolean.class, Types.BOOLEAN);
	public static final SqlDataType<Short> SHORT = new SqlDataType<>("SMALLINT", Short.class, Types.SMALLINT);
	public static final SqlDataType<Integer> INTEGER = new SqlDataType<>("INTEGER", Integer.class, Types.INTEGER);
	public static final SqlDataType<Long> LONG = new SqlDataType<>("BIGINT", Long.class, Types.BIGINT);
	public static final SqlDataType<Float> FLOAT = new SqlDataType<>("REAL", Float.class, Types.REAL);
	public static final SqlDataType<Double> DOUBLE = new SqlDataType<>("DOUBLE", Double.class, Types.DOUBLE);
	public static final SqlDataType<BigDecimal> BIG_DECIMAL = new SqlDataType<>("DECIMAL", BigDecimal.class, Types.DECIMAL);
	public static final SqlDataType<UUID> UUID = new SqlDataType<>("UUID", UUID.class, Types.OTHER);
	public static final SqlDataType<LocalDate> LOCAL_DATE = new SqlDataType<>("DATE", LocalDate.class, Types.DATE);
	public static final SqlDataType<LocalTime> LOCAL_TIME = new SqlDataType<>("TIME", LocalTime.class, Types.TIME);
	public static final SqlDataType<Instant> INSTANT = new SqlDataType<>("TIMESTAMP_UTC", Instant.class, Types.TIMESTAMP_WITH_TIMEZONE);
	public static final SqlDataType<LocalDateTime> LOCAL_DATE_TIME = new SqlDataType<>("TIMESTAMP", LocalDateTime.class, Types.TIMESTAMP);
	public static final SqlDataType<OffsetDateTime> OFFSET_DATE_TIME = new SqlDataType<>("TIMESTAMP_TZ", OffsetDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE);
	public static final SqlDataType<byte[]> BYTE_ARRAY = new SqlDataType<>("BLOB", byte[].class, Types.BLOB);
	public static final SqlDataType<Character> CHARACTER = new SqlDataType<>("CHAR", Character.class, Types.CHAR);

	public SqlDataType {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(javaType, "Java type must not be null");

		if (name.isBlank()) {
			throw new IllegalArgumentException("Name must not be blank");
		}
	}
}
