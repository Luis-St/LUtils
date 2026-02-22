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
 * Interface representing a SQL schema diff.<br>
 * <p>
 *     Diffs are used by the migration builder to determine what changes need to be applied.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlSchemaDiff {

	/**
	 * Computes the diff between two schemas.<br>
	 *
	 * @param current The current schema
	 * @param target The target schema
	 * @return The schema diff
	 */
	static @NonNull SqlSchemaDiff between(@NonNull SqlSchema current, @NonNull SqlSchema target) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the tables that were added in the target schema.<br>
	 * @return The list of added table diffs
	 */
	@NonNull List<SqlTableDiff> getAddedTables();

	/**
	 * Returns the tables that were removed from the current schema.<br>
	 * @return The list of removed table diffs
	 */
	@NonNull List<SqlTableDiff> getRemovedTables();

	/**
	 * Returns the tables that were modified between schemas.<br>
	 * @return The list of modified table diffs
	 */
	@NonNull List<SqlTableDiff> getModifiedTables();
}
