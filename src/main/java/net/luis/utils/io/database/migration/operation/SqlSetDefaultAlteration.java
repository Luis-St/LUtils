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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Column alteration that sets the default value of a column.<br>
 *
 * @author Luis-St
 *
 * @param value The new default value of the column
 */
public record SqlSetDefaultAlteration(@NonNull Object value) implements SqlColumnAlteration {
	
	/**
	 * Constructs a new set default alteration with the given default value.<br>
	 * @throws NullPointerException If the value is null
	 */
	public SqlSetDefaultAlteration {
		Objects.requireNonNull(value, "Sql default value must not be null");
	}
}
