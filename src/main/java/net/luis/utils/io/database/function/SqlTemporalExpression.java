package net.luis.utils.io.database.function;

import org.jspecify.annotations.NonNull;

import java.time.*;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlTemporalExpression<T> extends SqlOrderableExpression<T> {
	
	static @NonNull SqlTemporalExpression<LocalDateTime> now() {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalDate> currentDate() {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalTime> currentTime() {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalDateTime> currentTimestamp() {
		return null;
	}
	
	static <T extends Number> @NonNull SqlTemporalExpression<LocalDateTime> fromEpoch(long epoch) {
		return null;
	}
	
	static <T extends Number> @NonNull SqlTemporalExpression<LocalDateTime> fromEpoch(@NonNull SqlNumericExpression<T> expression) {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalDate> makeDate(int year, int month, int day) {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalDate> makeDate(@NonNull SqlNumericExpression<Integer> year, @NonNull SqlNumericExpression<Integer> month, @NonNull SqlNumericExpression<Integer> day) {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalTime> makeTime(int hour, int minute, int second) {
		return null;
	}
	
	static @NonNull SqlTemporalExpression<LocalTime> makeTime(@NonNull SqlNumericExpression<Integer> hour, @NonNull SqlNumericExpression<Integer> minute, @NonNull SqlNumericExpression<Integer> second) {
		return null;
	}
	
	@NonNull SqlNumericExpression<Integer> extract(@NonNull SqlTemporalPart part);
	
	@NonNull SqlTemporalExpression<T> truncate(@NonNull SqlTemporalPart part);
	
	@NonNull SqlTemporalExpression<T> add(@NonNull SqlTemporalPart part, int amount);
	
	default @NonNull SqlTemporalExpression<T> subtract(@NonNull SqlTemporalPart part, int amount) {
		return this.add(part, -amount);
	}
	
	@NonNull SqlNumericExpression<Long> toEpoch();
	
	@NonNull SqlTemporalExpression<LocalDate> toDate();
	
	@NonNull SqlTemporalExpression<LocalTime> toTime();
}
