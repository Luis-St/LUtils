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

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 * Builder for column options in migration table definitions.<br>
 * <p>
 *     Provides a fluent API for configuring column constraints such as
 *     nullability, uniqueness, default values, auto-increment, foreign keys, and check constraints.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlColumnBuilder {
	
	/**
	 * Marks the column as not null.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder notNull();
	
	/**
	 * Marks the column as unique.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder unique();
	
	/**
	 * Sets the default value for the column.<br>
	 *
	 * @param value The default value
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder defaultValue(@NonNull Object value);
	
	/**
	 * Marks the column as auto-incrementing.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder autoIncrement();
	
	/**
	 * Adds a foreign key reference to the specified table and column.<br>
	 *
	 * @param table The referenced table name
	 * @param column The referenced column name
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder references(@NonNull String table, @NonNull String column);
	
	/**
	 * Adds a check constraint to the column.<br>
	 *
	 * @param condition The check condition
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder check(@NonNull SqlCondition condition);
}
