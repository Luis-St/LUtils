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

package net.luis.utils.io.database.migration.operation;

/**
 * A sealed interface representing a single schema-migration DDL operation.<br>
 * Each permitted record models one concrete operation such as creating, dropping or renaming a table, column, index or constraint.<br>
 *
 * @author Luis-St
 */
public sealed interface SqlMigrationOperation permits
	SqlCreateTableOperation,
	SqlDropTableOperation,
	SqlRenameTableOperation,
	SqlAddColumnOperation,
	SqlDropColumnOperation,
	SqlRenameColumnOperation,
	SqlAlterColumnOperation,
	SqlCreateIndexOperation,
	SqlDropIndexOperation,
	SqlRenameIndexOperation,
	SqlAddUniqueConstraintOperation,
	SqlAddForeignKeyOperation,
	SqlAddCheckConstraintOperation,
	SqlAddCompositePrimaryKeyOperation,
	SqlDropConstraintOperation,
	SqlEnableAuditingOperation,
	SqlDisableAuditingOperation,
	SqlExecuteDataOperation {}
