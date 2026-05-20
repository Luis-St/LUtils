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
 *
 * @author Luis-St
 *
 */

public class SqlMigrationOperationRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered renderRenameTable(@NonNull SqlTable<?> fromTable, @NonNull SqlTable<?> toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Sql source table must not be null");
		Objects.requireNonNull(toTable, "Sql target table must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(fromTable.getName())).rename().to().literal(this.dialect.quoteIdentifier(toTable.getName())).toSql();
	}
	
	public @NonNull SqlRendered renderAddColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> type, @NonNull SqlColumnOptions options) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(options, "Sql column options must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.getName())).add().column().literal(this.dialect.quoteIdentifier(column.getName())).literal(this.dialect.getTypeName(type));
		this.appendColumnOptions(renderer, options);
		return renderer.toSql();
	}
	
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
			renderer.references().literal(this.dialect.quoteIdentifier(options.referencesTable().getName()));
		}
		if (options.check() != null) {
			SqlRendered checkRendered = this.dialect.renderCondition(options.check());
			renderer.check().openingBracket().rendered(checkRendered).closingBracket();
		}
	}
	
	public @NonNull SqlRendered renderDropColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.getName())).drop().column().literal(this.dialect.quoteIdentifier(column.getName())).toSql();
	}
	
	public @NonNull SqlRendered renderRenameColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> fromColumn, @NonNull SqlColumn<?, ?> toColumn) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(fromColumn, "Sql source column must not be null");
		Objects.requireNonNull(toColumn, "Sql target column must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.getName())).rename()
			.column().literal(this.dialect.quoteIdentifier(fromColumn.getName())).to()
			.literal(this.dialect.quoteIdentifier(toColumn.getName())).toSql();
	}
	
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.getName())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).unique().openingBracket();
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.getName())));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
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
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.getName())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).foreign().key().openingBracket();
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.getName())));
		renderer.closingBracket().references().literal(this.dialect.quoteIdentifier(referencedTable.getName())).openingBracket();
		SqlRenderingHelper.renderList(renderer, referencedColumns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.getName())));
		renderer.closingBracket();
		renderer.on().delete();
		this.dialect.renderReferentialAction(renderer, onDelete);
		renderer.on().update();
		this.dialect.renderReferentialAction(renderer, onUpdate);
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRendered conditionRendered = this.dialect.renderCondition(condition);
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.getName())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).check().openingBracket().rendered(conditionRendered).closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(table.getName())).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).primary().key().openingBracket();
		
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col.getName())));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderDropConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.getName())).drop().constraint().literal(this.dialect.quoteIdentifier(constraintName)).toSql();
	}
}
