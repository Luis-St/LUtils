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
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.exception.type.SqlResultRetrievalException;
import net.luis.utils.io.database.exception.type.SqlStatementBindException;
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

public record SqlArrayType<E>(@NonNull SqlType<E> elementType) implements SqlType<E[]> {
	
	public SqlArrayType {
		Objects.requireNonNull(elementType, "Element type must not be null");
		
		if (elementType instanceof SqlArrayType) {
			throw new IllegalArgumentException("Element type must not be nested array type");
		}
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
	public E @Nullable [] get(@NonNull ResultSet resultSet, int columnIndex) throws SqlResultRetrievalException {
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
				Object[] raw = (Object[]) arr.getArray();
				
				E[] result = (E[]) Array.newInstance(this.elementType.javaType(), raw.length);
				for (int i = 0; i < raw.length; i++) {
					result[i] = this.elementType.javaType().cast(raw[i]);
				}
				return result;
			} finally {
				arr.free();
			}
		} catch (SQLException e) {
			throw new SqlResultRetrievalException("Failed to retrieve array value from result set at column index " + columnIndex, e);
		}
	}
	
	@Override
	public void set(@NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, E @Nullable [] value) throws SqlException {
		Objects.requireNonNull(dialect, "Dialect must not be null");
		Objects.requireNonNull(preparedStatement, "Prepared statement must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Column index must be greater than or equal to 1");
		}
		
		try {
			if (value == null) {
				preparedStatement.setNull(columnIndex, Types.ARRAY);
				return;
			}
			
			if (!dialect.isTypeSupported(this.elementType)) {
				throw new SqlDialectUnsupportedTypeException("Element type " + this.elementType + " is not supported by the current sql dialect " + dialect.name());
			}
			
			java.sql.Array array = preparedStatement.getConnection().createArrayOf(dialect.getTypeName(this.elementType), value);
			try {
				preparedStatement.setArray(columnIndex, array);
			} finally {
				array.free();
			}
		} catch (SQLException e) {
			throw new SqlStatementBindException("Failed to bind array value to prepared statement at column index " + columnIndex, e);
		}
	}
}
