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

package net.luis.utils.io.database.query;

/**
 * Represents a set operation that can be applied to combine the results of two sql queries.<br>
 * It controls how the rows of the combined queries are merged.<br>
 *
 * @author Luis-St
 */
public enum SqlSetOperation {
	
	/**
	 * Combines the results of both queries and removes duplicate rows.<br>
	 */
	UNION,
	/**
	 * Combines the results of both queries and keeps duplicate rows.<br>
	 */
	UNION_ALL,
	/**
	 * Returns only the rows that are present in the results of both queries.<br>
	 */
	INTERSECT,
	/**
	 * Returns only the rows that are present in the result of the first query but not in the result of the second query.<br>
	 */
	EXCEPT
}
