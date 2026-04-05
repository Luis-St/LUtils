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
import net.luis.utils.io.database.exception.type.SqlResultRetrievalException;
import net.luis.utils.io.database.exception.type.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

record MappedParameterizableSqlType<S, T, P extends SqlParameter>(
	@NonNull ParameterizableSqlType<S, P> sourceType,
	@NonNull Class<T> javaType,
	@NonNull ThrowableFunction<@Nullable T, @Nullable S, SqlStatementBindException> fromTargetToSource,
	@NonNull ThrowableFunction<@NonNull S, @Nullable T, SqlResultRetrievalException> fromSourceToTarget
) implements ParameterizableSqlType<T, P> {
	
	MappedParameterizableSqlType {
		Objects.requireNonNull(sourceType, "Source type must not be null");
		Objects.requireNonNull(javaType, "Java type must not be null");
		Objects.requireNonNull(fromTargetToSource, "Getter function must not be null");
		Objects.requireNonNull(fromSourceToTarget, "Setter function must not be null");
	}
	
	@Override
	public @NonNull Class<P> parameterType() {
		return this.sourceType.parameterType();
	}
	
	@Override
	public @NonNull SqlType<T> configure(@NonNull P parameter) {
		return this.sourceType.configure(parameter).map(this.javaType, this.fromTargetToSource, this.fromSourceToTarget);
	}
}
