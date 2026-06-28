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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Functional interface that binds a value to a parameter of a {@link PreparedStatement}.<br>
 * It is used to customize how a value of a specific sql type is written to a statement.<br>
 *
 * @see SqlValueReader
 * @see SqlTypeMapping
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlValueBinder {
	
	/**
	 * Binds the given value to the parameter at the given index of the prepared statement.<br>
	 *
	 * @param statement The prepared statement to bind the value to
	 * @param index The one-based index of the parameter to bind
	 * @param value The value to bind or {@code null} to bind a sql null
	 * @throws SQLException If an error occurs while binding the value to the statement
	 */
	void bind(@NonNull PreparedStatement statement, int index, @Nullable Object value) throws SQLException;
}
