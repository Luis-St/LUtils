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

import java.util.Objects;

/**
 * Describes a check constraint as read from the database schema.<br>
 *
 * @author Luis-St
 *
 * @param constraintName The name of the check constraint
 * @param checkClause The check clause that defines the constraint
 */
public record SqlCheckConstraintInfo(
	@NonNull String constraintName,
	@NonNull String checkClause
) {
	
	/**
	 * Constructs a new check constraint info with the given values.<br>
	 * @throws NullPointerException If the constraint name or check clause is null
	 */
	public SqlCheckConstraintInfo {
		Objects.requireNonNull(constraintName, "Sql constraint name must not be null");
		Objects.requireNonNull(checkClause, "Sql check clause must not be null");
	}
}
