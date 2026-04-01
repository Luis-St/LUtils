package net.luis.utils.io.database.exception;

import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class SqlConnectionException extends SqlException {
	
	public SqlConnectionException(@Nullable String message) {
		super(message);
	}
	
	public SqlConnectionException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public SqlConnectionException(@Nullable Throwable cause) {
		super(cause);
	}
}
