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

package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import org.jspecify.annotations.NonNull;

/**
 * Represents an object that can be rendered into sql for a specific dialect.<br>
 * Implementations translate themselves into a {@link SqlRendered} that holds the rendered sql statements together with the ordered parameters.<br>
 *
 * @see SqlRendered
 * @see SqlDialect
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlRenderable {
	
	/**
	 * Renders this object into sql using the given dialect.<br>
	 *
	 * @param dialect The dialect that defines how this object is rendered into sql
	 * @return The rendered sql containing the statements and the ordered parameters
	 * @throws NullPointerException If the dialect is null
	 * @throws SqlException If this object cannot be rendered into sql
	 */
	@NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException;
}
