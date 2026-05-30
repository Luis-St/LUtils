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
import net.luis.utils.io.database.audit.SqlAuditColumn;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
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
		
		renderer.literal(this.dialect.quoteIdentifier(table.name()));
		renderer.openingBracket();
		
		boolean hasCompositeKey = table.compositePrimaryKey().isPresent();
		List<? extends SqlColumn<?, ?>> columns = table.columns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(this.renderColumnForTable(columns.get(i), hasCompositeKey));
		}
		
		if (table.auditConfig().isPresent()) {
			for (SqlAuditColumn auditColumn : table.auditConfig().get().auditColumns()) {
				renderer.comma().rendered(this.renderAuditColumnForTable(auditColumn));
			}
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			renderer.comma().rendered(tableConstraints);
		}
		
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull List<SqlRendered> renderTableRebuild(@NonNull SqlTable<?> table, @NonNull List<? extends SqlColumn<?, ?>> newColumns, @NonNull List<SqlRendered> extraInlineConstraints) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("Table rebuild is not supported by dialect " + this.dialect.name());
	}
	
	public @NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().table();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.dialect.quoteIdentifier(table.name()));
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.dialect.quoteIdentifier(table.name()));
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException {
		return SqlRenderer.empty().literal("GENERATED").literal("ALWAYS").as().literal("IDENTITY").toSql();
	}
	
	protected <E, C> @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<E, C> column, boolean skipPrimaryKey) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(this.dialect.quoteIdentifier(column.name()));
		
		try {
			renderer.literal(this.dialect.getTypeName(column.type()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Column type is not supported by dialect " + this.dialect.name(), e);
		}
		
		if (!column.nullable()) {
			renderer.not().null_();
		}
		if (column.defaultValue().isPresent()) {
			renderer.default_().literal(this.dialect.renderValueLiteral(column.defaultValue().get()));
		}
		if (column.autoIncrement()) {
			this.renderAutoIncrement(renderer, column);
		}
		if (column.primaryKey() && !skipPrimaryKey) {
			renderer.primary().key();
		}
		if (column.unique()) {
			renderer.unique();
		}
		
		if (column.foreignKey().isPresent()) {
			renderer.references();
			this.renderForeignKey(renderer, column.foreignKey().get());
		}
		
		for (SqlCondition check : column.checks()) {
			renderer.check().openingBracket().rendered(check.toSql(this.dialect)).closingBracket();
		}
		return renderer.toSql();
	}
	
	protected @NonNull SqlRendered renderAuditColumnForTable(@NonNull SqlAuditColumn column) throws SqlException {
		Objects.requireNonNull(column, "Sql audit column must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(this.dialect.quoteIdentifier(column.name()));
		
		try {
			renderer.literal(this.dialect.getTypeName(column.type()));
		} catch (SqlDialectUnsupportedRenderingException e) {
			throw new SqlDialectUnsupportedRenderingException("Audit column type is not supported by dialect " + this.dialect.name(), e);
		}
		
		if (!column.nullable()) {
			renderer.not().null_();
		}
		return renderer.toSql();
	}
	
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	protected void renderForeignKey(@NonNull SqlRenderer renderer, @NonNull SqlForeignKey<?> fk) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(fk, "Sql foreign key must not be null");
		
		renderer.literal(this.dialect.quoteIdentifier(fk.referencedTable().name()));
		renderer.openingBracket();
		
		List<? extends SqlColumn<?, ?>> referencedColumns = fk.referencedColumns();
		for (int i = 0; i < referencedColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.dialect.quoteIdentifier(referencedColumns.get(i).name()));
		}
		renderer.closingBracket();
		
		if (fk.onUpdate() != SqlReferentialAction.NO_ACTION) {
			renderer.on().update();
			this.dialect.renderReferentialAction(renderer, fk.onUpdate());
		}
		
		if (fk.onDelete() != SqlReferentialAction.NO_ACTION) {
			renderer.on().delete();
			this.dialect.renderReferentialAction(renderer, fk.onDelete());
		}
	}
	
	protected @NonNull SqlRendered renderTableConstraints(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		boolean first = true;
		
		if (table.compositePrimaryKey().isPresent()) {
			SqlCompositePrimaryKey<?> pk = table.compositePrimaryKey().get();
			
			renderer.primary().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, pk.columns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.name())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlTableForeignKey<?, ?> tableFk : table.foreignKeys()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.foreign().key().openingBracket();
			SqlRenderingHelper.renderList(renderer, tableFk.getReferencingColumns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.name())));
			renderer.closingBracket().references();
			this.renderForeignKey(renderer, tableFk.getForeignKey());
			
			first = false;
		}
		
		for (SqlUniqueConstraint<?> uniqueConstraint : table.uniqueConstraints()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.unique().openingBracket();
			SqlRenderingHelper.renderList(renderer, uniqueConstraint.columns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.name())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlCondition check : table.checkConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.check().openingBracket().rendered(check.toSql(this.dialect)).closingBracket();
			first = false;
		}
		
		return renderer.toSql();
	}
}
