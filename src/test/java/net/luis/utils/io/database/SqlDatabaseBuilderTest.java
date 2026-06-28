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

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import net.luis.utils.io.database.transaction.SqlIsolationLevel;
import net.luis.utils.io.database.transaction.SqlPropagation;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDatabaseBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlDatabaseBuilderTest {
	
	private static @NonNull SqlDatabaseBuilder newBuilder() {
		return new SqlDatabaseBuilder(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT);
	}
	
	@Test
	void constructWithValidArguments() throws SqlConnectionException {
		SqlDatabaseBuilder builder = new SqlDatabaseBuilder(SqlTestFixtures.failingDataSource(), SqlTestFixtures.DIALECT);
		assertNotNull(builder);
		assertNotNull(builder.build());
	}
	
	@Test
	void constructWithNullDataSourceThrows() {
		assertThrows(NullPointerException.class, () -> new SqlDatabaseBuilder(null, SqlTestFixtures.DIALECT));
	}
	
	@Test
	void constructWithNullDialectThrows() {
		assertThrows(NullPointerException.class, () -> new SqlDatabaseBuilder(SqlTestFixtures.failingDataSource(), null));
	}
	
	@Test
	void queryTimeoutWithNullThrows() {
		SqlDatabaseBuilder builder = newBuilder();
		assertThrows(NullPointerException.class, () -> builder.queryTimeout(null));
	}
	
	@Test
	void connectionAcquisitionTimeoutWithNullThrows() {
		SqlDatabaseBuilder builder = newBuilder();
		assertThrows(NullPointerException.class, () -> builder.connectionAcquisitionTimeout(null));
	}
	
	@Test
	void defaultTransactionIsolationLevelWithNullThrows() {
		SqlDatabaseBuilder builder = newBuilder();
		assertThrows(NullPointerException.class, () -> builder.defaultTransactionIsolationLevel(null));
	}
	
	@Test
	void defaultTransactionPropagationWithNullThrows() {
		SqlDatabaseBuilder builder = newBuilder();
		assertThrows(NullPointerException.class, () -> builder.defaultTransactionPropagation(null));
	}
	
	@Test
	void auditUserProviderWithNullThrows() {
		SqlDatabaseBuilder builder = newBuilder();
		assertThrows(NullPointerException.class, () -> builder.auditUserProvider(null));
	}
	
	@Test
	void queryTimeoutReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.queryTimeout(Duration.ofSeconds(5)));
	}
	
	@Test
	void connectionAcquisitionTimeoutReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.connectionAcquisitionTimeout(Duration.ofSeconds(5)));
	}
	
	@Test
	void defaultTransactionIsolationLevelReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.defaultTransactionIsolationLevel(SqlIsolationLevel.SERIALIZABLE));
	}
	
	@Test
	void defaultTransactionPropagationReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.defaultTransactionPropagation(SqlPropagation.REQUIRES_NEW));
	}
	
	@Test
	void autoCloseDataSourceReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.autoCloseDataSource(true));
		assertSame(builder, builder.autoCloseDataSource(false));
	}
	
	@Test
	void auditUserProviderReturnsSameBuilder() {
		SqlDatabaseBuilder builder = newBuilder();
		assertSame(builder, builder.auditUserProvider(SqlAuditUserProvider.of("admin")));
	}
	
	@Test
	void buildWithDefaults() throws SqlConnectionException {
		DataSource dataSource = SqlTestFixtures.failingDataSource();
		SqlDatabase database = new SqlDatabaseBuilder(dataSource, SqlTestFixtures.DIALECT).build();
		assertNotNull(database);
		assertSame(dataSource, database.getDataSource());
		assertSame(SqlTestFixtures.DIALECT, database.getDialect());
		assertTrue(database.getAuditUserProvider().get().isEmpty());
	}
	
	@Test
	void buildWithCustomAuditUserProvider() throws SqlConnectionException {
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("admin");
		SqlDatabase database = newBuilder().auditUserProvider(provider).build();
		assertSame(provider, database.getAuditUserProvider());
	}
	
	@Test
	void methodChainingConsistency() throws SqlConnectionException {
		DataSource dataSource = SqlTestFixtures.failingDataSource();
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("admin");
		SqlDatabaseBuilder builder = new SqlDatabaseBuilder(dataSource, SqlTestFixtures.DIALECT);
		SqlDatabaseBuilder chained = builder
			.queryTimeout(Duration.ofSeconds(5))
			.connectionAcquisitionTimeout(Duration.ofSeconds(5))
			.defaultTransactionIsolationLevel(SqlIsolationLevel.SERIALIZABLE)
			.defaultTransactionPropagation(SqlPropagation.REQUIRES_NEW)
			.autoCloseDataSource(true)
			.auditUserProvider(provider);
		assertSame(builder, chained);
		
		SqlDatabase database = chained.build();
		assertNotNull(database);
		assertSame(dataSource, database.getDataSource());
		assertSame(SqlTestFixtures.DIALECT, database.getDialect());
		assertSame(provider, database.getAuditUserProvider());
	}
	
	@Test
	void builderReuseProducesIndependentDatabases() throws SqlConnectionException {
		SqlDatabaseBuilder builder = newBuilder();
		SqlDatabase first = builder.build();
		assertTrue(first.getAuditUserProvider().get().isEmpty());
		
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("admin");
		builder.auditUserProvider(provider);
		SqlDatabase second = builder.build();
		assertSame(provider, second.getAuditUserProvider());
		assertTrue(first.getAuditUserProvider().get().isEmpty());
	}
}
