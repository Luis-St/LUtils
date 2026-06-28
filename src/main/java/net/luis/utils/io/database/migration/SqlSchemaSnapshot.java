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

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Captures a snapshot of the database schema at a point in time.<br>
 * The snapshot consists of the columns and the check constraints read from the schema.<br>
 *
 * @author Luis-St
 *
 * @param columns The columns read from the schema
 * @param checkConstraints The check constraints read from the schema, mapped by table name
 */
public record SqlSchemaSnapshot(
	@NonNull List<SqlSchemaColumnInfo> columns,
	@NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints
) {
	
	/**
	 * Constructs a new schema snapshot with the given values.<br>
	 * @throws NullPointerException If the columns or check constraints are null
	 */
	public SqlSchemaSnapshot {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(checkConstraints, "Sql check constraints must not be null");
	}
}
