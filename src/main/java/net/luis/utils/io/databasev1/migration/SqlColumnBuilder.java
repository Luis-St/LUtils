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

package net.luis.utils.io.databasev1.migration;

import net.luis.utils.io.databasev1.condition.SqlCondition;
import net.luis.utils.io.databasev1.table.SqlPrimaryKeyColumn;
import org.jspecify.annotations.NonNull;

/**
 * Builder for column options in migration table definitions.<br>
 * Provides a fluent API for configuring column constraints such as nullability, uniqueness, default values, auto-increment, foreign keys, and check constraints.<br>
 *
 * @author Luis-St
 *
 * @param <V> The type of the column value
 */
public interface SqlColumnBuilder<V> {
	
	/**
	 * Marks the column as not null.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> notNull();
	
	/**
	 * Marks the column as unique.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> unique();
	
	/**
	 * Sets the default value for the column.<br>
	 *
	 * @param value The default value
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> defaultValue(@NonNull V value);
	
	/**
	 * Marks the column as auto-incrementing.<br>
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> autoIncrement();
	
	/**
	 * Adds a foreign key reference to the specified primary key column.<br>
	 *
	 * @param referencedColumn The referenced primary key column (carries its owning table)
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> references(@NonNull SqlPrimaryKeyColumn<?, ?> referencedColumn);
	
	/**
	 * Adds a check constraint to the column.<br>
	 *
	 * @param condition The check condition
	 * @return This builder for chaining
	 */
	@NonNull SqlColumnBuilder<V> check(@NonNull SqlCondition condition);
}
