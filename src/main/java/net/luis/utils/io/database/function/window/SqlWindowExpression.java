package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.column.SqlColumn;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlWindowExpression<T> {
	
	// might be moved to another interface/class
	
	static @NonNull SqlExpression<Long> rowNumber() {
		return null;
	}
	
	static @NonNull SqlExpression<Long> rank() {
		return null;
	}
	
	static @NonNull SqlExpression<Long> denseRank() {
		return null;
	}
	
	static @NonNull SqlExpression<Long> tileBucket(int buckets) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	static @NonNull SqlExpression<Double> percentRank() {
		return null;
	}
	
	static @NonNull SqlExpression<Double> cumulativeDistribution() {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column, int offset) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column, int offset) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> firstValue(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> lastValue(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	static <T> @NonNull SqlExpression<T> valueAt(@NonNull SqlColumn<T> column, int position) {
		return null;
	}
}
