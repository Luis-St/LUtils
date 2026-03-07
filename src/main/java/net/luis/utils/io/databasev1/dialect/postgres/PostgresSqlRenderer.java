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

package net.luis.utils.io.databasev1.dialect.postgres;

import net.luis.utils.io.databasev1.dialect.SqlDialect;
import net.luis.utils.io.databasev1.renderer.SqlDefaultRenderer;
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
	
	@Override
	public @NonNull String uuidFunction() {
		return "gen_random_uuid()";
	}
	
	@Override
	public @NonNull String nowFunction() {
		return "NOW()";
	}
}
