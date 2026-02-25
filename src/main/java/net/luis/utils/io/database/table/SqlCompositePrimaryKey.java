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

package net.luis.utils.io.database.table;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a composite primary key definition on a table.<br>
 * <p>
 *     A composite primary key consists of multiple columns that together
 *     uniquely identify a row. The framework recognizes this definition
 *     for identity-based operations and constraint generation.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The entity type
 */
public interface SqlCompositePrimaryKey<T> {
	
	/**
	 * Returns the columns forming this composite primary key.<br>
	 * @return An unmodifiable list of columns in declaration order
	 */
	@NonNull List<SqlColumn<?>> columns();
}

