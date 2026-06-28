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

package net.luis.utils.io.database.query.util;

/**
 * Represents the type of a sql set clause used in update statements.<br>
 * The type determines how the value of the assigned column is computed.<br>
 *
 * @see SqlSetClause
 *
 * @author Luis-St
 */
public enum SqlSetType {
	
	/**
	 * Assigns the result of an expression to the column.<br>
	 */
	EXPRESSION,
	/**
	 * Increments the current value of the column by the result of an expression.<br>
	 */
	INCREMENT,
	/**
	 * Decrements the current value of the column by the result of an expression.<br>
	 */
	DECREMENT,
	/**
	 * Assigns {@code null} to the column.<br>
	 */
	NULL
}
