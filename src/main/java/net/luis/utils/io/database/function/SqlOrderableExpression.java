package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlOrderableExpression<T> extends SqlExpression<T>, SqlOrderable {
	
	@SafeVarargs
	static <T> @NonNull SqlOrderableExpression<T> greatest(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		return null;
	}
	
	@SafeVarargs
	static <T> @NonNull SqlOrderableExpression<T> least(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		return null;
	}
	
	@NonNull SqlCondition greaterThan(@NonNull T value);
	
	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull T value);
	
	@NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> other);
	
	@NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> other);
	
	@NonNull SqlCondition lessThan(@NonNull T value);
	
	@NonNull SqlCondition lessThanOrEqualTo(@NonNull T value);
	
	@NonNull SqlCondition lessThan(@NonNull SqlExpression<T> other);
	
	@NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> other);
	
	@NonNull SqlCondition between(@NonNull T start, @NonNull T end);
	
	@NonNull SqlCondition between(@NonNull SqlExpression<T> start, @NonNull SqlExpression<T> end);
	
	@NonNull SqlOrderableExpression<T> min();
	
	@NonNull SqlOrderableExpression<T> max();
}
