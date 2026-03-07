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

package net.luis.utils.io.databasev1.table;

import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL foreign key column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 * @param <R> The type of the referenced entity
 */
public interface SqlForeignColumn<T, R> extends SqlColumn<T> {
	
	/**
	 * Returns the table referenced by this foreign key column.<br>
	 * @return The referenced table
	 */
	@NonNull SqlTable<R> referencedTable();
	
	/**
	 * Returns the primary key column referenced by this foreign key column.<br>
	 * @return The referenced primary key column
	 */
	@NonNull SqlPrimaryKeyColumn<?, ?> referencedColumn();
	
	/**
	 * Returns the action to perform when a referenced row is deleted.<br>
	 * Defaults to {@link SqlForeignKeyAction#NO_ACTION}.<br>
	 *
	 * @return The on-delete action
	 */
	@NonNull SqlForeignKeyAction onDelete();
	
	/**
	 * Returns the action to perform when a referenced row is updated.<br>
	 * Defaults to {@link SqlForeignKeyAction#NO_ACTION}.<br>
	 *
	 * @return The on-update action
	 */
	@NonNull SqlForeignKeyAction onUpdate();
}
