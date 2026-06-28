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
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlConnectionSource}.<br>
 *
 * @author Luis-St
 */
class SqlConnectionSourceTest {
	
	private static @NonNull Connection recordingConnection(@NonNull AtomicInteger closeCount) {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			(proxy, method, args) -> {
				if ("close".equals(method.getName())) {
					closeCount.incrementAndGet();
					return null;
				}
				if ("toString".equals(method.getName())) {
					return "RecordingConnection";
				}
				throw new UnsupportedOperationException("Recording connection method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	private static @NonNull Connection throwingCloseConnection() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			(proxy, method, args) -> {
				if ("close".equals(method.getName())) {
					throw new SQLException("Connection close failed");
				}
				if ("toString".equals(method.getName())) {
					return "ThrowingCloseConnection";
				}
				throw new UnsupportedOperationException("Throwing connection method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	private static @NonNull DataSource dataSourceReturning(@NonNull Connection connection, @NonNull AtomicInteger getCount) {
		return (DataSource) Proxy.newProxyInstance(
			DataSource.class.getClassLoader(),
			new Class<?>[] { DataSource.class },
			(proxy, method, args) -> {
				if ("getConnection".equals(method.getName()) && (args == null || args.length == 0)) {
					getCount.incrementAndGet();
					return connection;
				}
				if ("toString".equals(method.getName())) {
					return "ControllableDataSource";
				}
				throw new UnsupportedOperationException("Controllable data source method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	@Test
	void pooledWithNullDataSourceThrows() throws SqlException {
		assertThrows(NullPointerException.class, () -> SqlConnectionSource.pooled(null));
	}
	
	@Test
	void fixedWithNullConnectionThrows() throws SqlException {
		assertThrows(NullPointerException.class, () -> SqlConnectionSource.fixed(null));
	}
	
	@Test
	void pooledOpenWrapsConnectionFailure() throws SqlException {
		SqlConnectionSource source = SqlConnectionSource.pooled(SqlTestFixtures.failingDataSource());
		SqlConnectionException exception = assertThrows(SqlConnectionException.class, source::open);
		assertInstanceOf(SQLException.class, exception.getCause());
	}
	
	@Test
	void pooledCloseWrapsReleaseFailure() throws SqlException {
		SqlConnectionSource source = SqlConnectionSource.pooled(dataSourceReturning(throwingCloseConnection(), new AtomicInteger()));
		SqlConnectionHandle handle = source.open();
		SqlConnectionException exception = assertThrows(SqlConnectionException.class, handle::close);
		assertInstanceOf(SQLException.class, exception.getCause());
	}
	
	@Test
	void pooledOpenReturnsHandleWithConnection() throws SqlException {
		Connection connection = recordingConnection(new AtomicInteger());
		SqlConnectionSource source = SqlConnectionSource.pooled(dataSourceReturning(connection, new AtomicInteger()));
		SqlConnectionHandle handle = source.open();
		assertNotNull(handle);
		assertSame(connection, handle.connection());
	}
	
	@Test
	void pooledCloseReleasesConnectionSuccessfully() throws SqlException {
		AtomicInteger closeCount = new AtomicInteger();
		SqlConnectionSource source = SqlConnectionSource.pooled(dataSourceReturning(recordingConnection(closeCount), new AtomicInteger()));
		SqlConnectionHandle handle = source.open();
		assertDoesNotThrow(handle::close);
		assertEquals(1, closeCount.get());
	}
	
	@Test
	void fixedOpenReturnsHandleWithSameConnection() throws SqlException {
		Connection connection = SqlTestFixtures.placeholderConnection();
		SqlConnectionHandle handle = SqlConnectionSource.fixed(connection).open();
		assertSame(connection, handle.connection());
	}
	
	@Test
	void fixedCloseIsNoOp() throws SqlException {
		SqlConnectionHandle handle = SqlConnectionSource.fixed(SqlTestFixtures.placeholderConnection()).open();
		assertDoesNotThrow(handle::close);
	}
	
	@Test
	void pooledOpenedTwiceObtainsConnectionEachTime() throws SqlException {
		AtomicInteger getCount = new AtomicInteger();
		SqlConnectionSource source = SqlConnectionSource.pooled(dataSourceReturning(recordingConnection(new AtomicInteger()), getCount));
		source.open();
		source.open();
		assertEquals(2, getCount.get());
	}
	
	@Test
	void fixedReusedAcrossMultipleOpens() throws SqlException {
		Connection connection = SqlTestFixtures.placeholderConnection();
		SqlConnectionSource source = SqlConnectionSource.fixed(connection);
		SqlConnectionHandle first = source.open();
		SqlConnectionHandle second = source.open();
		assertSame(connection, first.connection());
		assertSame(connection, second.connection());
		assertDoesNotThrow(first::close);
		assertDoesNotThrow(second::close);
	}
}
