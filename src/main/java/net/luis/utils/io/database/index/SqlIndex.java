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

package net.luis.utils.io.database.index;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlColumn;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public record SqlIndex(
	@NonNull String name,
	@NonNull List<SqlColumn<?, ?>> columns,
	boolean unique,
	@Nullable SqlCondition whereCondition,
	@NonNull SqlIndexMethod method
) {
	
	public SqlIndex(@NonNull String name, @NonNull List<SqlColumn<?, ?>> columns, boolean unique, @NotNull SqlIndexMethod method) {
		this(name, columns, unique, null, method);
	}
}
