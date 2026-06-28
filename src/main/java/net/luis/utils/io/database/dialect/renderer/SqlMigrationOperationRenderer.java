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

import com.google.common.collect.Lists;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Renders schema migration operations into dialect-specific sql.<br>
 * Each {@code renderXxx} method translates a single migration operation, such as renaming a table,
 * adding or dropping a column or managing constraints, into the {@code ALTER TABLE} statements
 * supported by the configured {@link SqlDialect dialect}.<br>
 *
 * @author Luis-St
 */

public class SqlMigrationOperationRenderer {
	
	/**
	 * The sql dialect used to render the migration operations.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new sql migration operation renderer for the given dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the migration operations
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders a statement that renames a table from the source table name to the target table name.<br>
	 *
	 * @param fromTable The source table to rename
	 * @param toTable The target table providing the new name
	 * @return The rendered rename table statement
	 * @throws NullPointerException If the source table or target table is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderRenameTable(@NonNull SqlTable<?> fromTable, @NonNull SqlTable<?> toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Sql source table must not be null");
		Objects.requireNonNull(toTable, "Sql target table must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(fromTable.name())).rename().to().literal(this.dialect.quoteIdentifier(toTable.name())).toSql();
	}
	
	/**
	 * Renders a statement that adds a column with the given type and options to the table.<br>
	 *
	 * @param table The table to add the column to
	 * @param column The column to add
	 * @param type The sql type of the new column
	 * @param options The column options to apply to the new column
	 * @return The rendered add column statement
	 * @throws NullPointerException If the table, column, type or options is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAddColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type, @NonNull SqlColumnOptions options) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(options, "Sql column options must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().column().literal(this.dialect.quoteIdentifier(column.name())).literal(this.dialect.getTypeName(type));
		this.appendColumnOptions(renderer, options);
		return renderer.toSql();
	}
	
	/**
	 * Appends the column constraints and modifiers described by the given options to the renderer.<br>
	 * Depending on the options this appends the not null, unique, auto increment, default value,
	 * references and check clauses in that order.<br>
	 *
	 * @param renderer The renderer to append the column options to
	 * @param options The column options to render
	 * @throws NullPointerException If the renderer or options is null
	 * @throws SqlException If rendering fails
	 */
	protected void appendColumnOptions(@NonNull SqlRenderer renderer, @NonNull SqlColumnOptions options) throws SqlException {
		if (options.notNull()) {
			renderer.not().null_();
		}
		if (options.unique()) {
			renderer.unique();
		}
		if (options.autoIncrement()) {
			renderer.rendered(this.dialect.tableRenderer().renderAutoIncrementKeyword());
		}
		if (options.defaultValue().isPresent()) {
			renderer.default_().literal(this.dialect.renderValueLiteral(options.defaultValue().get()));
		}
		if (options.referencesTable() != null) {
			renderer.references().literal(this.dialect.quoteIdentifier(options.referencesTable().name()));
		}
		if (options.check() != null) {
			SqlRendered checkRendered = this.dialect.renderCondition(options.check());
			renderer.check().openingBracket().rendered(checkRendered).closingBracket();
		}
	}
	
