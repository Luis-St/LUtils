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

package net.luis.utils.io.database.operation;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing numeric-specific column operations.<br>
 *
 * @author Luis-St
 */
public interface SqlNumericOps {
	
	/**
	 * Creates a condition that checks if the column value is between the given range.<br>
	 * Generates SQL: {@code column BETWEEN start AND end}.<br>
	 *
	 * @param start The start of the range
	 * @param end The end of the range
	 * @return The between condition
	 */
	@NonNull SqlCondition between(@NonNull Number start, @NonNull Number end);
}
