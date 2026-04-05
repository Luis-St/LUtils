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

import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record ParameterizedSqlType<T, P extends SqlParameter>(int jdbcType, @NonNull Class<T> javaType, @NonNull P parameter) implements SqlType<T> {
	
	public ParameterizedSqlType {
		Objects.requireNonNull(javaType, "Java type must not be null");
		Objects.requireNonNull(parameter, "Parameter must not be null");
	}
	
	public @NonNull SqlArrayType<T> array() {
		return new SqlArrayType<>(this);
	}
}
