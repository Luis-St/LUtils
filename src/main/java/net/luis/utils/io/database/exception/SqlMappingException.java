package net.luis.utils.io.database.exception;

import org.jspecify.annotations.Nullable;

import java.sql.SQLException;

/**
 *
 * @author Luis-St
 *
 */

public class SqlMappingException extends SqlException {
	
	public SqlMappingException(@Nullable String message) {
		super(message);
	}
	
	public SqlMappingException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public SqlMappingException(@Nullable Throwable cause) {
		super(cause);
	}
}
