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

package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlRendered(
	@NonNull @Unmodifiable List<String> statements,
	@NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> parameters
) {
	
	
	public SqlRendered {
		statements = List.copyOf(Objects.requireNonNull(statements, "Sql statements must not be null"));
		parameters = List.copyOf(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
	}
	
	public static @NonNull SqlRendered of(@NonNull String sql) {
		Objects.requireNonNull(sql, "Sql statement must not be null");
		
		return new SqlRendered(List.of(sql), List.of());
	}
	
	public @NonNull String sql() {
		return String.join(" ", this.statements);
	}
}
