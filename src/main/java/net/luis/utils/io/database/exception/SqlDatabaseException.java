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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

/**
 * Base type for all database originated sql failures.<br>
 * <p>
 *     A database exception represents a failure that reached the jdbc driver and produced a {@link SQLException}.<br>
 *     Its diagnostics ({@link SQLException#getSQLState() sql state}, {@link SQLException#getErrorCode() vendor error code} and the transient nature of the failure)<br>
 *     are lifted onto this exception so callers can inspect them without unwrapping the cause.
 * </p>
 * <p>
 *     When constructed from a {@link SQLException}, every chained {@link SQLException#getNextException() next exception}<br>
 *     is added to the {@link #getSuppressed() suppressed} list so batch detail is preserved.
 * </p>
 *
 * @author Luis-St
 */
public class SqlDatabaseException extends SqlException {
	
	/**
	 * Sentinel value used for {@link #vendorErrorCode} when the vendor error code is unknown.<br>
	 */
	private static final int UNKNOWN_ERROR_CODE = -1;
	
	/**
	 * The {@code SQLState} string of the underlying failure, or null if unknown.<br>
	 */
	private final @Nullable String sqlState;
	/**
	 * The vendor specific error code of the underlying failure, or {@code -1} if unknown.<br>
	 */
	private final int vendorErrorCode;
	/**
	 * Whether the underlying failure is transient and may succeed if the operation is retried.<br>
	 */
	private final boolean transient0;
	
	/**
	 * Constructs a new database exception from the given message and {@link SQLException} cause.<br>
	 * The diagnostics of the cause are lifted onto this exception and its chained next exceptions are added to the suppressed list.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlDatabaseException(@Nullable String message, @NonNull SQLException cause) {
		super(message, Objects.requireNonNull(cause, "Cause must not be null"));
		this.sqlState = cause.getSQLState();
		this.vendorErrorCode = cause.getErrorCode();
		this.transient0 = cause instanceof java.sql.SQLTransientException || cause instanceof java.sql.SQLRecoverableException;
		for (SQLException next = cause.getNextException(); next != null; next = next.getNextException()) {
			this.addSuppressed(next);
		}
	}
	
	/**
	 * Constructs a new database exception from the given message and {@link SQLException} cause, explicitly marking whether the failure is transient.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param isTransient Whether the failure is transient
	 * @throws NullPointerException If the cause is null
	 */
	public SqlDatabaseException(@Nullable String message, @NonNull SQLException cause, boolean isTransient) {
		super(message, Objects.requireNonNull(cause, "Cause must not be null"));
		this.sqlState = cause.getSQLState();
		this.vendorErrorCode = cause.getErrorCode();
		this.transient0 = isTransient;
		for (SQLException next = cause.getNextException(); next != null; next = next.getNextException()) {
			this.addSuppressed(next);
		}
	}
	
	/**
	 * Constructs a new hand-built database exception that did not originate from a {@link SQLException}.<br>
	 *
	 * @param message The detail message, may be null
	 * @param sqlState The {@code SQLState} string, may be null
	 * @param vendorErrorCode The vendor specific error code, or {@code -1} if unknown
	 */
	public SqlDatabaseException(@Nullable String message, @Nullable String sqlState, int vendorErrorCode) {
		super(message);
		this.sqlState = sqlState;
		this.vendorErrorCode = vendorErrorCode;
		this.transient0 = false;
	}
	
	/**
	 * Returns the {@code SQLState} string of the underlying failure.<br>
	 * @return An optional holding the {@code SQLState} string, or an empty optional if unknown
	 */
	public @NonNull Optional<String> sqlState() {
		return Optional.ofNullable(this.sqlState);
	}
	
	/**
	 * Returns the vendor specific error code of the underlying failure.<br>
	 * @return An optional int holding the vendor error code, or an empty optional int if unknown ({@code -1})
	 */
	public @NonNull OptionalInt vendorErrorCode() {
		return this.vendorErrorCode == UNKNOWN_ERROR_CODE ? OptionalInt.empty() : OptionalInt.of(this.vendorErrorCode);
	}
	
	/**
	 * Returns whether the underlying failure is transient and may succeed if the operation is retried.<br>
	 * @return True if the failure is transient, otherwise false
	 */
	public boolean isTransient() {
		return this.transient0;
	}
	
	/**
	 * Returns the {@link SqlStateClass} parsed from the {@link #sqlState()} of this exception.<br>
	 * @return An optional holding the matching state class, or an empty optional if the state is unknown or unrecognized
	 */
	public @NonNull Optional<SqlStateClass> sqlStateClass() {
		return SqlStateClass.fromState(this.sqlState);
	}
}
