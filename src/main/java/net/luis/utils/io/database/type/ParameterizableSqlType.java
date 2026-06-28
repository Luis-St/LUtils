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
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a sql type template that must be parameterized with a {@link SqlParameter} before it can be used.<br>
 * Some sql types such as {@code VARCHAR(n)} or {@code DECIMAL(p, s)} require concrete parameter values, like a length or a precision and scale.<br>
 * Applying such a parameter through {@link #configure(SqlParameter)} yields a concrete {@link SqlType} that can bind and read values.<br>
 *
 * @see SqlType
 * @see ParameterizedSqlType
 * @see SqlParameter
 *
 * @author Luis-St
 *
 * @param <T> The java type the configured sql type maps to
 * @param <P> The type of parameter required to configure this type
 */
public sealed interface ParameterizableSqlType<T, P extends SqlParameter> permits DirectParameterizableSqlType, MappedParameterizableSqlType {
	
	/**
	 * Returns the class of the parameter that is required to configure this type.<br>
	 * @return The parameter type class
	 */
	@NonNull Class<P> parameterType();
	
	/**
	 * Configures this type with the given parameter and returns a concrete sql type.<br>
	 *
	 * @param parameter The parameter to apply to this type
	 * @return The configured sql type
	 * @throws NullPointerException If the parameter is null
	 */
	@NonNull SqlType<T> configure(@NonNull P parameter);
	
	/**
	 * Maps this parameterizable type to a new parameterizable type that uses a different java type.<br>
	 * The given functions are used to convert between the source and target java type when binding and reading values once the resulting type has been configured.<br>
	 *
	 * @param targetType The java type the resulting type maps to
	 * @param fromTargetToSource The function converting a target value into a source value when binding a value
	 * @param fromSourceToTarget The function converting a source value into a target value when reading a value
	 * @param <O> The target java type to map to
	 * @return A parameterizable type that maps values to and from the target type
	 * @throws NullPointerException If the target type or any of the functions is null
	 */
	default <O> @NonNull ParameterizableSqlType<O, P> map(
		@NonNull Class<O> targetType,
		@NonNull ThrowableFunction<@Nullable O, @Nullable T, SqlStatementBindException> fromTargetToSource,
		@NonNull ThrowableFunction<@NonNull T, @Nullable O, SqlClientException> fromSourceToTarget
	) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		Objects.requireNonNull(fromTargetToSource, "Getter function must not be null");
		Objects.requireNonNull(fromSourceToTarget, "Setter function must not be null");
		
		return new MappedParameterizableSqlType<>(this, targetType, fromTargetToSource, fromSourceToTarget);
	}
}
