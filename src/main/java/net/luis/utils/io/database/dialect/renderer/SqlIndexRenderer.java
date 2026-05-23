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
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlIndexRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlIndexRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		renderer.index().literal(this.dialect.quoteIdentifier(index.name()));
		renderer.on().literal(this.dialect.quoteIdentifier(index.columns().getFirst().owningTable().name()));
		
		try {
			renderer.using().literal(this.dialect.getIndexMethodName(index.method()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Sql index method is not supported by dialect " + this.dialect.name(), e);
		}
		
		renderer.openingBracket();
		SqlRenderingHelper.renderList(renderer, index.columns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.name())));
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this.dialect));
		}
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderDropIndex(@Nullable SqlTable<?> owningTable, @NonNull String index) throws SqlException {
		Objects.requireNonNull(index, "Sql index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.dialect.quoteIdentifier(index));
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		Objects.requireNonNull(from, "Sql source index name must not be null");
		Objects.requireNonNull(to, "Sql target index name must not be null");
		
		return SqlRenderer.empty().alter().index().literal(this.dialect.quoteIdentifier(from)).literal("RENAME").to().literal(this.dialect.quoteIdentifier(to)).toSql();
	}
}
