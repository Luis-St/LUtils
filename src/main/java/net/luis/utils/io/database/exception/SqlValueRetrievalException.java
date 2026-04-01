package net.luis.utils.io.database.exception;

import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class SqlValueRetrievalException extends SqlException {
	
	public SqlValueRetrievalException(@Nullable String message) {
		super(message);
	}
	
	public SqlValueRetrievalException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public SqlValueRetrievalException(@Nullable Throwable cause) {
		super(cause);
	}
}
