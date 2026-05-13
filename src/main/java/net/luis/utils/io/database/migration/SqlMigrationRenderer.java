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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

class SqlMigrationRenderer {
	
	private final SqlDialect dialect;
	
	SqlMigrationRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	@NonNull List<SqlRendered> render(@NonNull List<SqlMigrationOperation> operations) throws SqlException {
		List<SqlRendered> results = Lists.newArrayList();
		for (SqlMigrationOperation operation : operations) {
			if (operation instanceof AlterColumnOperation op) {
				results.addAll(this.renderAlterColumn(op));
			} else {
				results.add(this.renderOperation(operation));
			}
		}
		return List.copyOf(results);
	}
	
	private @NonNull SqlRendered renderOperation(@NonNull SqlMigrationOperation operation) throws SqlException {
		return switch (operation) {
			case CreateTableOperation op -> this.renderCreateTable(op);
			case DropTableOperation op -> this.renderDropTable(op);
			case RenameTableOperation op -> this.renderRenameTable(op);
			case AddColumnOperation op -> this.renderAddColumn(op);
			case DropColumnOperation op -> this.renderDropColumn(op);
			case RenameColumnOperation op -> this.renderRenameColumn(op);
			case AlterColumnOperation op -> throw new IllegalStateException("AlterColumnOperation should be handled separately");
			case CreateIndexOperation op -> this.renderCreateIndex(op);
			case DropIndexOperation op -> this.renderDropIndex(op);
			case RenameIndexOperation op -> this.renderRenameIndex(op);
			case AddUniqueConstraintOperation op -> this.renderAddUniqueConstraint(op);
			case AddForeignKeyOperation op -> this.renderAddForeignKey(op);
			case AddCheckConstraintOperation op -> this.renderAddCheckConstraint(op);
			case AddCompositePrimaryKeyOperation op -> this.renderAddCompositePrimaryKey(op);
			case DropConstraintOperation op -> this.renderDropConstraint(op);
			case ExecuteDataOperation ignored -> SqlRendered.of("");
		};
	}
	
	private @NonNull SqlRendered renderCreateTable(@NonNull CreateTableOperation op) throws SqlException {
		StringBuilder sql = new StringBuilder("CREATE TABLE ");
		sql.append(this.dialect.quoteIdentifier(op.table().getName()));
		sql.append(" (");
		
		List<String> columnDefs = Lists.newArrayList();
		List<Pair<SqlType<?>, Object>> parameters = Lists.newArrayList();
		
		for (SqlColumnDefinition colDef : op.columns()) {
			columnDefs.add(this.renderColumnDefinition(colDef, parameters));
		}
		
		if (!op.primaryKeyColumns().isEmpty()) {
			String pkCols = op.primaryKeyColumns().stream()
				.map(col -> this.dialect.quoteIdentifier(col.getName()))
				.collect(Collectors.joining(", "));
			columnDefs.add("PRIMARY KEY (" + pkCols + ")");
		}
		
		sql.append(String.join(", ", columnDefs));
		sql.append(")");
		return new SqlRendered(List.of(sql.toString()), parameters);
	}
	
