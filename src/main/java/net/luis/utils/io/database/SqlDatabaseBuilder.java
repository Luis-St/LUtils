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

import net.luis.utils.io.database.audit.SqlTimestampSource;
import net.luis.utils.io.database.exception.SqlConnectionException;
import net.luis.utils.io.database.mapping.SqlNamingStrategy;
import net.luis.utils.io.database.mapping.SqlTypeConverter;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.time.Clock;

/**
 * Builder interface for configuring and creating a {@link SqlDatabase} instance.<br>
 * Obtained via {@link SqlDatabase#builder()}.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabaseBuilder {
	
	/**
	 * Sets the data source for the database connection.<br>
	 *
	 * @param dataSource The data source to use
	 * @return This builder
	 */
	@NonNull SqlDatabaseBuilder dataSource(@NonNull DataSource dataSource);
	
	/**
	 * Sets the timestamp source for audit operations.<br>
	 *
	 * @param source The audit timestamp source
	 * @return This builder
	 */
	@NonNull SqlDatabaseBuilder auditTimestampSource(@NonNull SqlTimestampSource source);
	
	/**
	 * Sets the clock used for audit timestamps.<br>
	 *
	 * @param clock The clock to use
	 * @return This builder
	 */
	@NonNull SqlDatabaseBuilder auditClock(@NonNull Clock clock);
	
	/**
	 * Registers a custom type converter for mapping between Java and database types.<br>
	 * <p>
	 *     Converters are used by the mapping layer when reading from and writing to
	 *     the database. If a converter is registered for a type, it takes precedence
	 *     over the default JDBC type handling.<br>
	 * </p>
	 *
	 * @param converter The type converter to register
	 * @param <J> The Java type
	 * @param <D> The database type
	 * @return This builder
	 */
	<J, D> @NonNull SqlDatabaseBuilder registerConverter(@NonNull SqlTypeConverter<J, D> converter);
	
	/**
	 * Sets the naming strategy used for mapping between Java record component names and SQL column names.<br>
	 *
	 * @param strategy The naming strategy to use
	 * @return This builder
	 */
	@NonNull SqlDatabaseBuilder namingStrategy(@NonNull SqlNamingStrategy strategy);
	
	/**
	 * Builds the configuration and creates a database instance.<br>
	 *
	 * @return The configured database instance
	 * @throws SqlConnectionException If the connection cannot be established
	 */
	@NonNull SqlDatabase build() throws SqlConnectionException;
}
