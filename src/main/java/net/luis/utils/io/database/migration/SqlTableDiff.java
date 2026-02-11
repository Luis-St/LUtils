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

package net.luis.utils.io.database.migration;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL table diff for migration purposes.<br>
 *
 * @author Luis-St
 */
public interface SqlTableDiff {
	
	/**
	 * Returns the name of the table.<br>
	 * @return The table name
	 */
	@NonNull String tableName();

	/**
	 * Returns the names of columns added to the table.<br>
	 * @return The list of added column names
	 */
	@NonNull List<String> addedColumns();

	/**
	 * Returns the names of columns removed from the table.<br>
	 * @return The list of removed column names
	 */
	@NonNull List<String> removedColumns();

	/**
	 * Returns the names of columns modified in the table.<br>
	 * @return The list of modified column names
	 */
	@NonNull List<String> modifiedColumns();
}
