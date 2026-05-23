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
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
@SuppressWarnings("JDBCResourceOpenedButNotSafelyClosed")
public interface SqlConnectionSource {
	
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
	
	@NonNull SqlConnectionHandle open() throws SqlException;
}
