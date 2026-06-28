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

package net.luis.utils.io.database.expression;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A sql expression that represents a constant value of a given sql type.<br>
 *
 * @author Luis-St
 *
 * @param value The constant value
 * @param type The sql type of the value
 * @param <T> The type of the value
 */
public record SqlValueExpression<T>(
	@NonNull T value,
	@NonNull SqlType<T> type
) implements SqlExpression<T> {
	
	/**
	 * Constructs a new value expression inferring the sql type from the given value.<br>
	 *
	 * @param value The constant value
	 * @throws NullPointerException If the value is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the value
	 */
	public SqlValueExpression(@NonNull T value) throws SqlTypeNotFoundException {
		Objects.requireNonNull(value, "Sql value must not be null");
		this(value, SqlType.inferType(value));
	}
	
	/**
	 * Constructs a new value expression with the given value and sql type.<br>
	 * @throws NullPointerException If the value or type is null
	 */
	public SqlValueExpression {
		Objects.requireNonNull(value, "Sql value must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return dialect.renderExpression(this);
	}
}
