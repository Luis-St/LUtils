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

package net.luis.utils.io.database.dialect.rendering.mysql;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.rendering.base.SqlMigrationOperationRenderer;
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

public class MySqlMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	public MySqlMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameTable(@NonNull String fromTable, @NonNull String toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Source table name must not be null");
		Objects.requireNonNull(toTable, "Target table name must not be null");
		
		return SqlRenderer.empty().rename().table().literal(this.dialect.quoteIdentifier(fromTable)).to().literal(this.dialect.quoteIdentifier(toTable)).toSql();
	}
}
