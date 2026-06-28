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
 * Holds the result of rendering an object into sql for a specific dialect.<br>
 * It consists of the rendered sql statements (tokens) and the ordered list of typed parameters that are bound to the statement.<br>
 *
 * @see SqlRenderable
 *
 * @author Luis-St
 */
public final class SqlRendered {
	
	/**
	 * The rendered sql statements as an ordered list of tokens.
	 */
	private final @Unmodifiable List<String> statements;
	/**
	 * The ordered list of parameters, each paired with its sql type.
	 */
	private final @Unmodifiable List<Pair<SqlType<?>, Object>> parameters;
	/**
	 * The cached sql string that is built lazily from the statements on first access.
	 */
	private String cachedSql;
	
	/**
	 * Constructs a new rendered sql with the given statements and parameters.<br>
	 * Both lists are copied defensively into immutable lists.<br>
	 *
	 * @param statements The rendered sql statements as an ordered list of tokens
	 * @param parameters The ordered list of parameters, each paired with its sql type
	 * @throws NullPointerException If the statements or parameters list is null
	 */
	public SqlRendered(@NonNull @Unmodifiable List<String> statements, @NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> parameters) {
		this.statements = List.copyOf(Objects.requireNonNull(statements, "Sql statements must not be null"));
		this.parameters = List.copyOf(Objects.requireNonNull(parameters, "Sql parameters must not be null"));
	}
	
	/**
	 * Creates a rendered sql from the given sql string without any parameters.<br>
	 *
	 * @param sql The sql statement to wrap
	 * @return A rendered sql containing the given statement and no parameters
	 * @throws NullPointerException If the sql is null
	 */
	public static @NonNull SqlRendered of(@NonNull String sql) {
		Objects.requireNonNull(sql, "Sql statement must not be null");
		
		return new SqlRendered(List.of(sql), List.of());
	}
	
	//region Static helper methods
	
	/**
	 * Joins the given sql tokens into a single sql string.<br>
	 * A single space is inserted between two tokens whenever a separator is required between them.<br>
	 *
	 * @param tokens The sql tokens to join
	 * @return The joined sql string
	 */
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
	
	/**
	 * Determines whether a separating space is required between the two given sql tokens.<br>
	 * No separator is added before an opening parenthesis, a closing parenthesis or a comma, and none is added directly after an opening parenthesis.<br>
	 *
	 * @param previous The token that precedes the current token
	 * @param current The token for which the separator is checked
	 * @return True if a separating space is required between the tokens, false otherwise
	 */
	private static boolean needsSeparator(@NonNull String previous, @NonNull String current) {
		if ("(".equals(current) || ")".equals(current) || ",".equals(current)) {
			return false;
		}
		return !"(".equals(previous);
	}
	//endregion
	
	/**
	 * Returns the rendered sql statements as an ordered list of tokens.<br>
	 * @return The unmodifiable list of sql tokens
	 */
	public @NonNull @Unmodifiable List<String> statements() {
		return this.statements;
	}
	
	/**
	 * Returns the ordered list of parameters, each paired with its sql type.<br>
	 * @return The unmodifiable list of typed parameters
	 */
	public @NonNull @Unmodifiable List<Pair<SqlType<?>, Object>> parameters() {
		return this.parameters;
	}
	
	/**
	 * Returns the rendered sql as a single string built from the statements.<br>
	 * The result is computed lazily on first access and cached for subsequent calls.<br>
	 *
	 * @return The joined sql string
	 */
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
		return this.parameters.equals(that.parameters);
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
