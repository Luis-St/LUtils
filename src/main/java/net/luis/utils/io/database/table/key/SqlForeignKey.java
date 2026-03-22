package net.luis.utils.io.database.table.key;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record SqlForeignKey(
	@NonNull @Unmodifiable List<SqlColumn<?>> referencingColumns,
	@NonNull SqlTable<?> referencedTable,
	@NonNull @Unmodifiable List<SqlColumn<?>> referencedColumns,
	@NonNull SqlReferentialAction onUpdate,
	@NonNull SqlReferentialAction onDelete
) {
	
	public SqlForeignKey(@NonNull SqlColumn<?> referencingColumn, @NonNull SqlTable<?> referencedTable) {
		Objects.requireNonNull(referencedTable, "Referenced table must not be null");
		this(Lists.newArrayList(referencingColumn), referencedTable, referencedTable.getPrimaryKeyColumns());
	}
	
	public SqlForeignKey(@NonNull SqlColumn<?> referencingColumn, @NonNull SqlTable<?> referencedTable, @NonNull SqlColumn<?> referencedColumn) {
		this(List.of(referencingColumn), referencedTable, List.of(referencedColumn), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey(@NonNull List<SqlColumn<?>> referencingColumns, @NonNull SqlTable<?> referencedTable, @NonNull List<SqlColumn<?>> referencedColumns) {
		this(referencingColumns, referencedTable, referencedColumns, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
	}
	
	public SqlForeignKey {
		Objects.requireNonNull(referencingColumns, "Referencing columns must not be null");
		Objects.requireNonNull(referencedTable, "Referenced table must not be null");
		Objects.requireNonNull(referencedColumns, "Referenced columns must not be null");
		Objects.requireNonNull(onUpdate, "On update action must not be null");
		Objects.requireNonNull(onDelete, "On delete action must not be null");
		
		if (referencingColumns.isEmpty()) {
			throw new IllegalArgumentException("Referencing columns must not be empty");
		}
		if (referencedColumns.isEmpty()) {
			throw new IllegalArgumentException("Referenced columns must not be empty");
		}
		if (referencingColumns.size() != referencedColumns.size()) {
			throw new IllegalArgumentException("Number of referencing columns must match number of referenced columns");
		}
		
		for (SqlColumn<?> column : referencingColumns) {
			if (!column.getOwningTable().equals(referencedTable)) {
				throw new IllegalArgumentException("Referencing column '" + column.getName() + "' does not belong to the referenced table '" + referencedTable.getName() + "'");
			}
		}
		
		for (SqlColumn<?> column : referencedColumns) {
			if (!column.getOwningTable().equals(referencedTable)) {
				throw new IllegalArgumentException("Referenced column '" + column.getName() + "' does not belong to the referenced table '" + referencedTable.getName() + "'");
			}
		}
		
		referencingColumns = List.copyOf(referencingColumns);
		referencedColumns = List.copyOf(referencedColumns);
	}
}
