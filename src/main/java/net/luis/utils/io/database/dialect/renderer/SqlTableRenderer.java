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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.*;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTableRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlTableRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().table();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		
		renderer.literal(this.dialect.quoteIdentifier(table.getName()));
		renderer.openingBracket();
		
		boolean hasCompositeKey = table.getCompositePrimaryKey().isPresent();
		List<? extends SqlColumn<?, ?>> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(this.renderColumnForTable(columns.get(i), hasCompositeKey));
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			renderer.comma().rendered(tableConstraints);
		}
		
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().table();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.dialect.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.dialect.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException {
		return SqlRenderer.empty().literal("GENERATED").literal("ALWAYS").as().literal("IDENTITY").toSql();
	}
	
	protected <E, C> @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<E, C> column, boolean skipPrimaryKey) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(this.dialect.quoteIdentifier(column.getName()));
		
		try {
			renderer.literal(this.dialect.getTypeName(column.getType()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Column type is not supported by dialect " + this.dialect.name(), e);
		}
		
		if (!column.isNullable()) {
			renderer.not().null_();
		}
		if (column.getDefaultValue().isPresent()) {
			renderer.default_().literal(this.dialect.renderValueLiteral(column.getDefaultValue().get()));
		}
		if (column.isAutoIncrement()) {
			this.renderAutoIncrement(renderer, column);
		}
		if (column.isPrimaryKey() && !skipPrimaryKey) {
			renderer.primary().key();
		}
		if (column.isUnique()) {
			renderer.unique();
		}
		
		if (column.getForeignKey().isPresent()) {
			renderer.references();
			this.renderForeignKey(renderer, column.getForeignKey().get());
		}
		
		for (SqlCondition check : column.getChecks()) {
			renderer.check().openingBracket().rendered(check.toSql(this.dialect)).closingBracket();
		}
		return renderer.toSql();
	}
	
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	protected void renderForeignKey(@NonNull SqlRenderer renderer, @NonNull SqlForeignKey<?, ?> fk) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(fk, "Sql foreign key must not be null");
		
		renderer.literal(this.dialect.quoteIdentifier(fk.getReferencedTable().getName()));
		renderer.openingBracket();
		
		List<? extends SqlColumn<?, ?>> referencedColumns = fk.getReferencedColumns();
		for (int i = 0; i < referencedColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.dialect.quoteIdentifier(referencedColumns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (fk.getOnUpdate() != SqlReferentialAction.NO_ACTION) {
			renderer.on().update();
			this.dialect.renderReferentialAction(renderer, fk.getOnUpdate());
		}
		
		if (fk.getOnDelete() != SqlReferentialAction.NO_ACTION) {
			renderer.on().delete();
			this.dialect.renderReferentialAction(renderer, fk.getOnDelete());
		}
	}
	
	protected @NonNull SqlRendered renderTableConstraints(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		boolean first = true;
		
		if (table.getCompositePrimaryKey().isPresent()) {
			SqlCompositePrimaryKey<?> pk = table.getCompositePrimaryKey().get();
			
			renderer.primary().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, pk.columns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlForeignKey<?, ?> fk : table.getForeignKeys()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.foreign().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, fk.getReferencingColumns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.getName())));
			renderer.closingBracket().references();
			this.renderForeignKey(renderer, fk);
			
			first = false;
		}
		
		for (List<? extends SqlColumn<?, ?>> uniqueColumns : table.getUniqueConstraints()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.unique().openingBracket();
			SqlRenderingHelper.renderList(renderer, uniqueColumns, (r, column) -> r.literal(this.dialect.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlCondition check : table.getCheckConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.check().openingBracket().rendered(check.toSql(this.dialect)).closingBracket();
			first = false;
		}
		
		return renderer.toSql();
	}
}
