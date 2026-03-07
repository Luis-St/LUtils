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

package net.luis.utils.io.databasev1.condition;

import net.luis.utils.io.databasev1.SqlRenderable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL orderable element.<br>
 *
 * @author Luis-St
 */
public interface SqlOrderable extends SqlRenderable {
	
	/**
	 * Sets the sort order to ascend.<br>
	 * Generates SQL: {@code ASC}.<br>
	 *
	 * @return This orderable with ascending order
	 */
	@NonNull SqlOrderable asc();
	
	/**
	 * Sets the sort order to descend.<br>
	 * Generates SQL: {@code DESC}.<br>
	 *
	 * @return This orderable with descending order
	 */
	@NonNull SqlOrderable desc();
	
	/**
	 * Sets null values to appear first in the sort order.<br>
	 * Generates SQL: {@code NULLS FIRST}.<br>
	 *
	 * @return This orderable with nulls first
	 */
	@NonNull SqlOrderable nullsFirst();
	
	/**
	 * Sets null values to appear last in the sort order.<br>
	 * Generates SQL: {@code NULLS LAST}.<br>
	 *
	 * @return This orderable with nulls last
	 */
	@NonNull SqlOrderable nullsLast();
	
}
