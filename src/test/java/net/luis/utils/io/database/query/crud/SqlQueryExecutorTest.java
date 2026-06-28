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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlQueryExecutor}.<br>
 *
 * @author Luis-St
 */
class SqlQueryExecutorTest {
	
	@Test
	void resolveUserWithNullProvider() {
		assertNull(SqlQueryExecutor.resolveUser(null));
	}
	
	@Test
	void resolveUserWithEmptyOptional() {
		SqlAuditUserProvider provider = Optional::empty;
		assertNull(SqlQueryExecutor.resolveUser(provider));
	}
	
	@Test
	void resolveUserWithPresentValue() {
		SqlAuditUserProvider provider = () -> Optional.of("alice");
		assertEquals("alice", SqlQueryExecutor.resolveUser(provider));
	}
	
	@Test
	void executeBatchedUpdateWithEmptyListReturnsZero() throws SqlException {
		assertEquals(0, SqlQueryExecutor.executeBatchedUpdate(DIALECT, SOURCE, List.of(), TIMEOUT));
	}
	
	@Test
	void executeUpdateReturningKeysWithEmptyListReturnsEmpty() throws SqlException {
		assertTrue(SqlQueryExecutor.executeUpdateReturningKeys(DIALECT, SOURCE, List.of(), TIMEOUT).isEmpty());
	}
	
	@Test
	void executeReturningQueryOnUnsupportedDialectThrows() {
		SqlRendered rendered = SqlRenderer.empty().toSql();
		assertThrows(SqlDialectFeatureException.class, () -> SqlQueryExecutor.executeReturningQuery(DIALECT, SOURCE, rendered, rendered, TIMEOUT, resultSet -> null));
	}
}
