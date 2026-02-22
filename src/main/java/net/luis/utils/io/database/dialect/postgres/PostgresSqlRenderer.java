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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.renderer.SqlDefaultRenderer;
import org.jspecify.annotations.NonNull;

/**
 * PostgreSQL-specific SQL renderer.<br>
 * Extends {@link SqlDefaultRenderer} with overrides for PostgreSQL-specific syntax
 * such as parameter placeholders, UUID generation, and auto-increment columns.<br>
 *
 * @see SqlDefaultRenderer
 *
 * @author Luis-St
 */
public class PostgresSqlRenderer extends SqlDefaultRenderer {

	/**
	 * Constructs a new PostgreSQL SQL renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect associated with this renderer
	 */
	public PostgresSqlRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Returns a PostgreSQL-specific parameter placeholder using {@code $N} syntax.<br>
	 * PostgreSQL uses positional parameters ({@code $1}, {@code $2}, etc.) instead of {@code ?}.<br>
	 *
	 * @param index The parameter index (1-based)
	 * @return The parameter placeholder in {@code $N} format
	 */
	@Override
	public @NonNull String parameterPlaceholder(int index) {
		return "$" + index;
	}

	/**
	 * Returns the PostgreSQL-specific UUID generation function.<br>
	 *
	 * @return The {@code gen_random_uuid()} function
	 */
	@Override
	public @NonNull String uuidFunction() {
		return "gen_random_uuid()";
	}

	/**
	 * Returns the PostgreSQL-specific auto-increment syntax using {@code SERIAL}.<br>
	 *
	 * @return The {@code GENERATED ALWAYS AS IDENTITY} syntax
	 */
	@Override
	public @NonNull String autoIncrementSyntax() {
		return "GENERATED ALWAYS AS IDENTITY";
	}

	/**
	 * Returns the PostgreSQL-specific current timestamp function.<br>
	 *
	 * @return The {@code NOW()} function
	 */
	@Override
	public @NonNull String nowFunction() {
		return "NOW()";
	}
}
