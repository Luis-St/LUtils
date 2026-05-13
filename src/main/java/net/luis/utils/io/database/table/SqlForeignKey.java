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
import net.luis.utils.io.database.exception.SqlAlreadyBindException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlForeignKey<E, T> {
	
	private final SqlTable<T> referencedTable;
	private final List<SqlColumn<T, ?>> referencedColumns = Lists.newArrayList();
	private final SqlReferentialAction onUpdate;
	private final SqlReferentialAction onDelete;
	private SqlTable<E> referencingTable;
	private List<SqlColumn<E, ?>> referencingColumns;
	
	public SqlForeignKey(@NonNull SqlTable<T> referencedTable) {
		Objects.requireNonNull(referencedTable, "Referenced table must not be null");
		this(null, null, referencedTable, referencedTable.getPrimaryKeyColumns(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(@NonNull SqlTable<T> referencedTable, @NonNull SqlColumn<T, ?> referencedColumn) {
		this(null, null, referencedTable, List.of(referencedColumn), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(@NonNull List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns) {
		this(null, referencingColumns, referencedTable, referencedColumns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(
		@Nullable List<SqlColumn<E, ?>> referencingColumns, @NonNull SqlTable<T> referencedTable, @NonNull List<SqlColumn<T, ?>> referencedColumns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete
	) {
		this(null, referencingColumns, referencedTable, referencedColumns, onUpdate, onDelete);
	}
	
	SqlForeignKey(
		@Nullable SqlTable<E> referencingTable,
		@Nullable List<SqlColumn<E, ?>> referencingColumns,
		@NonNull SqlTable<T> referencedTable,
		@NonNull List<SqlColumn<T, ?>> referencedColumns,
		@NonNull SqlReferentialAction onUpdate,
		@NonNull SqlReferentialAction onDelete
	) {
		this.referencingTable = referencingTable;
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
		
		if (referencingColumns != null && referencingTable != null) {
			this.validateReferencingColumns();
		}
		
		for (SqlColumn<?, ?> column : referencedColumns) {
			if (!column.getOwningTable().equals(referencedTable)) {
				throw new IllegalArgumentException("Referenced column " + column.getName() + " does not belong to the referenced table '" + referencedTable.getName() + "'");
			}
		}
	}
	
	//region Validation
	private void validateReferencingColumns() {
		if (this.referencingColumns == null || this.referencingColumns.isEmpty()) {
			throw new IllegalStateException("Referencing columns must be defined for foreign key referencing table '" + this.referencedTable.getName() + "'");
		}
		
		for (SqlColumn<?, ?> column : this.referencingColumns) {
			if (!this.referencingTable.equals(column.getOwningTable())) {
				throw new IllegalArgumentException("Referencing column '" + column.getName() + "' does not belong to the referencing table '" + this.referencingTable.getName() + "'");
			}
		}
	}
	//endregion
	
	public void bindTo(@NonNull SqlTable<E> referencingTable, @NonNull SqlColumn<E, ?> referencingColumns) throws SqlAlreadyBindException {
		Objects.requireNonNull(referencingTable, "Referencing table must not be null");
		Objects.requireNonNull(referencingColumns, "Referencing columns must not be null");
		if (this.referencingTable != null) {
			throw new SqlAlreadyBindException("Foreign key referencing table " + this.referencedTable.getName() + " is already associated with referencing table '" + this.referencingTable.getName() + "'");
		}
		
		this.referencingTable = referencingTable;
		this.referencingColumns = Lists.newArrayList(referencingColumns);
		this.validateReferencingColumns();
	}
	
	public @NonNull SqlTable<E> getReferencingTable() {
		if (this.referencingTable == null) {
			throw new IllegalStateException("Foreign key has not been bound to a referencing table yet, call bindTo() first");
		}
		return this.referencingTable;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<E, ?>> getReferencingColumns() {
		if (this.referencingColumns == null) {
			throw new IllegalStateException("Foreign key has not been bound to a referencing table yet, call bindTo() first");
		}
		return Collections.unmodifiableList(this.referencingColumns);
	}
	
	public @NonNull SqlTable<T> getReferencedTable() {
		return this.referencedTable;
	}
	
	public @NonNull @Unmodifiable List<SqlColumn<T, ?>> getReferencedColumns() {
		return Collections.unmodifiableList(this.referencedColumns);
	}
	
	public @NonNull SqlReferentialAction getOnUpdate() {
		return this.onUpdate;
	}
	
	public @NonNull SqlReferentialAction getOnDelete() {
		return this.onDelete;
	}
}
