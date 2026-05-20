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
	
	public @NonNull SqlRendered renderRenameTable(@NonNull String fromTable, @NonNull String toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Sql source table name must not be null");
		Objects.requireNonNull(toTable, "Sql target table name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(fromTable)).rename().to().literal(this.dialect.quoteIdentifier(toTable)).toSql();
	}
	
	public @NonNull SqlRendered renderAddColumn(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> type, @NonNull SqlColumnOptions options) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(columnName, "Sql column name must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(options, "Sql column options must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).add().column().literal(this.dialect.quoteIdentifier(columnName)).literal(this.dialect.getTypeName(type));
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
	
	public @NonNull SqlRendered renderDropColumn(@NonNull String tableName, @NonNull String columnName) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(columnName, "Sql column name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).drop().column().literal(this.dialect.quoteIdentifier(columnName)).toSql();
	}
	
	public @NonNull SqlRendered renderRenameColumn(@NonNull String tableName, @NonNull String fromColumn, @NonNull String toColumn) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(fromColumn, "Sql source column name must not be null");
		Objects.requireNonNull(toColumn, "Sql target column name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).rename().column().literal(this.dialect.quoteIdentifier(fromColumn)).to().literal(this.dialect.quoteIdentifier(toColumn)).toSql();
	}
	
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull String tableName, @NonNull String constraintName, @NonNull List<String> columnNames) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columnNames, "Sql column names must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).unique().openingBracket();
		SqlRenderingHelper.renderList(renderer, columnNames, (r, col) -> r.literal(this.dialect.quoteIdentifier(col)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAddForeignKey(
		@NonNull String tableName,
		@NonNull String constraintName,
		@NonNull List<String> columns,
		@NonNull String referencedTable,
		@NonNull List<String> referencedColumns,
		@NonNull SqlReferentialAction onDelete,
		@NonNull SqlReferentialAction onUpdate
	) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(referencedTable, "Sql referenced table name must not be null");
		Objects.requireNonNull(referencedColumns, "Sql referenced columns must not be null");
		Objects.requireNonNull(onDelete, "On sql delete action must not be null");
		Objects.requireNonNull(onUpdate, "On sql update action must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).foreign().key().openingBracket();
		SqlRenderingHelper.renderList(renderer, columns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col)));
		renderer.closingBracket().references().literal(this.dialect.quoteIdentifier(referencedTable)).openingBracket();
		SqlRenderingHelper.renderList(renderer, referencedColumns, (r, col) -> r.literal(this.dialect.quoteIdentifier(col)));
		renderer.closingBracket();
		renderer.on().delete();
		this.dialect.renderReferentialAction(renderer, onDelete);
		renderer.on().update();
		this.dialect.renderReferentialAction(renderer, onUpdate);
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull String tableName, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRendered conditionRendered = this.dialect.renderCondition(condition);
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).check().openingBracket().rendered(conditionRendered).closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull String tableName, @NonNull String constraintName, @NonNull List<String> columnNames) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(columnNames, "Sql column names must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).add().constraint().literal(this.dialect.quoteIdentifier(constraintName)).primary().key().openingBracket();
		
		SqlRenderingHelper.renderList(renderer, columnNames, (r, col) -> r.literal(this.dialect.quoteIdentifier(col)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	public @NonNull SqlRendered renderDropConstraint(@NonNull String tableName, @NonNull String constraintName) throws SqlException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).drop().constraint().literal(this.dialect.quoteIdentifier(constraintName)).toSql();
	}
}
