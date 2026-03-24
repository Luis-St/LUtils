package net.luis.utils.io.database.table;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTableProvider<T> {
	
	public void create() throws SqlException {
	
	}
	
	public void createIfNotExists() throws SqlException {
	
	}
	
	public boolean exists() throws SqlException {
		return false;
	}
	
	public void truncate() throws SqlException {
	
	}
	
	public void drop() throws SqlException {
	
	}
	
	public void dropIfExists() throws SqlException {
	
	}
	
	public void createIndex(@NotNull String name, @NonNull List<SqlColumn<?>> columns, @NonNull SqlIndexMethod method) throws SqlException {
	
	}
	
	public void createIndex(@NotNull String name, @NonNull List<SqlColumn<?>> columns, boolean unique, @NonNull SqlIndexMethod method) throws SqlException {
	
	}
	
	public void createIndex(@NotNull String name, @NonNull List<SqlColumn<?>> columns, boolean unique, @Nullable SqlCondition whereCondition, @NonNull SqlIndexMethod method) throws SqlException {
	
	}
	
	public @NotNull @Unmodifiable List<SqlIndex> getIndexes() throws SqlException {
		return null;
	}
	
	public void dropIndex(@NotNull String name) throws SqlException {
	
	}
}
