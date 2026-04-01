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

import com.google.common.collect.Lists;
import net.luis.utils.io.database.SqlReferentialAction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlForeignKey {
	
	private final SqlTable<?> referencedTable;
	private final List<SqlColumn<?>> referencedColumns = Lists.newArrayList();
	private final SqlReferentialAction onUpdate;
	private final SqlReferentialAction onDelete;
	private List<SqlColumn<?>> referencingColumns;
	
	public SqlForeignKey(@NonNull SqlTable<?> referencedTable) {
		Objects.requireNonNull(referencedTable, "Referenced table must not be null");
		this(null, referencedTable, referencedTable.getPrimaryKeyColumns(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(@NonNull SqlTable<?> referencedTable, @NonNull SqlColumn<?> referencedColumn) {
		this(null, referencedTable, List.of(referencedColumn), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(@NonNull List<SqlColumn<?>> referencingColumns, @NonNull SqlTable<?> referencedTable, @NonNull List<SqlColumn<?>> referencedColumns) {
		this(referencingColumns, referencedTable, referencedColumns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(
		@Nullable List<SqlColumn<?>> referencingColumns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull List<SqlColumn<?>> referencedColumns,
		@NonNull SqlReferentialAction onUpdate,
		@NonNull SqlReferentialAction onDelete
	) {
		this.referencingColumns = referencingColumns;
		this.referencedTable = Objects.requireNonNull(referencedTable, "Referenced table must not be null");
		this.referencedColumns.addAll(Objects.requireNonNull(referencedColumns, "Referenced columns must not be null"));
		this.onUpdate = Objects.requireNonNull(onUpdate, "On update action must not be null");
		this.onDelete = Objects.requireNonNull(onDelete, "On delete action must not be null");
		
		if (referencingColumns != null && referencingColumns.isEmpty()) {
			throw new IllegalArgumentException("Referencing columns must not be empty");
		}
		if (referencedColumns.isEmpty()) {
			throw new IllegalArgumentException("Referenced columns must not be empty");
		}
		
		if (referencingColumns != null) {
			this.validateReferencingColumns();
		}
		
		for (SqlColumn<?> column : referencedColumns) {
			if (!column.getOwningTable().equals(referencedTable)) {
				throw new IllegalArgumentException("Referenced column '" + column.getName() + "' does not belong to the referenced table '" + referencedTable.getName() + "'");
			}
		}
	}
	
	private void validateReferencingColumns() {
		if (this.referencingColumns == null || this.referencingColumns.isEmpty()) {
			throw new IllegalStateException("Referencing columns must be defined for foreign key referencing table '" + this.referencedTable.getName() + "'");
		}
		
		for (SqlColumn<?> column : this.referencingColumns) {
			if (!column.getOwningTable().equals(this.referencedTable)) {
				throw new IllegalArgumentException("Referencing column '" + column.getName() + "' does not belong to the referenced table '" + this.referencedTable.getName() + "'");
			}
		}
	}
	
	public @NonNull SqlTable<?> getReferencedTable() {
		return this.referencedTable;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<?>> getReferencedColumns() {
		return Collections.unmodifiableList(this.referencedColumns);
	}
	
	public @NonNull SqlReferentialAction getOnUpdate() {
		return this.onUpdate;
	}
	
	public @NonNull SqlReferentialAction getOnDelete() {
		return this.onDelete;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<?>> getReferencingColumns() {
		return Collections.unmodifiableList(this.referencingColumns);
	}
	
	@ApiStatus.Internal
	void setReferencingColumn(@NonNull SqlColumn<?> referencingColumns) {
		Objects.requireNonNull(referencingColumns, "Referencing column must not be null");
		if (this.referencingColumns != null) {
			throw new IllegalStateException("Referencing columns are already defined for foreign key referencing table '" + this.referencedTable.getName() + "'");
		}
		
		this.referencingColumns = Lists.newArrayList(referencingColumns);
		this.validateReferencingColumns();
	}
}
