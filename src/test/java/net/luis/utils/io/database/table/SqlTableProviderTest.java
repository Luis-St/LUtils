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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.SqlTestFixtures.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import net.luis.utils.io.database.index.SqlIndexMethod;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTableProvider}.<br>
 * <p>
 *     Only the constructor and the pre-database validation guards of {@code createIndex} / {@code dropIndex}
 *     are unit-testable; everything that opens a connection is integration-level. The shared {@link #SOURCE}
 *     throws when a connection is actually requested, which lets the "validation passes" tests prove they
 *     reached the database step.
 * </p>
 *
 * @author Luis-St
 */
class SqlTableProviderTest {
	
	private static final SqlConnectionSource SOURCE = () -> {
		throw new SqlConnectionException("No database available in unit test", new SQLException());
	};
	
	private static SqlTableProvider<Object> provider(SqlTable<Object> table) {
		return new SqlTableProvider<>(table, DIALECT, SOURCE, Duration.ofSeconds(5));
	}
	
	/**
	 * Builds a provider on the sample table whose connections come from the given recording source, so the executed
	 * statements can be observed.
	 *
	 * @param dataSource The recording source handing out the connections
	 * @param dialect The dialect the provider renders with
	 * @return The built provider
	 */
	private static SqlTableProvider<Object> recordingProvider(RecordingDataSource dataSource, SqlDialect dialect) {
		return new SqlTableProvider<>(sampleTable(), dialect, SqlConnectionSource.pooled(dataSource), Duration.ofSeconds(5));
	}
	
	@Test
	void constructWithValidArguments() {
		assertNotNull(provider(sampleTable()));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlTableProvider<>(null, DIALECT, SOURCE, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTableProvider<>(sampleTable(), null, SOURCE, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlTableProvider<>(sampleTable(), DIALECT, null, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlTableProvider<>(sampleTable(), DIALECT, SOURCE, null));
	}
	
	@Test
	void createIndexWithNullName() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(NullPointerException.class, () -> provider(table).createIndex(null, List.of(column), SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexWithNullColumns() {
		assertThrows(NullPointerException.class, () -> provider(sampleTable()).createIndex("idx", null, SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexWithNullMethod() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(NullPointerException.class, () -> provider(table).createIndex("idx", List.of(column), null));
	}
	
	@Test
	void createIndexWithEmptyColumns() {
		assertThrows(IllegalArgumentException.class, () -> provider(sampleTable()).createIndex("idx", List.of(), SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexWithColumnFromOtherTable() {
		SqlColumn<Object, ?> foreign = integerColumn();
		assertThrows(IllegalArgumentException.class, () -> provider(sampleTable()).createIndex("idx", List.of(foreign), SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexWithUnsupportedMethod() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(SqlDialectException.class, () -> provider(table).createIndex("idx", List.of(column), SqlIndexMethod.HASH));
	}
	
	@Test
	void dropIndexWithNullName() {
		assertThrows(NullPointerException.class, () -> provider(sampleTable()).dropIndex(null));
	}
	
	@Test
	void createIndexUniqueOverloadDelegates() {
		assertThrows(IllegalArgumentException.class, () -> provider(sampleTable()).createIndex("idx", List.of(), true, SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexColumnFromTablePassesValidation() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(SqlConnectionException.class, () -> provider(table).createIndex("idx", List.of(column), SqlIndexMethod.BTREE));
	}
	
	@Test
	void createIndexSupportedMethodPassesValidation() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(SqlConnectionException.class, () -> provider(table).createIndex("idx", List.of(column), false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void truncateWithCascadeOnUnsupportingDialect() {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		SqlTableProvider<Object> provider = recordingProvider(dataSource, DIALECT);
		
		SqlDialectFeatureException thrown = assertThrows(SqlDialectFeatureException.class, () -> provider.truncate(true));
		assertTrue(thrown.getMessage().contains("TRUNCATE_CASCADE"), thrown.getMessage());
		assertTrue(thrown.getMessage().contains(DIALECT.name()), thrown.getMessage());
		assertTrue(dataSource.executedSql().isEmpty());
	}
	
	@Test
	void truncateWithCascadeOnSupportingDialect() throws SqlException {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		recordingProvider(dataSource, SqlDialects.POSTGRESQL).truncate(true);
		
		assertEquals(1, dataSource.executedSql().size());
		assertTrue(dataSource.executedSql().getFirst().endsWith("CASCADE"), dataSource.executedSql().getFirst());
	}
	
	@Test
	void truncateWithoutCascadeOnUnsupportingDialect() throws SqlException {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		assertDoesNotThrow(() -> recordingProvider(dataSource, DIALECT).truncate(false));
		
		assertEquals(List.of("TRUNCATE TABLE \"test_table\""), dataSource.executedSql());
	}
	
	@Test
	void truncateWithoutCascadeOnSupportingDialect() throws SqlException {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		recordingProvider(dataSource, SqlDialects.POSTGRESQL).truncate(false);
		
		assertEquals(List.of("TRUNCATE TABLE \"test_table\""), dataSource.executedSql());
		assertFalse(dataSource.executedSql().getFirst().contains("CASCADE"));
	}
	
	@Test
	void truncateDelegatesToNonCascadingOverload() throws SqlException {
		RecordingDataSource noArg = SqlTestFixtures.recordingDataSource();
		RecordingDataSource explicit = SqlTestFixtures.recordingDataSource();
		recordingProvider(noArg, DIALECT).truncate();
		recordingProvider(explicit, DIALECT).truncate(false);
		
		assertEquals(explicit.executedSql(), noArg.executedSql());
	}
	
	@Test
	void truncateExecutesExactlyOneStatement() throws SqlException {
		RecordingDataSource plain = SqlTestFixtures.recordingDataSource();
		RecordingDataSource cascading = SqlTestFixtures.recordingDataSource();
		recordingProvider(plain, SqlDialects.POSTGRESQL).truncate(false);
		recordingProvider(cascading, SqlDialects.POSTGRESQL).truncate(true);
		
		assertEquals(1, plain.executedSql().size());
		assertEquals(1, cascading.executedSql().size());
	}
	
	@Test
	void truncateQuotesTableIdentifier() throws SqlException {
		RecordingDataSource plain = SqlTestFixtures.recordingDataSource();
		RecordingDataSource cascading = SqlTestFixtures.recordingDataSource();
		recordingProvider(plain, SqlDialects.POSTGRESQL).truncate(false);
		recordingProvider(cascading, SqlDialects.POSTGRESQL).truncate(true);
		
		String quoted = SqlDialects.POSTGRESQL.quoteIdentifier(sampleTable().name());
		assertTrue(plain.executedSql().getFirst().contains(quoted), plain.executedSql().getFirst());
		assertTrue(cascading.executedSql().getFirst().contains(quoted), cascading.executedSql().getFirst());
	}
	
	@Test
	void truncateWithCascadeAfterInsertsClearsThroughSameConnectionSource() throws SqlException {
		RecordingDataSource dataSource = SqlTestFixtures.recordingDataSource();
		SqlTableProvider<Object> provider = recordingProvider(dataSource, SqlDialects.POSTGRESQL);
		
		provider.truncate(false);
		provider.truncate(true);
		assertThrows(SqlDialectFeatureException.class, () -> recordingProvider(dataSource, DIALECT).truncate(true));
		provider.truncate(false);
		
		assertEquals(3, dataSource.executedSql().size());
		assertTrue(dataSource.executedSql().get(1).endsWith("CASCADE"), dataSource.executedSql().get(1));
		assertFalse(dataSource.executedSql().getLast().contains("CASCADE"), dataSource.executedSql().getLast());
	}
}
