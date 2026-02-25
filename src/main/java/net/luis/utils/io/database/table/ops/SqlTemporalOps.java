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

package net.luis.utils.io.database.table.ops;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.time.*;

/**
 * Interface providing temporal-specific SQL operations for column conditions.<br>
 * These operations generate SQL conditions for time-based range checks and comparisons.<br>
 *
 * @author Luis-St
 */
public interface SqlTemporalOps {
	
	/**
	 * Creates a condition that checks if the column value is within the last given duration.<br>
	 * Generates SQL: {@code column >= NOW() - INTERVAL 'duration'}.<br>
	 *
	 * @param duration The duration to check within
	 * @return The within-last condition
	 */
	@NonNull SqlCondition withinLast(@NonNull Duration duration);
	
	/**
	 * Creates a condition that checks if the column value is within the next given duration.<br>
	 * Generates SQL: {@code column <= NOW() + INTERVAL 'duration'}.<br>
	 *
	 * @param duration The duration to check within
	 * @return The within-next condition
	 */
	@NonNull SqlCondition withinNext(@NonNull Duration duration);
	
	/**
	 * Creates a condition that checks if the column value is before the given instant.<br>
	 * Generates SQL: {@code column < instant}.<br>
	 *
	 * @param instant The instant to compare against
	 * @return The before condition
	 */
	@NonNull SqlCondition before(@NonNull Instant instant);
	
	/**
	 * Creates a condition that checks if the column value is after the given instant.<br>
	 * Generates SQL: {@code column > instant}.<br>
	 *
	 * @param instant The instant to compare against
	 * @return The after condition
	 */
	@NonNull SqlCondition after(@NonNull Instant instant);
	
	/**
	 * Creates a condition that checks if the column value falls on the given date.<br>
	 * Generates SQL: {@code column >= date AND column < date + 1 day} or {@code column::date = date} depending on the dialect.<br>
	 *
	 * @param date The date to check against
	 * @return The on-date condition
	 */
	@NonNull SqlCondition onDate(@NonNull LocalDate date);
}
