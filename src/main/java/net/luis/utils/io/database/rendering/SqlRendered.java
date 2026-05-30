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

public final class SqlRendered {
	
	private final @Unmodifiable List<String> statements;
	private final @Unmodifiable List<Pair<SqlType<?>, Object>> parameters;
	private String cachedSql;
	
	public SqlRendered(@NonNull @Unmodifiable List<String> statements, @NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> parameters) {
		this.statements = List.copyOf(Objects.requireNonNull(statements, "Sql statements must not be null"));
		this.parameters = List.copyOf(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
	}
	
	public static @NonNull SqlRendered of(@NonNull String sql) {
		Objects.requireNonNull(sql, "Sql statement must not be null");
		
		return new SqlRendered(List.of(sql), List.of());
	}
	
	//region Static helper methods
	
	private static @NonNull String joinTokens(@NonNull List<String> tokens) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if (i > 0 && needsSeparator(tokens.get(i - 1), token)) {
				builder.append(' ');
			}
			builder.append(token);
		}
		return builder.toString();
	}
	
	private static boolean needsSeparator(@NonNull String previous, @NonNull String current) {
		if ("(".equals(current) || ")".equals(current) || ",".equals(current)) {
			return false;
		}
		return !"(".equals(previous);
	}
	//endregion
	
	public @NonNull @Unmodifiable List<String> statements() {
		return this.statements;
	}
	
	public @NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> parameters() {
		return this.parameters;
	}
	
	public @NonNull String sql() {
		if (this.cachedSql == null) {
			this.cachedSql = joinTokens(this.statements);
		}
		return this.cachedSql;
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlRendered that)) return false;
		
		if (!this.statements.equals(that.statements)) return false;
		if (!this.parameters.equals(that.parameters)) return false;
		return Objects.equals(this.cachedSql, that.cachedSql);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.statements, this.parameters);
	}
	
	@Override
	public String toString() {
		return "SqlRendered[statements=" + this.statements + ", parameters=" + this.parameters + "]";
	}
	//endregion
}
