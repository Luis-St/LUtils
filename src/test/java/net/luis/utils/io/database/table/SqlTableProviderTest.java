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
import net.luis.utils.io.database.exception.client.dialect.SqlDialectException;
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
}