	private @NonNull String renderColumnDefinition(@NonNull SqlColumnDefinition colDef, @NonNull List<Pair<SqlType<?>, Object>> parameters) throws SqlException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.dialect.quoteIdentifier(colDef.column().getName()));
		sb.append(" ").append(this.dialect.getTypeName(colDef.type()));
		this.appendColumnOptions(sb, colDef.type(), colDef.options(), parameters);
		return sb.toString();
	}
	
	private void appendColumnOptions(@NonNull StringBuilder sb, @NonNull SqlType<?> type, @NonNull SqlColumnOptions options, @NonNull List<Pair<SqlType<?>, Object>> parameters) throws SqlException {
		if (options.notNull()) {
			sb.append(" NOT NULL");
		}
		if (options.unique()) {
			sb.append(" UNIQUE");
		}
		if (options.autoIncrement()) {
			sb.append(" ").append(this.dialect.renderAutoIncrementKeyword());
		}
		if (options.defaultValue().isPresent()) {
			sb.append(" DEFAULT ").append(this.renderValueLiteral(type, options.defaultValue().get()));
		}
		if (options.referencesTable() != null) {
			sb.append(" REFERENCES ").append(this.dialect.quoteIdentifier(options.referencesTable().getName()));
		}
		if (options.check() != null) {
			SqlRendered checkRendered = this.dialect.renderCondition(options.check());
			sb.append(" CHECK (").append(checkRendered.sql()).append(")");
			parameters.addAll(checkRendered.parameters());
		}
	}
	
	private @NonNull SqlRendered renderDropTable(@NonNull DropTableOperation op) {
		String sql = "DROP TABLE " + this.dialect.quoteIdentifier(op.table().getName());
		return SqlRendered.of(sql);
	}
	
	private @NonNull SqlRendered renderRenameTable(@NonNull RenameTableOperation op) {
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.from().getName()) +
			" RENAME TO " + this.dialect.quoteIdentifier(op.to().getName());
		return SqlRendered.of(sql);
	}
	
	private @NonNull SqlRendered renderAddColumn(@NonNull AddColumnOperation op) throws SqlException {
		List<Pair<SqlType<?>, Object>> parameters = Lists.newArrayList();
		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		sb.append(this.dialect.quoteIdentifier(op.column().getOwningTable().getName()));
		sb.append(" ADD COLUMN ");
		sb.append(this.dialect.quoteIdentifier(op.column().getName()));
		sb.append(" ").append(this.dialect.getTypeName(op.type()));
		this.appendColumnOptions(sb, op.type(), op.options(), parameters);
		return new SqlRendered(List.of(sb.toString()), parameters);
	}
	
	private @NonNull SqlRendered renderDropColumn(@NonNull DropColumnOperation op) {
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.column().getOwningTable().getName()) +
			" DROP COLUMN " + this.dialect.quoteIdentifier(op.column().getName());
		return SqlRendered.of(sql);
	}
	
	private @NonNull SqlRendered renderRenameColumn(@NonNull RenameColumnOperation op) {
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.from().getOwningTable().getName()) +
			" RENAME COLUMN " + this.dialect.quoteIdentifier(op.from().getName()) +
			" TO " + this.dialect.quoteIdentifier(op.to().getName());
		return SqlRendered.of(sql);
	}
	
	private @NonNull List<SqlRendered> renderAlterColumn(@NonNull AlterColumnOperation op) throws SqlException {
		String tableName = op.column().getOwningTable().getName();
		String columnName = op.column().getName();
		List<SqlRendered> results = Lists.newArrayList();
		
		for (SqlColumnAlteration alteration : op.alterations()) {
			switch (alteration) {
				case SetTypeAlteration setType -> results.add(this.dialect.renderAlterColumnType(tableName, columnName, setType.type()));
				case SetNullableAlteration setNullable -> results.add(this.dialect.renderAlterColumnNullability(tableName, columnName, op.column().getType(), setNullable.nullable()));
				case SetDefaultAlteration setDefault -> results.add(this.dialect.renderAlterColumnSetDefault(tableName, columnName, this.renderValueLiteral(op.column().getType(), setDefault.value())));
				case DropDefaultAlteration ignored -> results.add(this.dialect.renderAlterColumnDropDefault(tableName, columnName));
			}
		}
		return results;
	}
	
	private @NonNull SqlRendered renderCreateIndex(@NonNull CreateIndexOperation op) throws SqlException {
		return this.dialect.renderCreateIndex(op.index());
	}
	
	private @NonNull SqlRendered renderDropIndex(@NonNull DropIndexOperation op) throws SqlException {
		String tableName = op.tableName() != null ? op.tableName() : "";
		return this.dialect.renderDropIndex(tableName, op.name());
	}
	
	private @NonNull SqlRendered renderRenameIndex(@NonNull RenameIndexOperation op) throws SqlException {
		return this.dialect.renderRenameIndex(op.tableName(), op.from(), op.to());
	}
	
	private @NonNull SqlRendered renderAddUniqueConstraint(@NonNull AddUniqueConstraintOperation op) {
		String cols = op.columns().stream()
			.map(col -> this.dialect.quoteIdentifier(col.getName()))
			.collect(Collectors.joining(", "));
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.table().getName()) +
			" ADD CONSTRAINT " + this.dialect.quoteIdentifier(op.name()) +
			" UNIQUE (" + cols + ")";
		return SqlRendered.of(sql);
	}
	
	private @NonNull SqlRendered renderAddForeignKey(@NonNull AddForeignKeyOperation op) {
		String cols = op.columns().stream()
			.map(col -> this.dialect.quoteIdentifier(col.getName()))
			.collect(Collectors.joining(", "));
		String refCols = op.referencedColumns().stream()
			.map(col -> this.dialect.quoteIdentifier(col.getName()))
			.collect(Collectors.joining(", "));
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.table().getName()) +
			" ADD CONSTRAINT " + this.dialect.quoteIdentifier(op.name()) +
			" FOREIGN KEY (" + cols + ")" +
			" REFERENCES " + this.dialect.quoteIdentifier(op.referencedTable().getName()) + " (" + refCols + ")" +
			" ON DELETE " + this.renderReferentialAction(op.onDelete()) +
			" ON UPDATE " + this.renderReferentialAction(op.onUpdate());
		return SqlRendered.of(sql);
	}
	
	private @NonNull String renderReferentialAction(@NonNull SqlReferentialAction action) {
		return switch (action) {
			case NO_ACTION -> "NO ACTION";
			case RESTRICT -> "RESTRICT";
			case CASCADE -> "CASCADE";
			case SET_NULL -> "SET NULL";
			case SET_DEFAULT -> "SET DEFAULT";
		};
	}
	
	private @NonNull SqlRendered renderAddCheckConstraint(@NonNull AddCheckConstraintOperation op) throws SqlException {
		SqlRendered conditionRendered = this.dialect.renderCondition(op.condition());
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.table().getName()) +
			" ADD CONSTRAINT " + this.dialect.quoteIdentifier(op.name()) +
			" CHECK (" + conditionRendered.sql() + ")";
		return new SqlRendered(List.of(sql), conditionRendered.parameters());
	}
	
	private @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull AddCompositePrimaryKeyOperation op) {
		String cols = op.columns().stream()
			.map(col -> this.dialect.quoteIdentifier(col.getName()))
			.collect(Collectors.joining(", "));
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.table().getName()) +
			" ADD CONSTRAINT " + this.dialect.quoteIdentifier(op.name()) +
			" PRIMARY KEY (" + cols + ")";
		return SqlRendered.of(sql);
	}
	
	private @NonNull String renderValueLiteral(@NonNull SqlType<?> type, @NonNull Object value) {
		if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof Boolean b) {
			return b ? "TRUE" : "FALSE";
		} else {
			return "'" + value.toString().replace("'", "''") + "'";
		}
	}
	
	private @NonNull SqlRendered renderDropConstraint(@NonNull DropConstraintOperation op) {
		String sql = "ALTER TABLE " + this.dialect.quoteIdentifier(op.table().getName()) +
			" DROP CONSTRAINT " + this.dialect.quoteIdentifier(op.name());
		return SqlRendered.of(sql);
	}
}
