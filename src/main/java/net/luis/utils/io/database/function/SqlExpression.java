package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.query.SqlAlias;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlExpression<T> {
	
	@SafeVarargs
	static <T> @NonNull SqlExpression<T> coalesce(@NonNull SqlExpression<T> @NonNull ... values) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> nullif(@NonNull SqlExpression<T> value1, @NonNull T value2) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull T thenValue, @NonNull T elseValue) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> of(@NonNull String functionName, @NonNull Class<T> resultType, SqlExpression<?> @NonNull ... args) {
		return null;
	}
	
	@NonNull SqlCondition equalTo(@NonNull T value);
	
	@NonNull SqlCondition equalTo(@NonNull SqlExpression<T> other);
	
	@NonNull SqlCondition isDistinctFrom(@NonNull T value);
	
	@NonNull SqlCondition isNull();
	
	@NonNull SqlExpression<T> as(@NonNull SqlAlias alias);
	
	<R> @NonNull SqlExpression<R> cast(@NonNull Class<R> type);
	
	@NonNull SqlExpression<Long> count();
	
	@NonNull SqlExpression<Long> countDistinct();
	
	@NonNull SqlExpression<T> over(@NonNull SqlWindowClause clause);
	
	@NonNull SqlExpression<T> over();
}
