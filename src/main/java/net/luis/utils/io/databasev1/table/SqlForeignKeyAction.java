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

/**
 * Enum representing the referential actions for foreign key constraints.<br>
 * Used to define the behavior when a referenced row is updated or deleted.<br>
 *
 * @author Luis-St
 */
public enum SqlForeignKeyAction {
	
	/**
	 * Prevents the operation if it would violate the foreign key constraint.<br>
	 * Generates SQL: {@code RESTRICT}.<br>
	 */
	RESTRICT,
	
	/**
	 * Cascades the operation to all referencing rows.<br>
	 * Generates SQL: {@code CASCADE}.<br>
	 */
	CASCADE,
	
	/**
	 * Sets the referencing column to {@code NULL} when the referenced row is deleted or updated.<br>
	 * Generates SQL: {@code SET NULL}.<br>
	 */
	SET_NULL,
	
	/**
	 * Sets the referencing column to its default value when the referenced row is deleted or updated.<br>
	 * Generates SQL: {@code SET DEFAULT}.<br>
	 */
	SET_DEFAULT,
	
	/**
	 * No action is taken; the constraint is checked at the end of the transaction.<br>
	 * Generates SQL: {@code NO ACTION}.<br>
	 */
	NO_ACTION
}

