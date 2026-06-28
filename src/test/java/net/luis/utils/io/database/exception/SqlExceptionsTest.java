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

package net.luis.utils.io.database.exception;

import net.luis.utils.io.database.exception.database.*;
import net.luis.utils.io.database.exception.database.concurrency.SqlDeadlockException;
import net.luis.utils.io.database.exception.database.concurrency.SqlTimeoutException;
import net.luis.utils.io.database.exception.database.constraint.*;
import net.luis.utils.io.database.exception.database.statement.SqlSyntaxException;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlExceptions}.<br>
 *
 * @author Luis-St
 */
class SqlExceptionsTest {
	
	@Test
	void translateWithNullCause() {
		assertThrows(NullPointerException.class, () -> SqlExceptions.translate("ctx", null));
	}
	
	@Test
	void translateIntegrityConstraintBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLIntegrityConstraintViolationException("dup"));
		assertInstanceOf(SqlConstraintViolationException.class, result);
	}
	
	@Test
	void translateUniqueConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("dup", "23505"));
		assertInstanceOf(SqlUniqueConstraintException.class, result);
	}
	
	@Test
	void translateForeignKeyConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("fk", "23503"));
		assertInstanceOf(SqlForeignKeyConstraintException.class, result);
	}
	
	@Test
	void translateNotNullConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("nn", "23502"));
		assertInstanceOf(SqlNotNullConstraintException.class, result);
	}
	
	@Test
	void translateCheckConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("chk", "23514"));
		assertInstanceOf(SqlCheckConstraintException.class, result);
	}
	
	@Test
	void translateGenericConstraintByUnknownState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("x", "23999"));
		assertEquals(SqlConstraintViolationException.class, result.getClass());
	}
	
	@Test
	void translateSyntaxBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLSyntaxErrorException("bad"));
		assertInstanceOf(SqlSyntaxException.class, result);
	}
	
	@Test
	void translateSyntaxByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("bad", "42601"));
		assertInstanceOf(SqlSyntaxException.class, result);
	}
	
	@Test
	void translateTimeoutBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTimeoutException("slow"));
		assertInstanceOf(SqlTimeoutException.class, result);
	}
	
	@Test
	void translateDeadlockBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTransactionRollbackException("rb"));
		assertInstanceOf(SqlDeadlockException.class, result);
	}
	
	@Test
	void translateDeadlockBySerializationFailureState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("rb", "40001"));
		assertInstanceOf(SqlDeadlockException.class, result);
	}
	
	@Test
	void translateDeadlockByDeadlockStateCaseInsensitive() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("rb", "40p01"));
		assertInstanceOf(SqlDeadlockException.class, result);
	}
	
	@Test
	void translateConnectionByNonTransientSubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLNonTransientConnectionException("down"));
		assertInstanceOf(SqlConnectionException.class, result);
	}
	
	@Test
	void translateConnectionByTransientSubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTransientConnectionException("down"));
		assertInstanceOf(SqlConnectionException.class, result);
	}
	
	@Test
	void translateConnectionByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("down", "08006"));
		assertInstanceOf(SqlConnectionException.class, result);
	}
	
	@Test
	void translateDataBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLDataException("bad"));
		assertInstanceOf(SqlDataException.class, result);
	}
	
	@Test
	void translateDataByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("bad", "22003"));
		assertInstanceOf(SqlDataException.class, result);
	}
	
	@Test
	void translateAuthorizationBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLInvalidAuthorizationSpecException("denied"));
		assertInstanceOf(SqlAuthorizationException.class, result);
	}
	
	@Test
	void translateAuthorizationByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("denied", "28000"));
		assertInstanceOf(SqlAuthorizationException.class, result);
	}
	
	@Test
	void translateUnknownExceptionFallsBackToGeneric() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("weird"));
		assertEquals(SqlDatabaseException.class, result.getClass());
		assertFalse(result.isTransient());
	}
	
	@Test
	void translateUnknownExceptionWithUnmatchedState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("weird", "99999"));
		assertEquals(SqlDatabaseException.class, result.getClass());
	}
	
	@Test
	void translateUnknownExceptionWithShortState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("weird", "2"));
		assertEquals(SqlDatabaseException.class, result.getClass());
	}
	
	@Test
	void translateTransientExceptionMarksTransient() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTransientException("retry"));
		assertEquals(SqlDatabaseException.class, result.getClass());
		assertTrue(result.isTransient());
	}
	
	@Test
	void translateRecoverableExceptionMarksTransient() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLRecoverableException("retry"));
		assertEquals(SqlDatabaseException.class, result.getClass());
		assertTrue(result.isTransient());
	}
	
	@Test
	void translatePreservesNullContext() {
		SQLException cause = new SQLDataException("x");
		SqlDatabaseException result = SqlExceptions.translate(null, cause);
		assertInstanceOf(SqlDataException.class, result);
		assertSame(cause, result.getCause());
	}
	
	@Test
	void translatePreservesContextAndCause() {
		SQLException cause = new SQLDataException("x");
		SqlDatabaseException result = SqlExceptions.translate("loading user", cause);
		assertSame(cause, result.getCause());
		assertEquals("loading user", result.getMessage());
	}
	
	@Test
	void translateEarlierBranchWinsOverLaterBranch() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTimeoutException("slow", "08006"));
		assertInstanceOf(SqlTimeoutException.class, result);
	}
	
	@Test
	void translateStateBranchWinsOverLaterSubclassBranch() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLDataException("x", "23505"));
		assertInstanceOf(SqlUniqueConstraintException.class, result);
	}
}
