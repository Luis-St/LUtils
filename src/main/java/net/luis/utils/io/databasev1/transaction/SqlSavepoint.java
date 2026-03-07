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

package net.luis.utils.io.databasev1.transaction;

import net.luis.utils.io.databasev1.exception.SqlTransactionException;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL savepoint.<br>
 *
 * @author Luis-St
 */
public interface SqlSavepoint {
	
	/**
	 * Returns the name of this savepoint.<br>
	 * @return The savepoint name
	 */
	@NonNull String name();
	
	/**
	 * Releases this savepoint.<br>
	 * Executes SQL: {@code RELEASE SAVEPOINT name}.<br>
	 *
	 * @throws SqlTransactionException If the savepoint cannot be released
	 */
	void release() throws SqlTransactionException;
}
