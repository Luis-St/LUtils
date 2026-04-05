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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.type.SqlResultRetrievalException;
import net.luis.utils.io.database.exception.type.SqlStatementBindException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Luis-St
 *
 */

record MappedSqlType<S, T>(
	@NonNull SqlType<S> sourceType,
	@NonNull Class<T> javaType,
	@NonNull ThrowableFunction<@Nullable T, @Nullable S, SqlStatementBindException> fromTargetToSource,
	@NonNull ThrowableFunction<@NonNull S, @Nullable T, SqlResultRetrievalException> fromSourceToTarget
) implements SqlType<T> {
	
	@Override
	public int jdbcType() {
		return this.sourceType.jdbcType();
	}
	
	@Override
	public @Nullable T get(@NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		S source = this.sourceType.get(resultSet, columnIndex);
		if (source == null) {
			return null;
		}
		return this.fromSourceToTarget.apply(source);
	}
	
	@Override
	public void set(@NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, @Nullable T value) throws SqlException {
		this.sourceType.set(dialect, preparedStatement, columnIndex, this.fromTargetToSource.apply(value));
	}
}