	/**
	 * Renders a statement that drops the given column from the table.<br>
	 *
	 * @param table The table to drop the column from
	 * @param column The column to drop
	 * @return The rendered drop column statement
	 * @throws NullPointerException If the table or column is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderDropColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.name())).drop().column().literal(this.dialect.quoteIdentifier(column.name())).toSql();
	}
	
	/**
	 * Renders a statement that renames a column of the table from the source column name to the target column name.<br>
	 *
	 * @param table The table that owns the column
	 * @param fromColumn The source column to rename
	 * @param toColumn The target column providing the new name
	 * @return The rendered rename column statement
	 * @throws NullPointerException If the table, source column or target column is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderRenameColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> fromColumn, @NonNull SqlColumn<?, ?> toColumn) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(fromColumn, "Sql source column must not be null");
		Objects.requireNonNull(toColumn, "Sql target column must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.name())).rename()
			.column().literal(this.dialect.quoteIdentifier(fromColumn.name())).to()
			.literal(this.dialect.quoteIdentifier(toColumn.name())).toSql();
	}
	
	/**
	 * Renders a statement that adds a named unique constraint spanning the given columns to the table.<br>
	 *
	 * @param table The table to add the unique constraint to
	 * @param constraintName The name of the unique constraint
	 * @param columns The columns covered by the unique constraint
	 * @return The rendered add unique constraint statement
	 * @throws NullPointerException If the table, constraint name or columns is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).unique().openingBracket();
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders a statement that adds a named foreign key constraint to the table.<br>
	 * The constraint maps the given columns to the referenced columns of the referenced table and applies
	 * the supplied referential actions on delete and on update.<br>
	 *
	 * @param table The table to add the foreign key to
	 * @param constraintName The name of the foreign key constraint
	 * @param columns The local columns that form the foreign key
	 * @param referencedTable The table referenced by the foreign key
	 * @param referencedColumns The referenced columns in the referenced table
	 * @param onDelete The referential action applied on delete
	 * @param onUpdate The referential action applied on update
	 * @return The rendered add foreign key statement
	 * @throws NullPointerException If any argument is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAddForeignKey(
		@NonNull SqlTable<?> table,
		@NonNull String constraintName,
		@NonNull List<SqlColumn<?, ?>> columns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull List<SqlColumn<?, ?>> referencedColumns,
		@NonNull SqlReferentialAction onDelete,
		@NonNull SqlReferentialAction onUpdate
	) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(referencedTable, "Sql referenced table name must not be null");
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		Objects.requireNonNull(onDelete, "On sql delete action must not be null");
		Objects.requireNonNull(onUpdate, "On sql update action must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).foreign().key().openingBracket();
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
		renderer.closingBracket().references().literal(this.dialect.quoteIdentifier(referencedTable.name())).openingBracket();
		SqlRenderingHelper.renderList(renderer, referencedColumns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
		renderer.closingBracket();
		renderer.on().delete();
		this.dialect.renderReferentialAction(renderer, onDelete);
		renderer.on().update();
		this.dialect.renderReferentialAction(renderer, onUpdate);
		return renderer.toSql();
	}
	
	/**
	 * Renders a statement that adds a named check constraint based on the given condition to the table.<br>
	 *
	 * @param table The table to add the check constraint to
	 * @param constraintName The name of the check constraint
	 * @param condition The condition that the check constraint enforces
	 * @return The rendered add check constraint statement
	 * @throws NullPointerException If the table, constraint name or condition is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRendered conditionRendered = this.dialect.renderCondition(condition);
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).check().openingBracket().rendered(conditionRendered).closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders a statement that adds a named composite primary key spanning the given columns to the table.<br>
	 *
	 * @param table The table to add the composite primary key to
	 * @param constraintName The name of the primary key constraint
	 * @param columns The columns that form the composite primary key
	 * @return The rendered add composite primary key statement
	 * @throws NullPointerException If the table, constraint name or columns is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).primary().key().openingBracket();
		
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	/**
	 * Renders the statements that enable auditing for the table according to the given audit config.<br>
	 * One add column statement is rendered for each audit column, where version columns additionally receive
	 * a default value and a not null constraint.<br>
	 *
	 * @param table The table to enable auditing for
	 * @param config The audit config describing the audit columns
	 * @return The rendered statements that add the audit columns
	 * @throws NullPointerException If the table or config is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull List<SqlRendered> renderEnableAuditing(@NonNull SqlTable<?> table, @NonNull SqlAuditConfig config) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(config, "Sql audit config must not be null");
		
		List<SqlRendered> results = Lists.newArrayList();
		for (SqlAuditColumn column : config.auditColumns()) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.alter().table().literal(this.dialect.quoteIdentifier(table.name())).add().column().literal(this.dialect.quoteIdentifier(column.name())).literal(this.dialect.getTypeName(column.type()));
			
			if (column.role() == SqlAuditRole.VERSION) {
				renderer.default_().literal("0").not().null_();
			}
			results.add(renderer.toSql());
		}
		return List.copyOf(results);
	}
	
	/**
	 * Renders the statements that disable auditing for the table according to the given audit config.<br>
	 * One drop column statement is rendered for each audit column described by the config.<br>
	 *
	 * @param table The table to disable auditing for
	 * @param config The audit config describing the audit columns
	 * @return The rendered statements that drop the audit columns
	 * @throws NullPointerException If the table or config is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull List<SqlRendered> renderDisableAuditing(@NonNull SqlTable<?> table, @NonNull SqlAuditConfig config) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(config, "Sql audit config must not be null");
		
		List<SqlRendered> results = Lists.newArrayList();
		for (SqlAuditColumn column : config.auditColumns()) {
			results.add(SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.name())).drop().column().literal(this.dialect.quoteIdentifier(column.name())).toSql());
		}
		return List.copyOf(results);
	}
	
	/**
	 * Renders a statement that drops the named constraint from the table.<br>
	 *
	 * @param table The table to drop the constraint from
	 * @param constraintName The name of the constraint to drop
	 * @return The rendered drop constraint statement
	 * @throws NullPointerException If the table or constraint name is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderDropConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.name())).drop().constraint().literal(this.dialect.quoteIdentifier(constraintName)).toSql();
	}
}
