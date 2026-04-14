package net.luis.utils.io.database.function.functions.aggregate;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.query.SqlAlias;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCountDistinctFunction(
	@Nullable SqlExpression<?> value
) implements SqlAggregateFunction<Long> {
	
	@Override
	public @NonNull SqlExpression<Long> as(@NonNull SqlAlias alias) {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<Long> ascending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<Long> descending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<Long> nullsFirst() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<Long> nullsLast() {
		return null;
	}
}
