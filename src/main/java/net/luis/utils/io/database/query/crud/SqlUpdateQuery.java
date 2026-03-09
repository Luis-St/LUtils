package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.SqlException;
import net.luis.utils.io.database.SqlTable;
import net.luis.utils.io.database.column.SqlColumn;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqlUpdateQuery<T> implements SqlJoinableQuery<T> {
	
	@Override
	public @NonNull SqlUpdateQuery<T> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<T> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<T> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<T> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return null;
	}
	
	@Override
	public @NonNull SqlUpdateQuery<T> crossJoin(@NonNull SqlTable<?> table) {
		return null;
	}
	
	public <V> @NonNull SqlUpdateQuery<T> set(@NonNull SqlColumn<V> column, @NonNull V value) {
		return null;
	}
	
	public <V> @NonNull SqlUpdateQuery<T> set(@NonNull SqlColumn<V> column, @NonNull SqlExpression<V> expression) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<T> where(@NonNull SqlCondition condition) {
		return null;
	}
	
	public @NonNull SqlUpdateQuery<T> batchSize(int batchSize) {
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
