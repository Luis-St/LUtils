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

import net.luis.utils.io.database.dialect.SqlColumnType;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Interface describing changes applied to a column during a migration.<br>
 * Each method returns an {@link Optional} that is present only if the corresponding property was changed.<br>
 *
 * @author Luis-St
 */
public interface SqlColumnChange {
	
	/**
	 * Returns the name of the column being changed.<br>
	 * @return An optional containing the column name
	 */
	@NonNull Optional<String> columnName();
	
	/**
	 * Returns the previous column type before the change.<br>
	 * @return An optional containing the old column type
	 */
	@NonNull Optional<SqlColumnType> oldType();
	
	/**
	 * Returns the new column type after the change.<br>
	 * @return An optional containing the new column type
	 */
	@NonNull Optional<SqlColumnType> newType();
	
	/**
	 * Returns the previous nullability setting before the change.<br>
	 * @return An optional containing the old nullable flag
	 */
	@NonNull Optional<Boolean> oldNullable();
	
	/**
	 * Returns the new nullability setting after the change.<br>
	 * @return An optional containing the new nullable flag
	 */
	@NonNull Optional<Boolean> newNullable();
	
	/**
	 * Returns the previous default value before the change.<br>
	 * @return An optional containing the old default value
	 */
	@NonNull Optional<Object> oldDefault();
	
	/**
	 * Returns the new default value after the change.<br>
	 * @return An optional containing the new default value
	 */
	@NonNull Optional<Object> newDefault();
}
