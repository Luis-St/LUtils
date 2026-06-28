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

package net.luis.utils.io.database;

import net.luis.utils.io.database.exception.SqlException;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;

/**
 * Represents a handle to a borrowed {@link Connection} obtained from a {@link SqlConnectionSource}.<br>
 * Closing the handle releases the underlying connection back to its source, for example returning it to a connection pool.<br>
 *
 * @see SqlConnectionSource
 *
 * @author Luis-St
 */
public interface SqlConnectionHandle extends AutoCloseable {
	
	/**
	 * Returns the underlying jdbc connection held by this handle.<br>
	 * @return The underlying connection
	 */
	@NonNull Connection connection();
	
	/**
	 * Releases the underlying connection back to its source.<br>
	 * @throws SqlException If the connection could not be released
	 */
	@Override
	void close() throws SqlException;
}
