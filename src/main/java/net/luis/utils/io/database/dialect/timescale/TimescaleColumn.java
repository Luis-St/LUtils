/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.database.dialect.timescale;

import net.luis.utils.io.database.dialect.postgres.PostgresColumn;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Interface representing a TimescaleDB-specific column.<br>
 * TimescaleDB is built on PostgreSQL, so this extends {@link PostgresColumn}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface TimescaleColumn<T> extends PostgresColumn<T> {
	
	/**
	 * Groups time values into buckets of the specified interval.<br>
	 * Generates SQL: {@code time_bucket('interval', column)}.
	 *
	 * @param interval The bucket interval duration
	 * @return An expression representing the time bucket boundary
	 */
	@NonNull SqlExpression<?> timeBucket(@NonNull Duration interval);
	
	/**
	 * Groups time values into buckets with gap-filling for missing intervals.<br>
	 * Generates SQL: {@code time_bucket_gapfill('interval', column, 'start', 'end')}.<br>
	 * <p>
	 *     Unlike regular {@code time_bucket}, this function inserts rows for empty buckets<br>
	 *     within the specified time range. It is typically used with {@code locf()} or<br>
	 *     {@code interpolate()} to fill in missing values.
	 * </p>
	 *
	 * @param interval The bucket interval duration
	 * @param start The start of the time range to gap-fill
	 * @param end The end of the time range to gap-fill
	 * @return An expression representing the gap-filled time bucket
	 */
	@NonNull SqlExpression<?> timeBucketGapfill(@NonNull Duration interval, @NonNull Instant start, @NonNull Instant end);
	
	/**
	 * Returns the first value of this column ordered by the specified time column.<br>
	 * Generates SQL: {@code first(column, timeColumn)}.
	 *
	 * @param timeColumn The time column used for ordering
	 * @return An expression representing the first value in time order
	 */
	@NonNull SqlExpression<?> first(@NonNull SqlColumn<?> timeColumn);
	
	/**
	 * Returns the last value of this column ordered by the specified time column.<br>
	 * Generates SQL: {@code last(column, timeColumn)}.
	 *
	 * @param timeColumn The time column used for ordering
	 * @return An expression representing the last value in time order
	 */
	@NonNull SqlExpression<?> last(@NonNull SqlColumn<?> timeColumn);
	
	/**
	 * Fills missing values by carrying the last observed value forward.<br>
	 * Generates SQL: {@code locf(column)}.<br>
	 * <p>
	 *     The {@code locf} (last observation carried forward) function is used in combination<br>
	 *     with {@code time_bucket_gapfill} to fill gaps in time-series data with the most<br>
	 *     recent known value.
	 * </p>
	 *
	 * @return An expression representing the gap-filled column value
	 */
	@NonNull SqlExpression<?> locf();
}
