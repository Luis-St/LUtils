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

package net.luis.utils.io.database.index;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing SQL index information.<br>
 *
 * @author Luis-St
 */
public interface SqlIndexInfo {
	
	/**
	 * Returns the name of the index.<br>
	 * @return The index name
	 */
	@NonNull String name();

	/**
	 * Returns the name of the table the index belongs to.<br>
	 * @return The table name
	 */
	@NonNull String tableName();

	/**
	 * Returns the column names included in the index.<br>
	 * @return The list of column names
	 */
	@NonNull List<String> columns();

	/**
	 * Returns whether the index enforces uniqueness.<br>
	 * @return Whether the index is unique
	 */
	boolean isUnique();

	/**
	 * Returns the index method used.<br>
	 * @return The index method
	 */
	@NonNull SqlIndexMethod method();
}
