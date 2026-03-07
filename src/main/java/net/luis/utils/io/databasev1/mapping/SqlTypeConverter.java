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

package net.luis.utils.io.databasev1.mapping;

import net.luis.utils.io.databasev1.SqlDatabaseBuilder;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.UUID;

/**
 * Interface for converting between a Java type and a database (JDBC) type.<br>
 * <p>
 *     Implementations define how custom Java types are stored in and retrieved from<br>
 *     Register converters via {@link SqlDatabaseBuilder} to make them available to the mapping layer.
 * </p>
 * Built-in converters are provided for common types such as {@link UUID}, {@link Instant}, {@link LocalDate}, {@link LocalDateTime}, and {@link Enum} types.<br>
 *
 *  @author Luis-St
 *
 * @param <J> The Java type
 * @param <D> The database type (as read from / written to JDBC, e.g., {@link String}, {@link java.sql.Timestamp})
 */
public interface SqlTypeConverter<J, D> {
	
	/**
	 * Converts a Java value to its database representation.<br>
	 *
	 * @param value The Java value to convert
	 * @return The database representation
	 */
	@NonNull D toDatabase(@NonNull J value);
	
	/**
	 * Converts a database value to its Java representation.<br>
	 *
	 * @param value The database value to convert
	 * @return The Java representation
	 */
	@NonNull J fromDatabase(@NonNull D value);
	
	/**
	 * Returns the Java type this converter handles.<br>
	 * @return The Java class
	 */
	@NonNull Class<J> javaType();
	
	/**
	 * Returns the database type this converter produces / consumes.<br>
	 * @return The database (JDBC) class
	 */
	@NonNull Class<D> databaseType();
}

