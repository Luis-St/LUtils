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

package net.luis.utils.io.databasev1.connection;

import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * A pooled {@link DataSource} implementation for connection pooling.<br>
 * Implements {@link AutoCloseable} to allow proper shutdown of the connection pool and release of all pooled connections.<br>
 *
 * @author Luis-St
 */
public class SqlPooledDataSource implements DataSource, AutoCloseable {
	
	/**
	 * Constructs a new pooled data source with the given JDBC URL and pool bounds.<br>
	 *
	 * @param jdbcUrl The JDBC connection URL
	 * @param minConnections The minimum number of connections in the pool
	 * @param maxConnections The maximum number of connections in the pool
	 */
	public SqlPooledDataSource(@NonNull String jdbcUrl, int minConnections, int maxConnections) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs a new pooled data source with the given JDBC URL, credentials, and pool bounds.<br>
	 *
	 * @param jdbcUrl The JDBC connection URL
	 * @param username The database username
	 * @param password The database password
	 * @param minConnections The minimum number of connections in the pool
	 * @param maxConnections The maximum number of connections in the pool
	 */
	public SqlPooledDataSource(@NonNull String jdbcUrl, @NonNull String username, @NonNull String password, int minConnections, int maxConnections) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Closes this pooled data source and releases all pooled connections.<br>
	 * After calling this method, no new connections can be obtained from this data source.<br>
	 */
	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
