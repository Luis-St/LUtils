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

/*package net.luis.utils.io.databasev1.migration;

import net.luis.utils.io.databasev1.condition.SqlCondition;
import net.luis.utils.io.databasev1.index.SqlIndexMethod;
import net.luis.utils.io.databasev1.table.SqlColumn;
import org.jspecify.annotations.NonNull;

*//**
 * Builder for index definitions in migrations.<br>
 * Provides a fluent API for specifying the columns, uniqueness, method, and optional partial index condition.<br>
 *
 * @author Luis-St
 *//*
public interface SqlIndexBuilder {
	
	*//**
	 * Specifies the columns included in the index.<br>
	 *
	 * @param columns The column references
	 * @return This builder for chaining
	 *//*
	@NonNull SqlIndexBuilder columns(SqlColumn<?> @NonNull ... columns);
	
	*//**
	 * Marks the index as unique.<br>
	 * @return This builder for chaining
	 *//*
	@NonNull SqlIndexBuilder unique();
	
	*//**
	 * Specifies the index method to use.<br>
	 *
	 * @param method The index method
	 * @return This builder for chaining
	 *//*
	@NonNull SqlIndexBuilder method(@NonNull SqlIndexMethod method);
	
	*//**
	 * Adds a partial index condition.<br>
	 * Generates SQL: {@code CREATE INDEX ... WHERE condition}.<br>
	 *
	 * @param condition The condition for the partial index
	 * @return This builder for chaining
	 *//*
	@NonNull SqlIndexBuilder where(@NonNull SqlCondition condition);
}*/
