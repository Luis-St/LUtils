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

package net.luis.utils.io.database;

/**
 * Represents the referential action applied to the rows of a child table when a referenced parent row is updated or deleted.<br>
 * The action defines how a foreign key constraint reacts to changes of the referenced row.<br>
 *
 * @author Luis-St
 */
public enum SqlReferentialAction {
	
	/**
	 * Performs no referential action, deferring the constraint check to the end of the statement.<br>
	 */
	NO_ACTION,
	/**
	 * Rejects the update or delete of the referenced row if any child row still references it.<br>
	 */
	RESTRICT,
	/**
	 * Propagates the update or delete to the referencing child rows.<br>
	 */
	CASCADE,
	/**
	 * Sets the referencing columns of the child rows to {@code null}.<br>
	 */
	SET_NULL,
	/**
	 * Sets the referencing columns of the child rows to their default value.<br>
	 */
	SET_DEFAULT
}
