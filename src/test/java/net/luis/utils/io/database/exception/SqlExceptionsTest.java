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
import net.luis.utils.io.database.exception.database.concurrency.*;
import net.luis.utils.io.database.exception.database.constraint.*;
import net.luis.utils.io.database.exception.database.statement.*;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlExceptions}.<br>
 *
 * @author Luis-St
 */
class SqlExceptionsTest {

	@Test
	void translateRejectsNullCause() {
		assertThrows(NullPointerException.class, () -> SqlExceptions.translate("context", null));
	}

	@Test
	void translateUniqueConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("dup", "23505", 1062));
		assertInstanceOf(SqlUniqueConstraintException.class, result);
		assertEquals(Optional.of("23505"), result.sqlState());
		assertEquals(OptionalInt.of(1062), result.vendorErrorCode());
		assertEquals(Optional.of(SqlStateClass.INTEGRITY_CONSTRAINT), result.sqlStateClass());
		assertFalse(result.isTransient());
	}

	@Test
	void translateForeignKeyNotNullAndCheckConstraints() {
		assertInstanceOf(SqlForeignKeyConstraintException.class, SqlExceptions.translate("ctx", new SQLException("fk", "23503", 0)));
		assertInstanceOf(SqlNotNullConstraintException.class, SqlExceptions.translate("ctx", new SQLException("nn", "23502", 0)));
		assertInstanceOf(SqlCheckConstraintException.class, SqlExceptions.translate("ctx", new SQLException("ck", "23514", 0)));
	}

	@Test
	void translateGenericConstraintByState() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("ic", "23000", 0));
		assertInstanceOf(SqlConstraintViolationException.class, result);
		assertFalse(result instanceof SqlUniqueConstraintException);
	}

	@Test
	void translateConstraintBySubclass() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLIntegrityConstraintViolationException("ic", "23000", 0));
		assertInstanceOf(SqlConstraintViolationException.class, result);
	}

	@Test
	void translateSyntax() {
		assertInstanceOf(SqlSyntaxException.class, SqlExceptions.translate("ctx", new SQLException("bad", "42601", 0)));
		assertInstanceOf(SqlSyntaxException.class, SqlExceptions.translate("ctx", new SQLSyntaxErrorException("bad", "42000", 0)));
	}

	@Test
	void translateTimeout() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTimeoutException("slow", "HYT00", 0));
		assertInstanceOf(SqlTimeoutException.class, result);
		assertTrue(result.isTransient());
	}

	@Test
	void translateDeadlock() {
		SqlDatabaseException byState = SqlExceptions.translate("ctx", new SQLException("dl", "40001", 0));
		assertInstanceOf(SqlDeadlockException.class, byState);
		assertTrue(byState.isTransient());
		assertInstanceOf(SqlDeadlockException.class, SqlExceptions.translate("ctx", new SQLException("dl", "40P01", 0)));
		assertInstanceOf(SqlDeadlockException.class, SqlExceptions.translate("ctx", new SQLTransactionRollbackException("dl", "40000", 0)));
	}

	@Test
	void translateConnection() {
		assertInstanceOf(SqlConnectionException.class, SqlExceptions.translate("ctx", new SQLException("conn", "08006", 0)));
		assertInstanceOf(SqlConnectionException.class, SqlExceptions.translate("ctx", new SQLNonTransientConnectionException("conn", "08001", 0)));
	}

	@Test
	void translateData() {
		assertInstanceOf(SqlDataException.class, SqlExceptions.translate("ctx", new SQLException("ovf", "22003", 0)));
		assertInstanceOf(SqlDataException.class, SqlExceptions.translate("ctx", new SQLDataException("ovf", "22001", 0)));
	}

	@Test
	void translateAuthorization() {
		assertInstanceOf(SqlAuthorizationException.class, SqlExceptions.translate("ctx", new SQLException("auth", "28000", 0)));
		assertInstanceOf(SqlAuthorizationException.class, SqlExceptions.translate("ctx", new SQLInvalidAuthorizationSpecException("auth", "28P01", 0)));
	}

	@Test
	void translateUnknownFallsBackToBase() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("weird", "99999", 7));
		assertEquals(SqlDatabaseException.class, result.getClass());
		assertEquals(Optional.of("99999"), result.sqlState());
		assertEquals(OptionalInt.of(7), result.vendorErrorCode());
		assertEquals(Optional.empty(), result.sqlStateClass());
		assertFalse(result.isTransient());
	}

	@Test
	void translateUnknownTransientMarkerIsTransient() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLTransientException("retry", null, 0));
		assertEquals(SqlDatabaseException.class, result.getClass());
		assertTrue(result.isTransient());
	}

	@Test
	void translateUnknownStateHasNoVendorCode() {
		SqlDatabaseException result = SqlExceptions.translate("ctx", new SQLException("x", null, -1));
		assertEquals(Optional.empty(), result.sqlState());
		assertEquals(OptionalInt.empty(), result.vendorErrorCode());
	}

	@Test
	void translateChainsNextExceptionIntoSuppressed() {
		SQLException root = new SQLException("first", "99999", 1);
		root.setNextException(new SQLException("second", "99999", 2));
		SqlDatabaseException result = SqlExceptions.translate("ctx", root);
		assertEquals(1, result.getSuppressed().length);
		assertEquals("second", result.getSuppressed()[0].getMessage());
	}
}
