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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renders schema-level ddl into dialect-specific sql.<br>
 * Each {@code renderXxx} method translates a schema operation, such as creating or dropping a schema,
 * into the statements supported by the configured {@link SqlDialect dialect}.<br>
 *
 * @author Luis-St
 */

public class SqlSchemaRenderer {
	
	/**
	 * The sql dialect used to render the schema statements.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql schema renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the schema statements
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlSchemaRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders a statement that creates a schema with the given name.<br>
	 * If the if not exists flag is set, an {@code IF NOT EXISTS} clause is included in the statement.<br>
	 *
	 * @param name The name of the schema to create
	 * @param ifNotExists Whether to include an {@code IF NOT EXISTS} clause
	 * @return The rendered create schema statement
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().schema();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		return renderer.literal(this.dialect.quoteIdentifier(name)).toSql();
	}
	
	/**
	 * Renders a statement that drops the schema with the given name.<br>
	 * If the if exists flag is set, an {@code IF EXISTS} clause is included, and if the cascade flag is set,
	 * a {@code CASCADE} clause is appended to the statement.<br>
	 *
	 * @param name The name of the schema to drop
	 * @param ifExists Whether to include an {@code IF EXISTS} clause
	 * @param cascade Whether to append a {@code CASCADE} clause
	 * @return The rendered drop schema statement
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderDropSchema(@NonNull String name, boolean ifExists, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().schema();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.dialect.quoteIdentifier(name));
		
		if (cascade) {
			renderer.cascade();
		}
		return renderer.toSql();
	}
}
