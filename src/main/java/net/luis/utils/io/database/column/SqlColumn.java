package net.luis.utils.io.database.column;

import net.luis.utils.io.database.function.*;
import net.luis.utils.io.database.query.SqlAlias;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public abstract class SqlColumn<T> {
	
	public @NonNull SqlExpression<T> of(@NonNull SqlAlias alias) {
		return null;
	}
	
	public @NonNull SqlOrderableExpression<T> orderable() {
		return null;
	}
	
	public @NonNull SqlNumericExpression<T> numeric() {
		return null;
	}
	
	public @NonNull SqlStringExpression string() {
		return null;
	}
	
	public @NonNull SqlTemporalExpression<T> temporal() {
		return null;
	}
}
