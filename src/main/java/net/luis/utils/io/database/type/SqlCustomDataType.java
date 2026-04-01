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
