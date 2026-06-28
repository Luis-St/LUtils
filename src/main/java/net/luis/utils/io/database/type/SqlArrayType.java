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
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.Objects;

/**
 * A sql type representing an array whose elements are of a given element type.<br>
 * Nested array types are not supported, the element type must not itself be an array type.<br>
 *
 * @see SqlType
 *
 * @author Luis-St
 *
 * @param <E> The java type of the array elements
 */
@SuppressWarnings({ "unchecked", "ClassEscapesDefinedScope" })
public final class SqlArrayType<E> implements SqlType<E[]> {
	
	/**
	 * The sql type of the array elements.
	 */
	private final SqlType<E> elementType;
	
	/**
	 * Constructs a new array sql type with the given element type.<br>
	 *
	 * @param elementType The sql type of the array elements
	 * @throws NullPointerException If the element type is null
	 * @throws IllegalArgumentException If the element type is itself an array type
	 */
	SqlArrayType(@NonNull SqlType<E> elementType) {
		Objects.requireNonNull(elementType, "Element type must not be null");
		
		if (elementType instanceof SqlArrayType) {
			throw new IllegalArgumentException("Element type must not be nested array type");
		}
		this.elementType = elementType;
	}
	
	/**
	 * Converts the given value into the source representation of the given sql type.<br>
	 * For a {@link MappedSqlType mapped} type the value is recursively converted using its target-to-source function, otherwise the value is returned unchanged.<br>
	 *
	 * @param type The sql type to convert the value for
	 * @param value The value to convert
	 * @return The converted source value
	 * @throws NullPointerException If the type is null
	 * @throws SqlException If the value could not be converted
	 */
	private static @Nullable Object toSourceValue(@NonNull SqlType<?> type, @Nullable Object value) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		if (type instanceof MappedSqlType<?, ?> mapped) {
			return toSourceValue(mapped.sourceType(), ((MappedSqlType<Object, Object>) mapped).fromTargetToSource().apply(value));
		}
		return value;
	}
	
	/**
	 * Converts the given source value into the target representation of the given sql type.<br>
	 * For a {@link MappedSqlType mapped} type the value is recursively converted using its source-to-target function, otherwise the value is returned unchanged.<br>
	 *
	 * @param type The sql type to convert the value for
	 * @param value The source value to convert
	 * @return The converted value or {@code null} if the converted source value is null
	 * @throws NullPointerException If the type is null
	 * @throws SqlException If the value could not be converted
	 */
	private static @Nullable Object fromSourceValue(@NonNull SqlType<?> type, @Nullable Object value) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		if (type instanceof MappedSqlType<?, ?> mapped) {
			Object source = fromSourceValue(mapped.sourceType(), value);
			if (source == null) {
				return null;
			}
			
			return ((MappedSqlType<Object, Object>) mapped).fromSourceToTarget().apply(source);
		}
		return value;
	}
	
	/**
	 * Returns the sql type of the array elements.<br>
	 * @return The element sql type
	 */
	public @NonNull SqlType<E> elementType() {
		return this.elementType;
	}
	
	@Override
	public int jdbcType() {
		return Types.ARRAY;
	}
	
	@Override
	public @NonNull Class<E[]> javaType() {
		return (Class<E[]>) Array.newInstance(this.elementType.javaType(), 0).getClass();
	}
	
	@Override
	@ApiStatus.Internal
	public E @Nullable [] get(@NonNull SqlTypeInternalAccess access, @NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		if (access == null) {
			throw new IllegalCallerException("SqlType#get should only be called from inside the net.luis.utils.io.database.type package, external callers should use SqlType#getValue");
		}
		
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
					result[i] = this.elementType.javaType().cast(fromSourceValue(this.elementType, Array.get(arrayData, i)));
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
	@ApiStatus.Internal
	public void set(@NonNull SqlTypeInternalAccess access, @NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, E @Nullable [] value) throws SqlException {
		if (access == null) {
			throw new IllegalCallerException("SqlType#set should only be called from inside the net.luis.utils.io.database.type package, external callers should use SqlType#setValue");
		}
		
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(preparedStatement, "Prepared statement must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than or equal to 1");
		}
		
		try {
			if (value == null) {
				preparedStatement.setNull(columnIndex, Types.ARRAY);
				return;
			}
			
			SqlType<?> baseType = this.elementType.baseType();
			if (!dialect.isTypeSupported(baseType)) {
				throw new SqlDialectUnsupportedRenderingException("Element type " + this.elementType + " is not supported by the current sql dialect " + dialect.name());
			}
			
			Object[] elements = new Object[value.length];
			for (int i = 0; i < value.length; i++) {
				elements[i] = toSourceValue(this.elementType, value[i]);
			}
			
			java.sql.Array array = preparedStatement.getConnection().createArrayOf(dialect.getTypeName(baseType), elements);
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
