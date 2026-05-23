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

package net.luis.utils.io.database.type;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlArrayType<E> implements SqlType<E[]> {
	
	private final SqlType<E> elementType;
	
	SqlArrayType(@NonNull SqlType<E> elementType) {
		Objects.requireNonNull(elementType, "Element type must not be null");
		
		if (elementType instanceof SqlArrayType) {
			throw new IllegalArgumentException("Element type must not be nested array type");
		}
		this.elementType = elementType;
	}
	
	public @NonNull SqlType<E> elementType() {
		return this.elementType;
	}
	
	@Override
	public int jdbcType() {
		return Types.ARRAY;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<E[]> javaType() {
		return (Class<E[]>) Array.newInstance(this.elementType.javaType(), 0).getClass();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public E @Nullable [] get(@NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		Objects.requireNonNull(resultSet, "Result set must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Column index must be greater than or equal to 1");
		}
		
		try {
			java.sql.Array arr = resultSet.getArray(columnIndex);
			if (arr == null) {
				return null;
			}
			
			try {
				Object arrayData = arr.getArray();
				int length = Array.getLength(arrayData);
				
				E[] result = (E[]) Array.newInstance(this.elementType.javaType(), length);
				for (int i = 0; i < length; i++) {
					result[i] = this.elementType.javaType().cast(Array.get(arrayData, i));
				}
				return result;
			} finally {
				arr.free();
			}
		} catch (SQLException e) {
			throw new SqlResultMappingException("Failed to retrieve array value from result set at column index " + columnIndex, e, this.javaType(), null);
		}
	}
	
	@Override
	public void set(@NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, E @Nullable [] value) throws SqlException {
		Objects.requireNonNull(dialect, "Dialect must not be null");
		Objects.requireNonNull(preparedStatement, "Prepared statement must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than or equal to 1");
		}
		
		try {
			if (value == null) {
				preparedStatement.setNull(columnIndex, Types.ARRAY);
				return;
			}
			
			if (!dialect.isTypeSupported(this.elementType)) {
				throw new SqlDialectUnsupportedRenderingException("Element type " + this.elementType + " is not supported by the current sql dialect " + dialect.name());
			}
			
			java.sql.Array array = preparedStatement.getConnection().createArrayOf(dialect.getTypeName(this.elementType), value);
			preparedStatement.setArray(columnIndex, array);
		} catch (SQLException e) {
			throw new SqlStatementBindException("Failed to bind array value to prepared statement at column index " + columnIndex, e, columnIndex);
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlArrayType<?> that)) return false;
		
		return this.elementType.equals(that.elementType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elementType);
	}
	
	@Override
	public @NonNull String toString() {
		return "SqlArrayType[elementType=" + this.elementType + "]";
	}
	//endregion
}
