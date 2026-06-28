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
 * Renderer that turns table definitions and table-level operations into dialect-specific SQL.<br>
 * It produces {@link SqlRendered} statements such as {@code CREATE TABLE}, {@code DROP TABLE} and {@code TRUNCATE TABLE} for the configured {@link SqlDialect}.<br>
 * Subclasses may override the protected rendering hooks to adapt column, constraint and auto-increment rendering to a specific dialect.<br>
 *
 * @author Luis-St
 */

public class SqlTableRenderer {
	
	/**
	 * The dialect used to render the table statements.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new table renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect used to render the table statements
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlTableRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders a {@code CREATE TABLE} statement for the given table.<br>
	 * The statement includes all columns, audit columns and table-level constraints of the table.<br>
	 *
	 * @param table The table to create
	 * @param ifNotExists Whether an {@code IF NOT EXISTS} clause should be added
	 * @return The rendered statement
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If rendering fails
	 */
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
	
	/**
	 * Renders the statements required to rebuild the given table with a new set of columns.<br>
	 * The default implementation does not support table rebuilds and always throws an exception, subclasses may override it for dialects that require a rebuild strategy.<br>
	 *
	 * @param table The table to rebuild
	 * @param newColumns The new columns the rebuilt table should have
	 * @param extraInlineConstraints The additional inline constraints to add to the rebuilt table
	 * @return The list of rendered statements that perform the rebuild
	 * @throws NullPointerException If the table, the new columns or the extra inline constraints are null
	 * @throws SqlException If rendering fails or the dialect does not support table rebuilds
	 */
	public @NonNull List<SqlRendered> renderTableRebuild(@NonNull SqlTable<?> table, @NonNull List<? extends SqlColumn<?, ?>> newColumns, @NonNull List<SqlRendered> extraInlineConstraints) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("Table rebuild is not supported by dialect " + this.dialect.name());
	}
	
	/**
	 * Renders a {@code DROP TABLE} statement for the given table.<br>
	 *
	 * @param table The table to drop
	 * @param ifExists Whether an {@code IF EXISTS} clause should be added
	 * @return The rendered statement
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If rendering fails
	 */
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
	
	/**
	 * Renders a {@code TRUNCATE TABLE} statement for the given table.<br>
	 *
	 * @param table The table to truncate
	 * @return The rendered statement
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.dialect.quoteIdentifier(table.name()));
		return renderer.toSql();
	}
	
	/**
	 * Renders the keyword sequence used to mark a column as auto-incrementing.<br>
	 *
	 * @return The rendered keyword sequence
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException {
		return SqlRenderer.empty().literal("GENERATED").literal("ALWAYS").as().literal("IDENTITY").toSql();
	}
	
	/**
	 * Renders the column definition of the given column for use inside a {@code CREATE TABLE} statement.<br>
	 * The definition includes the type, nullability, default value, auto-increment, primary key, unique, foreign key and check constraints of the column.<br>
	 *
	 * @param <E> The element type of the column
	 * @param <C> The column value type of the column
	 * @param column The column to render
	 * @param skipPrimaryKey Whether the inline primary key marker should be skipped, used when the table has a composite primary key
	 * @return The rendered column definition
	 * @throws NullPointerException If the column is null
	 * @throws SqlException If rendering fails or the column type is not supported by the dialect
	 */
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
	
	/**
	 * Renders the column definition of the given audit column for use inside a {@code CREATE TABLE} statement.<br>
	 * The definition includes the type and nullability of the audit column.<br>
	 *
	 * @param column The audit column to render
	 * @return The rendered column definition
	 * @throws NullPointerException If the audit column is null
	 * @throws SqlException If rendering fails or the audit column type is not supported by the dialect
	 */
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
	
	/**
	 * Appends the auto-increment marker for the given column to the given renderer.<br>
	 * Subclasses may override this to use a dialect-specific auto-increment syntax.<br>
	 *
	 * @param renderer The renderer to append the auto-increment marker to
	 * @param column The column that should be marked as auto-incrementing
	 * @throws NullPointerException If the renderer or the column is null
	 * @throws SqlException If rendering fails
	 */
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	/**
	 * Appends the referenced table, referenced columns and referential actions of the given foreign key to the given renderer.<br>
	 * The {@code ON UPDATE} and {@code ON DELETE} clauses are only appended when the corresponding referential action is not {@link SqlReferentialAction#NO_ACTION}.<br>
	 *
	 * @param renderer The renderer to append the foreign key to
	 * @param fk The foreign key to render
	 * @throws NullPointerException If the renderer or the foreign key is null
	 * @throws SqlException If rendering fails
	 */
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
	
	/**
	 * Renders the table-level constraints of the given table for use inside a {@code CREATE TABLE} statement.<br>
	 * This includes the composite primary key, foreign keys, unique constraints and check constraints of the table.<br>
	 * The returned rendered SQL is empty if the table has no table-level constraints.<br>
	 *
	 * @param table The table whose constraints should be rendered
	 * @return The rendered constraints, or an empty rendered SQL if there are none
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If rendering fails
	 */
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
