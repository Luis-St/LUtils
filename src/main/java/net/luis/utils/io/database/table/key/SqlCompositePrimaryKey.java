package net.luis.utils.io.database.table.key;

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

public record SqlCompositePrimaryKey(
	@NonNull @Unmodifiable List<SqlColumn<?>> columns,
	@NonNull SqlReferentialAction onUpdate,
	@NonNull SqlReferentialAction onDelete
) implements SqlReference<List<SqlColumn<?>>> {
	
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
	
	@Override
	public @NonNull @Unmodifiable List<SqlColumn<?>> referenceTarget() {
		return this.columns;
	}
}
