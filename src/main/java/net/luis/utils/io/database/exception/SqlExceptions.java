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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.util.Objects;

/**
 * Utility class that translates a raw {@link SQLException} into the most specific {@link SqlDatabaseException}<br>
 * leaf type based on the concrete exception subclass and the reported {@code SQLState}.<br>
 * <p>
 *     This is the single routing point used by the database system so that call sites produce information-rich,<br>
 *     catchable exceptions instead of a generic catch-all.
 * </p>
 *
 * @author Luis-St
 */
public final class SqlExceptions {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static utility class.<br>
	 */
	private SqlExceptions() {}
	
	/**
	 * Translates the given {@link SQLException} into the most specific {@link SqlDatabaseException} leaf type.<br>
	 * The concrete exception subclass is inspected first, then the {@code SQLState}.<br>
	 * <p>
	 *     If neither identifies a specific failure mode,<br>
	 *     a plain {@link SqlDatabaseException} is returned with its transient flag derived from<br>
	 * 	   the {@link SQLTransientException}/{@link SQLRecoverableException} markers.
	 * </p>
	 *
	 * @param context The contextual detail message describing the failed operation, may be null
	 * @param cause The sql exception to translate
	 * @return The translated database exception
	 * @throws NullPointerException If the cause is null
	 */
	public static @NonNull SqlDatabaseException translate(@Nullable String context, @NonNull SQLException cause) {
		Objects.requireNonNull(cause, "Cause must not be null");
		
		String state = cause.getSQLState();
		if (cause instanceof SQLIntegrityConstraintViolationException || isStateClass(state, "23")) {
			return translateConstraint(context, cause, state);
		} else if (cause instanceof SQLSyntaxErrorException || isStateClass(state, "42")) {
			return new SqlSyntaxException(context, cause);
		} else if (cause instanceof SQLTimeoutException) {
			return new SqlTimeoutException(context, cause);
		} else if (cause instanceof SQLTransactionRollbackException || "40001".equals(state) || "40P01".equalsIgnoreCase(state)) {
			return new SqlDeadlockException(context, cause);
		} else if (cause instanceof SQLNonTransientConnectionException || cause instanceof SQLTransientConnectionException || isStateClass(state, "08")) {
			return new SqlConnectionException(context, cause);
		} else if (cause instanceof SQLDataException || isStateClass(state, "22")) {
			return new SqlDataException(context, cause);
		} else if (cause instanceof SQLInvalidAuthorizationSpecException || isStateClass(state, "28")) {
			return new SqlAuthorizationException(context, cause);
		}
		
		boolean isTransient = cause instanceof SQLTransientException || cause instanceof SQLRecoverableException;
		return new SqlDatabaseException(context, cause, isTransient);
	}
	
	/**
	 * Translates an integrity constraint violation into the most specific constraint leaf type based on its {@code SQLState} subclass.<br>
	 *
	 * @param context The contextual detail message, may be null
	 * @param cause The sql exception to translate
	 * @param state The reported {@code SQLState}, may be null
	 * @return The translated constraint violation exception
	 * @throws NullPointerException If the cause is null
	 */
	private static @NonNull SqlConstraintViolationException translateConstraint(@Nullable String context, @NonNull SQLException cause, @Nullable String state) {
		Objects.requireNonNull(cause, "Cause must not be null");
		
		return switch (state) {
			case "23505" -> new SqlUniqueConstraintException(context, cause);
			case "23503" -> new SqlForeignKeyConstraintException(context, cause);
			case "23502" -> new SqlNotNullConstraintException(context, cause);
			case "23514" -> new SqlCheckConstraintException(context, cause);
			case null, default -> new SqlConstraintViolationException(context, cause);
		};
	}
	
	/**
	 * Checks whether the given {@code SQLState} starts with the given two character class prefix.<br>
	 *
	 * @param state The {@code SQLState} to check, may be null
	 * @param prefix The two character class prefix
	 * @return True if the state is non-null and starts with the prefix, otherwise false
	 * @throws NullPointerException If the prefix is null
	 */
	private static boolean isStateClass(@Nullable String state, @NonNull String prefix) {
		Objects.requireNonNull(prefix, "Prefix must not be null");
		
		return state != null && state.length() >= 2 && state.regionMatches(true, 0, prefix, 0, 2);
	}
}
