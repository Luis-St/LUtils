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
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record SqlColumnOptions(
	boolean notNull,
	boolean unique,
	boolean autoIncrement,
	@NonNull Optional<Object> defaultValue,
	@Nullable SqlTable<?> referencesTable,
	@Nullable SqlCondition check
) {
	
	public static final SqlColumnOptions EMPTY = new SqlColumnOptions(false, false, false, Optional.empty(), null, null);
	
	public SqlColumnOptions {
		Objects.requireNonNull(defaultValue, "Sql default value must not be null");
	}
}
