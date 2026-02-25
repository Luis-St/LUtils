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

package net.luis.utils.io.database.mapping;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface for mapping SQL result set rows to record instances.<br>
 * <p>
 *     Implementations define how column values from a query result are
 *     transformed into instances of a specific record type.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The record type to map to
 */
public interface SqlRecordMapper<T> {
	
	/**
	 * Maps a result row to a record instance.<br>
	 *
	 * @param row The result row providing typed column access
	 * @return The mapped record instance
	 */
	@NonNull T map(@NonNull SqlResultRow row);
	
	/**
	 * Returns the column names expected by this mapper.<br>
	 * @return The list of column names
	 */
	@NonNull List<String> columnNames();
	
	/**
	 * Returns the record type this mapper produces.<br>
	 * @return The record class
	 */
	@NonNull Class<T> recordType();
}
