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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.renderer.SqlMigrationOperationRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

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
			case SqlExecuteDataOperation _ -> SqlRendered.of("");
		};
	}
	
	private @NonNull List<SqlRendered> renderAlterColumn(@NonNull SqlAlterColumnOperation op) throws SqlException {
		Objects.requireNonNull(op, "Alter column operation must not be null");
		
		SqlColumn<?, ?> column = op.column();
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
}
