package net.luis.utils.io.database.query;

import net.luis.utils.io.database.SqlTable;
import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlJoinableQuery<T> extends SqlQuery<T> {
	
	@NonNull SqlJoinableQuery<T> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@NonNull SqlJoinableQuery<T> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@NonNull SqlJoinableQuery<T> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@NonNull SqlJoinableQuery<T> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@NonNull SqlJoinableQuery<T> crossJoin(@NonNull SqlTable<?> table);
}
