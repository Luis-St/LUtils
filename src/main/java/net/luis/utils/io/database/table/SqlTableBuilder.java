package net.luis.utils.io.database.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.SqlDataType;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.key.*;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTableBuilder<T> {
	
	private final Class<T> type;
	private final String name;
	private final String schema;
	private Optional<SqlCompositePrimaryKey> compositePrimaryKey = Optional.empty();
	private final List<SqlForeignKey> foreignKeys = Lists.newArrayList();
	private final List<List<SqlColumn<?>>> uniqueConstraints = Lists.newArrayList();
	private final List<SqlCondition> checkConstraints = Lists.newArrayList();
	private final Map</*Name*/ String, SqlColumn<?>> columns = Maps.newHashMap();
	
	SqlTableBuilder(@NonNull Class<T> type, @NonNull String name, @NonNull String schema) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.schema = Objects.requireNonNull(schema, "Schema must not be null");
		
		if (name.isBlank()) {
			throw new IllegalArgumentException("Table name must not be blank");
		}
		if (schema.isBlank()) {
			throw new IllegalArgumentException("Table schema must not be blank");
		}
	}
	
	public <C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull SqlDataType<C> dataType) {
		return this.column(name, dataType, _ -> {});
	}
	
	public <C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull SqlDataType<C> dataType, @NonNull Consumer<SqlColumnBuilder<C>> action) {
		Objects.requireNonNull(action, "Column configuration action must not be null");
		
		SqlColumnBuilder<C> builder = SqlColumn.builder(name, dataType);
		action.accept(builder);
		
		SqlColumn<C> column = builder.build();
		if (this.columns.containsKey(column.getName())) {
			throw new IllegalStateException("Column with name '" + name + "' already exists in table " + this.name);
		}
		if (column.isPrimaryKey() && this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Unable to add primary key column '" + name + "' to table " + this.name + " because a composite primary key is already defined");
		}
		this.columns.put(column.getName(), column);
		return column;
	}
	
	public @NonNull SqlCompositePrimaryKey compositePrimaryKey(@NonNull List<SqlColumn<?>> columns) {
		return this.compositePrimaryKey(new SqlCompositePrimaryKey(columns));
	}
	
	public @NonNull SqlCompositePrimaryKey compositePrimaryKey(@NonNull List<SqlColumn<?>> columns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete) {
		return this.compositePrimaryKey(new SqlCompositePrimaryKey(columns, onUpdate, onDelete));
	}
	
	public @NonNull SqlCompositePrimaryKey generateCompositePrimaryKey() {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Unable to generate composite primary key for table " + this.name + " because a composite primary key is already defined");
		}
		
		List<SqlColumn<?>> columns = Lists.newArrayList();
		for (SqlColumn<?> column : this.columns.values()) {
			if (column.isPrimaryKey()) {
				columns.add(column);
			}
		}
		
		if (columns.isEmpty()) {
			throw new IllegalStateException("Unable to generate composite primary key for table " + this.name + " because no primary key columns are defined");
		}
		return this.compositePrimaryKey(new SqlCompositePrimaryKey(columns));
	}
	
	private @NonNull SqlCompositePrimaryKey compositePrimaryKey(@NonNull SqlCompositePrimaryKey reference) {
		if (this.compositePrimaryKey.isPresent()) {
			throw new IllegalStateException("Composite primary key already defined for table " + this.name);
		}
		
		this.compositePrimaryKey = Optional.of(reference);
		return reference;
	}
	
	public @NonNull SqlForeignKey foreignKey(@NonNull SqlColumn<?> referencingColumn, @NonNull SqlTable<?> referencedTable) {
		return this.foreignKey(new SqlForeignKey(referencingColumn, referencedTable));
	}
	
	public @NonNull SqlForeignKey foreignKey(@NonNull SqlColumn<?> referencingColumn, @NonNull SqlTable<?> referencedTable, @NonNull SqlColumn<?> referencedColumn) {
		return this.foreignKey(new SqlForeignKey(referencingColumn, referencedTable, referencedColumn));
	}
	
	public @NonNull SqlForeignKey foreignKey(@NonNull List<SqlColumn<?>> columns, @NonNull SqlTable<?> referencedTable, @NonNull List<SqlColumn<?>> referencedColumns) {
		return this.foreignKey(new SqlForeignKey(columns, referencedTable, referencedColumns));
	}
	
	public @NonNull SqlForeignKey foreignKey(@NonNull List<SqlColumn<?>> referencingColumns, @NonNull SqlTable<?> referencedTable, @NonNull List<SqlColumn<?>> referencedColumns, @NonNull SqlReferentialAction onUpdate, @NonNull SqlReferentialAction onDelete) {
		return this.foreignKey(new SqlForeignKey(referencingColumns, referencedTable, referencedColumns, onUpdate, onDelete));
	}
	
	private @NonNull SqlForeignKey foreignKey(@NonNull SqlForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
		return foreignKey;
	}
	
	public void uniqueConstraint(SqlColumn<?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		
		this.uniqueConstraint(Lists.newArrayList(columns));
	}
	
	public void uniqueConstraint(@NonNull List<SqlColumn<?>> columns) {
		Objects.requireNonNull(columns, "Columns must not be null");
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Unique constraint must contain at least one column");
		}
		
		for (SqlColumn<?> column : columns) {
			Objects.requireNonNull(column, "Unique constraint columns must not contain null values");
			
			if (!this.columns.containsKey(column.getName())) {
				throw new IllegalArgumentException("Column '" + column.getName() + "' is not defined in table " + this.name);
			}
		}
		
		this.uniqueConstraints.add(columns);
	}
	
	public void checkConstraint(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Condition must not be null");
		
		this.checkConstraints.add(condition);
	}
	
	public @NonNull SqlTable<T> build() {
		return new SqlTable<>(this.type, this.name, this.schema, this.compositePrimaryKey, this.foreignKeys, this.uniqueConstraints, this.checkConstraints, this.columns);
	}
}
