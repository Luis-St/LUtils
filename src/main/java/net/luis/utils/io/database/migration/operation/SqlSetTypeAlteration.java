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

import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Column alteration that changes the sql type of a column.<br>
 *
 * @author Luis-St
 *
 * @param type The new sql type of the column
 */
public record SqlSetTypeAlteration(@NonNull SqlType<?> type) implements SqlColumnAlteration {
	
	/**
	 * Constructs a new set type alteration with the given sql type.<br>
	 * @throws NullPointerException If the type is null
	 */
	public SqlSetTypeAlteration {
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
