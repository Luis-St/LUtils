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

package net.luis.utils.io.database.type;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface that reads a value from a column of a {@link ResultSet}.<br>
 * It is used to customize how a value of a specific sql type is retrieved from a result set.<br>
 *
 * @see SqlValueBinder
 * @see SqlTypeMapping
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlValueReader {
	
	/**
	 * Reads the value from the column at the given index of the result set.<br>
	 *
	 * @param resultSet The result set to read the value from
	 * @param index The one-based index of the column to read
	 * @return The read value or {@code null} if the column contains a sql null
	 * @throws SQLException If an error occurs while reading the value from the result set
	 */
	@Nullable Object read(@NonNull ResultSet resultSet, int index) throws SQLException;
}
