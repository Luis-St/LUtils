package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.SqlException;
import net.luis.utils.io.database.SqlTable;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqlDeleteQuery<T> implements SqlJoinableQuery<T> {
	
	@Override
	public @NonNull SqlDeleteQuery<T> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlDeleteQuery<T> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlDeleteQuery<T> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlDeleteQuery<T> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlDeleteQuery<T> crossJoin(@NonNull SqlTable<?> table) {
		return null;
	}
	
	public @NonNull SqlDeleteQuery<T> where(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlDeleteQuery<T> batchSize(int batchSize) {
		return null;
	}
	
	public int execute() throws SqlException {
		return 0;
	}
	
	public @NonNull List<T> returning() throws SqlException {
		return null;
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
		return null;
	}
}
