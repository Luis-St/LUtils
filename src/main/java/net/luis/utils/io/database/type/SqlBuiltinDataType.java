package net.luis.utils.io.database.type;

import net.luis.utils.function.throwable.ThrowableBiFunction;
import net.luis.utils.io.database.exception.SqlMappingException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

record SqlBuiltinDataType<T>(
	@NonNull Class<T> javaType,
	int jdbcType,
	@NonNull ThrowableBiFunction<ResultSet, Integer, T, SQLException> indexGetter,
	@NonNull ThrowableBiFunction<ResultSet, String, T, SQLException> nameGetter
) implements SqlDataType<T> {
	
	SqlBuiltinDataType {
		Objects.requireNonNull(javaType, "Java type must not be null");
		Objects.requireNonNull(indexGetter, "Index getter must not be null");
		Objects.requireNonNull(nameGetter, "Name getter must not be null");
	}
	
	@Override
	public @Nullable T get(@NonNull ResultSet result, int columnIndex) throws SqlMappingException {
		Objects.requireNonNull(result, "Result set must not be null");
		
		try {
			return this.indexGetter.apply(result, columnIndex);
		} catch (SQLFeatureNotSupportedException e) {
			throw new SqlMappingException("Fail to retrieve value of type " + this.javaType.getName() + " at column index " + columnIndex + ": " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new SqlMappingException("Sql error while retrieving value of type " + this.javaType.getName() + " at column index " + columnIndex + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public @Nullable T get(@NonNull ResultSet result, @NonNull String columnName) throws SqlMappingException {
		Objects.requireNonNull(result, "Result set must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
		
		try {
			return this.nameGetter.apply(result, columnName);
		} catch (SQLFeatureNotSupportedException e) {
			throw new SqlMappingException("Fail to retrieve value of type " + this.javaType.getName() + " at column name '" + columnName + "': " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new SqlMappingException("Sql error while retrieving value of type " + this.javaType.getName() + " at column name '" + columnName + "': " + e.getMessage(), e);
		}
	}
}
