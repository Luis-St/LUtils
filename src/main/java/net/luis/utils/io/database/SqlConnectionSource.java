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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Functional interface that supplies {@link SqlConnectionHandle connection handles} on demand.<br>
 * A source abstracts over how connections are obtained, for example from a pooled {@link DataSource} or from a single fixed {@link Connection}.<br>
 *
 * @see SqlConnectionHandle
 *
 * @author Luis-St
 */
@FunctionalInterface
@SuppressWarnings("JDBCResourceOpenedButNotSafelyClosed")
public interface SqlConnectionSource {
	
	/**
	 * Creates a connection source that borrows a fresh connection from the given data source for every handle.<br>
	 * Closing a handle returned by the source releases the connection back to the data source.<br>
	 *
	 * @param dataSource The data source to obtain connections from
	 * @return A connection source backed by the given data source
	 * @throws NullPointerException If the data source is null
	 */
	static @NonNull SqlConnectionSource pooled(@NonNull DataSource dataSource) {
		Objects.requireNonNull(dataSource, "Data source must not be null");
		
		return () -> {
			Connection connection;
			try {
				connection = dataSource.getConnection();
			} catch (SQLException e) {
				throw new SqlConnectionException("Failed to obtain connection from data source", e);
			}
			
			return new SqlConnectionHandle() {
				
				@Override
				public @NonNull Connection connection() {
					return connection;
				}
				
				@Override
				public void close() throws SqlException {
					try {
						connection.close();
					} catch (SQLException e) {
						throw new SqlConnectionException("Failed to release connection to data source", e);
					}
				}
			};
		};
	}
	
	/**
	 * Creates a connection source that always hands out the given fixed connection.<br>
	 * The connection is shared across all handles and closing a handle does not close the underlying connection.<br>
	 *
	 * @param connection The connection to be shared by all handles
	 * @return A connection source backed by the given fixed connection
	 * @throws NullPointerException If the connection is null
	 */
	static @NonNull SqlConnectionSource fixed(@NonNull Connection connection) {
		Objects.requireNonNull(connection, "Connection must not be null");
		
		return () -> new SqlConnectionHandle() {
			
			@Override
			public @NonNull Connection connection() {
				return connection;
			}
			
			@Override
			public void close() {}
		};
	}
	
	/**
	 * Opens a new connection handle from this source.<br>
	 *
	 * @return A handle wrapping the obtained connection
	 * @throws SqlException If a connection could not be obtained
	 */
	@NonNull SqlConnectionHandle open() throws SqlException;
}
