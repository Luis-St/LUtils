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
 * A single-connection {@link DataSource} implementation for simple database access.<br>
 *
 * @author Luis-St
 */
public class SqlSingleConnectionDataSource implements DataSource {
	
	/**
	 * Constructs a new single-connection data source with the given JDBC URL.<br>
	 * @param jdbcUrl The JDBC connection URL
	 */
	public SqlSingleConnectionDataSource(@NonNull String jdbcUrl) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs a new single-connection data source with the given JDBC URL and credentials.<br>
	 *
	 * @param jdbcUrl The JDBC connection URL
	 * @param username The database username
	 * @param password The database password
	 */
	public SqlSingleConnectionDataSource(@NonNull String jdbcUrl, @NonNull String username, @NonNull String password) {
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
}
