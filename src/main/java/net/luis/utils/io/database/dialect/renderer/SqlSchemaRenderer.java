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
 *
 * @author Luis-St
 *
 */

public class SqlSchemaRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlSchemaRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().schema();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		return renderer.literal(this.dialect.quoteIdentifier(name)).toSql();
	}
	
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
