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

package net.luis.utils.io.database.migration;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.dialect.renderer.SqlMigrationOperationRenderer;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

class SqlMigrationRenderer {
	
	private final SqlDialect dialect;
	private final SqlMigrationOperationRenderer migrationRenderer;
	
	SqlMigrationRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.migrationRenderer = dialect.migrationRenderer();
	}
	
	@NonNull List<SqlRendered> render(@NonNull List<SqlMigrationOperation> operations) throws SqlException {
		List<SqlRendered> results = Lists.newArrayList();
		for (SqlMigrationOperation operation : operations) {
			if (operation instanceof SqlAlterColumnOperation op) {
				results.addAll(this.renderAlterColumn(op));
			} else if (operation instanceof SqlEnableAuditingOperation op) {
				results.addAll(this.migrationRenderer.renderEnableAuditing(op.table(), op.config()));
			} else if (operation instanceof SqlDisableAuditingOperation op) {
				results.addAll(this.migrationRenderer.renderDisableAuditing(op.table(), op.config()));
			} else if (this.dialect.isFeatureSupported(SqlFeature.TABLE_REBUILD) && this.isRebuiltConstraint(operation)) {
				results.addAll(this.renderAddConstraintViaRebuild(operation));
			} else {
				results.add(this.renderOperation(operation));
			}
		}
		return List.copyOf(results);
	}
	
	private @NonNull SqlRendered renderOperation(@NonNull SqlMigrationOperation operation) throws SqlException {
		Objects.requireNonNull(operation, "Sql migration operation must not be null");
		
		return switch (operation) {
			case SqlCreateTableOperation op -> this.dialect.tableRenderer().renderCreateTable(op.table(), false);
			case SqlDropTableOperation op -> this.dialect.tableRenderer().renderDropTable(op.table(), false);
			case SqlRenameTableOperation op -> this.migrationRenderer.renderRenameTable(op.from(), op.to());
			case SqlAddColumnOperation op -> this.migrationRenderer.renderAddColumn(op.column().owningTable(), op.column(), op.type(), op.options());
			case SqlDropColumnOperation op -> this.migrationRenderer.renderDropColumn(op.column().owningTable(), op.column());
			case SqlRenameColumnOperation op -> this.migrationRenderer.renderRenameColumn(op.from().owningTable(), op.from(), op.to());
			case SqlAlterColumnOperation _ -> throw new IllegalStateException("SqlAlterColumnOperation should be handled separately");
			case SqlCreateIndexOperation op -> this.dialect.indexRenderer().renderCreateIndex(op.index());
			case SqlDropIndexOperation op -> this.dialect.indexRenderer().renderDropIndex(op.table() != null ? op.table() : null, op.index());
			case SqlRenameIndexOperation op -> this.dialect.indexRenderer().renderRenameIndex(op.table(), op.from(), op.to());
			case SqlAddUniqueConstraintOperation op -> this.migrationRenderer.renderAddUniqueConstraint(op.table(), op.name(), op.columns());
			case SqlAddForeignKeyOperation op -> this.migrationRenderer.renderAddForeignKey(op.table(), op.name(), op.columns(), op.referencedTable(), op.referencedColumns(), op.onDelete(), op.onUpdate());
			case SqlAddCheckConstraintOperation op -> this.migrationRenderer.renderAddCheckConstraint(op.table(), op.name(), op.condition());
			case SqlAddCompositePrimaryKeyOperation op -> this.migrationRenderer.renderAddCompositePrimaryKey(op.table(), op.name(), op.columns());
			case SqlDropConstraintOperation op -> this.migrationRenderer.renderDropConstraint(op.table(), op.name());
			case SqlEnableAuditingOperation _ -> throw new IllegalStateException("SqlEnableAuditingOperation should be handled separately");
			case SqlDisableAuditingOperation _ -> throw new IllegalStateException("SqlDisableAuditingOperation should be handled separately");
			case SqlExecuteDataOperation _ -> SqlRendered.of("");
		};
	}
	
	private @NonNull List<SqlRendered> renderAlterColumn(@NonNull SqlAlterColumnOperation op) throws SqlException {
		Objects.requireNonNull(op, "Alter column operation must not be null");
		
		SqlColumn<?, ?> column = op.column();
		if (this.dialect.isFeatureSupported(SqlFeature.TABLE_REBUILD)) {
			return this.renderAlterColumnViaRebuild(column, op.alterations());
		}
		
		List<SqlRendered> results = Lists.newArrayList();
		for (SqlColumnAlteration alteration : op.alterations()) {
			switch (alteration) {
				case SqlSetTypeAlteration setType -> results.add(this.dialect.columnRenderer().renderAlterColumnType(column, setType.type()));
				case SqlSetNullableAlteration setNullable -> results.add(this.dialect.columnRenderer().renderAlterColumnNullability(column, setNullable.nullable()));
				case SqlSetDefaultAlteration setDefault -> results.add(this.dialect.columnRenderer().renderAlterColumnSetDefault(column, this.dialect.renderValueLiteral(setDefault.value())));
				case SqlDropDefaultAlteration _ -> results.add(this.dialect.columnRenderer().renderAlterColumnDropDefault(column));
			}
		}
		return results;
	}
	
	private @NonNull List<SqlRendered> renderAlterColumnViaRebuild(@NonNull SqlColumn<?, ?> column, @NonNull List<SqlColumnAlteration> alterations) throws SqlException {
		SqlColumn<?, ?> altered = column;
		for (SqlColumnAlteration alteration : alterations) {
			altered = applyAlteration(altered, alteration);
		}
		
		SqlTable<?> table = column.owningTable();
		List<SqlColumn<?, ?>> newColumns = new ArrayList<>();
		for (SqlColumn<?, ?> existing : table.columns()) {
			newColumns.add(existing.name().equals(column.name()) ? altered : existing);
		}
		return this.dialect.tableRenderer().renderTableRebuild(table, newColumns, List.of());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static @NonNull SqlColumn<?, ?> applyAlteration(@NonNull SqlColumn<?, ?> column, @NonNull SqlColumnAlteration alteration) {
		SqlType<?> type = column.type();
		boolean nullable = column.nullable();
		Optional<?> defaultValue = column.defaultValue();
		switch (alteration) {
			case SqlSetTypeAlteration setType -> type = setType.type();
			case SqlSetNullableAlteration setNullable -> nullable = setNullable.nullable();
			case SqlSetDefaultAlteration setDefault -> defaultValue = Optional.of(setDefault.value());
			case SqlDropDefaultAlteration _ -> defaultValue = Optional.empty();
		}
		return new SqlColumn(column.owningTable(), column.name(), column.index(), type, column.getter(), nullable, defaultValue, column.autoIncrement(), column.unique(), column.primaryKey(), column.foreignKey(), column.checks());
	}
	
	private boolean isRebuiltConstraint(@NonNull SqlMigrationOperation operation) {
		return operation instanceof SqlAddUniqueConstraintOperation || operation instanceof SqlAddForeignKeyOperation || operation instanceof SqlAddCheckConstraintOperation || operation instanceof SqlAddCompositePrimaryKeyOperation;
	}
	
	private @NonNull List<SqlRendered> renderAddConstraintViaRebuild(@NonNull SqlMigrationOperation operation) throws SqlException {
		SqlTable<?> table = switch (operation) {
			case SqlAddUniqueConstraintOperation op -> op.table();
			case SqlAddForeignKeyOperation op -> op.table();
			case SqlAddCheckConstraintOperation op -> op.table();
			case SqlAddCompositePrimaryKeyOperation op -> op.table();
			default -> throw new IllegalStateException("Operation is not a rebuilt constraint: " + operation.getClass().getName());
		};
		
		SqlRendered constraint = this.renderInlineConstraint(operation);
		return this.dialect.tableRenderer().renderTableRebuild(table, List.copyOf(table.columns()), List.of(constraint));
	}
	
	private @NonNull SqlRendered renderInlineConstraint(@NonNull SqlMigrationOperation operation) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		switch (operation) {
			case SqlAddUniqueConstraintOperation op -> {
				renderer.unique().openingBracket();
				SqlRenderingHelper.renderList(renderer, op.columns(), (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
				renderer.closingBracket();
			}
			case SqlAddCompositePrimaryKeyOperation op -> {
				renderer.primary().key().openingBracket();
				SqlRenderingHelper.renderList(renderer, op.columns(), (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
				renderer.closingBracket();
			}
			case SqlAddCheckConstraintOperation op -> renderer.check().openingBracket().rendered(this.dialect.renderCondition(op.condition())).closingBracket();
			case SqlAddForeignKeyOperation op -> {
				renderer.foreign().key().openingBracket();
				SqlRenderingHelper.renderList(renderer, op.columns(), (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
				renderer.closingBracket().references().literal(this.dialect.quoteIdentifier(op.referencedTable().name())).openingBracket();
				SqlRenderingHelper.renderList(renderer, op.referencedColumns(), (r, col) -> r.literal(this.dialect.quoteIdentifier(col.name())));
				renderer.closingBracket();
				if (op.onDelete() != SqlReferentialAction.NO_ACTION) {
					renderer.on().delete();
					this.dialect.renderReferentialAction(renderer, op.onDelete());
				}
				if (op.onUpdate() != SqlReferentialAction.NO_ACTION) {
					renderer.on().update();
					this.dialect.renderReferentialAction(renderer, op.onUpdate());
				}
			}
			default -> throw new IllegalStateException("Operation is not a rebuilt constraint: " + operation.getClass().getName());
		}
		return renderer.toSql();
	}
}
