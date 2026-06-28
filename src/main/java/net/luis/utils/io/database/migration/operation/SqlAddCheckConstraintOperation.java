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

package net.luis.utils.io.database.migration.operation;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Migration operation that adds a check constraint to an existing table.<br>
 *
 * @author Luis-St
 *
 * @param table The table to add the constraint to
 * @param name The name of the constraint
 * @param condition The condition that must hold for the check constraint
 */
public record SqlAddCheckConstraintOperation(
	@NonNull SqlTable<?> table,
	@NonNull String name,
	@NonNull SqlCondition condition
) implements SqlMigrationOperation {
	
	/**
	 * Constructs a new add check constraint operation with the given table, name and condition.<br>
	 * @throws NullPointerException If the table, name or condition is null
	 */
	public SqlAddCheckConstraintOperation {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Sql constraint name must not be null");
		Objects.requireNonNull(condition, "Sql condition must not be null");
	}
}
