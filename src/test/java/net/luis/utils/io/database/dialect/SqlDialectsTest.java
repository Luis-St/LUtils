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

package net.luis.utils.io.database.dialect;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDialects}.<br>
 *
 * @author Luis-St
 */
class SqlDialectsTest {
	
	@Test
	void defaultDialectConstant() {
		assertNotNull(SqlDialects.DEFAULT);
		assertInstanceOf(SqlDefaultDialect.class, SqlDialects.DEFAULT);
		assertEquals("Default", SqlDialects.DEFAULT.name());
	}
	
	@Test
	void h2DialectConstant() {
		assertNotNull(SqlDialects.H2);
		assertInstanceOf(H2Dialect.class, SqlDialects.H2);
		assertEquals("H2", SqlDialects.H2.name());
	}
	
	@Test
	void mariaDbDialectConstant() {
		assertNotNull(SqlDialects.MARIA_DB);
		assertInstanceOf(MariaDbDialect.class, SqlDialects.MARIA_DB);
		assertEquals("MariaDB", SqlDialects.MARIA_DB.name());
	}
	
	@Test
	void mySqlDialectConstant() {
		assertNotNull(SqlDialects.MYSQL);
		assertInstanceOf(MySqlDialect.class, SqlDialects.MYSQL);
		assertEquals("MySQL", SqlDialects.MYSQL.name());
	}
	
	@Test
	void postgreSqlDialectConstant() {
		assertNotNull(SqlDialects.POSTGRESQL);
		assertInstanceOf(PostgresSqlDialect.class, SqlDialects.POSTGRESQL);
		assertEquals("PostgreSQL", SqlDialects.POSTGRESQL.name());
	}
	
	@Test
	void sqliteDialectConstant() {
		assertNotNull(SqlDialects.SQLITE);
		assertInstanceOf(SqliteDialect.class, SqlDialects.SQLITE);
		assertEquals("SQLite", SqlDialects.SQLITE.name());
	}
	
	@Test
	void sqlServerDialectConstant() {
		assertNotNull(SqlDialects.SQL_SERVER);
		assertInstanceOf(SqlServerDialect.class, SqlDialects.SQL_SERVER);
		assertEquals("SQL Server", SqlDialects.SQL_SERVER.name());
	}
	
	@Test
	void allConstantsAreDistinctInstances() {
		Set<SqlDialect> identitySet = Collections.newSetFromMap(new IdentityHashMap<>());
		identitySet.add(SqlDialects.DEFAULT);
		identitySet.add(SqlDialects.H2);
		identitySet.add(SqlDialects.MARIA_DB);
		identitySet.add(SqlDialects.MYSQL);
		identitySet.add(SqlDialects.POSTGRESQL);
		identitySet.add(SqlDialects.SQLITE);
		identitySet.add(SqlDialects.SQL_SERVER);
		assertEquals(7, identitySet.size());
	}
}
