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
 * Represents the type of a sql join clause.<br>
 * The type determines which rows of the joined tables are included in the result.<br>
 *
 * @see SqlJoinClause
 *
 * @author Luis-St
 */
public enum SqlJoinType {
	
	/**
	 * An inner join that returns only the rows that have matching values in both tables.<br>
	 */
	INNER,
	/**
	 * A left join that returns all rows from the left table and the matching rows from the right table.<br>
	 */
	LEFT,
	/**
	 * A right join that returns all rows from the right table and the matching rows from the left table.<br>
	 */
	RIGHT,
	/**
	 * A full outer join that returns all rows when there is a match in either the left or the right table.<br>
	 */
	FULL,
	/**
	 * A cross join that returns the cartesian product of the rows of both tables.<br>
	 */
	CROSS
}
