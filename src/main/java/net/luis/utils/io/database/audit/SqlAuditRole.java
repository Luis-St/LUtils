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

package net.luis.utils.io.database.audit;

/**
 * Represents the semantic role of an {@link SqlAuditColumn audit column}.<br>
 * Each role identifies which piece of audit information the column stores.<br>
 *
 * @author Luis-St
 */
public enum SqlAuditRole {
	
	/**
	 * The optimistic-locking version counter of the row.<br>
	 */
	VERSION,
	/**
	 * The timestamp at which the row was created.<br>
	 */
	CREATED_AT,
	/**
	 * The user that created the row.<br>
	 */
	CREATED_BY,
	/**
	 * The timestamp at which the row was last updated.<br>
	 */
	UPDATED_AT,
	/**
	 * The user that last updated the row.<br>
	 */
	UPDATED_BY
}
