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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.SqlReferentialAction;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCompositePrimaryKey(
	@NonNull @Unmodifiable List<SqlColumn<?>> columns,
	@NonNull SqlReferentialAction onUpdate,
	@NonNull SqlReferentialAction onDelete
) {
	
	public SqlCompositePrimaryKey(@NonNull List<SqlColumn<?>> columns) {
		this(columns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlCompositePrimaryKey {
		Objects.requireNonNull(columns, "Referenced columns must not be null");
		Objects.requireNonNull(onUpdate, "On update action must not be null");
		Objects.requireNonNull(onDelete, "On delete action must not be null");
		
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Referenced columns must not be empty");
		}
		
		SqlTable<?> owningTable = columns.getFirst().getOwningTable();
		for (int i = 1; i < columns.size(); i++) {
			SqlColumn<?> column = columns.get(i);
			
			if (column.getOwningTable() != owningTable) {
				throw new IllegalArgumentException(
					"All referenced columns must belong to the same table: Mismatch between '" + column.getOwningTable().getName() + "' and '" + owningTable.getName() + "' of column '" + column.getName() + "'"
				);
			}
		}
		
		columns = List.copyOf(columns);
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<?>> referenceTarget() {
		return this.columns;
	}
}
