package net.luis.utils.io.database.type;

import net.luis.utils.io.database.exception.SqlMappingException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlCustomDataType<T> implements SqlDataType<T> {
	
	@Override
	public @NonNull Class<T> javaType() {
		return null;
	}
	
	@Override
	public int jdbcType() {
		return 0;
	}
	
	@Override
	public @Nullable T get(@NonNull ResultSet result, int columnIndex) throws SqlMappingException {
		Objects.requireNonNull(result, "Result set must not be null");
		
		return null;
	}
	
	@Override
	public @Nullable T get(@NonNull ResultSet result, @NonNull String columnName) throws SqlMappingException {
		Objects.requireNonNull(result, "Result set must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		
		return null;
	}
}
