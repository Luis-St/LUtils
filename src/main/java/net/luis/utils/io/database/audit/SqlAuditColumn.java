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

package net.luis.utils.io.database.audit;

import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Describes a single audit column that is managed automatically for audited tables.<br>
 * An audit column is defined by its name, its sql type, the {@link SqlAuditRole role} it fulfills and whether it is nullable.<br>
 *
 * @author Luis-St
 *
 * @param name The name of the audit column
 * @param type The sql type of the audit column
 * @param role The role the audit column fulfills
 * @param nullable Whether the audit column is nullable
 */
public record SqlAuditColumn(
	@NonNull String name,
	@NonNull SqlType<?> type,
	@NonNull SqlAuditRole role,
	boolean nullable
) {
	
	/**
	 * Constructs a new audit column with the given name, type, role and nullability.<br>
	 *
	 * @throws NullPointerException If the name, type or role is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public SqlAuditColumn {
		Objects.requireNonNull(name, "Sql audit column name must not be null");
		Objects.requireNonNull(type, "Sql audit column type must not be null");
		Objects.requireNonNull(role, "Sql audit column role must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Sql audit column name must not be blank");
		}
	}
}
