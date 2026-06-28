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
 * Renders index ddl into dialect-specific sql.<br>
 * Each {@code renderXxx} method translates an index operation, such as creating, dropping or renaming
 * an {@link SqlIndex index}, into the statements supported by the configured {@link SqlDialect dialect}.<br>
 *
 * @author Luis-St
 */

public class SqlIndexRenderer {
	
	/**
	 * The sql dialect used to render the index statements.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql index renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the index statements
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlIndexRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders a statement that creates the given index.<br>
	 * The rendered statement honors the unique flag, index method and an optional where condition of the index.<br>
	 *
	 * @param index The index to create
	 * @return The rendered create index statement
	 * @throws NullPointerException If the index is null
	 * @throws SqlException If rendering fails
	 * @throws SqlDialectUnsupportedRenderingException If the index method is not supported by the dialect
	 */
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
	
	/**
	 * Renders a statement that drops the index with the given name.<br>
	 *
	 * @param owningTable The table that owns the index, may be null if not required by the dialect
	 * @param index The name of the index to drop
	 * @return The rendered drop index statement
	 * @throws NullPointerException If the index name is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderDropIndex(@Nullable SqlTable<?> owningTable, @NonNull String index) throws SqlException {
		Objects.requireNonNull(index, "Sql index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.dialect.quoteIdentifier(index));
		return renderer.toSql();
	}
	
	/**
	 * Renders a standard statement that drops the named index on its owning table.<br>
	 * This produces a {@code DROP INDEX ... ON ...} statement for dialects that require the owning table.<br>
	 *
	 * @param owningTable The table that owns the index
	 * @param indexName The name of the index to drop
	 * @return The rendered drop index on table statement
	 * @throws NullPointerException If the owning table or index name is null
	 * @throws SqlException If rendering fails
	 */
	protected @NonNull SqlRendered renderStandardDropIndexOnTable(@NonNull SqlTable<?> owningTable, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(owningTable, "Sql index owning table must not be null");
		Objects.requireNonNull(indexName, "Sql index name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.dialect.quoteIdentifier(indexName)).on().literal(this.dialect.quoteIdentifier(owningTable.name()));
		return renderer.toSql();
	}
	
	/**
	 * Renders a statement that renames an index from the source name to the target name.<br>
	 *
	 * @param table The table that owns the index, may be null if not required by the dialect
	 * @param from The current name of the index
	 * @param to The new name of the index
	 * @return The rendered rename index statement
	 * @throws NullPointerException If the source name or target name is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		Objects.requireNonNull(from, "Sql source index name must not be null");
		Objects.requireNonNull(to, "Sql target index name must not be null");
		
		return SqlRenderer.empty().alter().index().literal(this.dialect.quoteIdentifier(from)).literal("RENAME").to().literal(this.dialect.quoteIdentifier(to)).toSql();
	}
}
