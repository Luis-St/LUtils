/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint.config.temporal.provider;

import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Interface that provides access to date field values from temporal types.<br>
 * <p>
 *     This interface is used to extract day of week, day of month, month, and year values
 *     from various temporal types in a uniform way, enabling delegation of validation logic.
 * </p>
 *
 * @author Luis-St
 */
public interface DateFieldProvider {
	
	/**
	 * Creates a date field provider from the given suppliers.<br>
	 *
	 * @param dayOfWeekSupplier The supplier for the day of week
	 * @param dayOfMonthSupplier The supplier for the day of month
	 * @param monthSupplier The supplier for the month
	 * @param yearSupplier The supplier for the year
	 * @return A date field provider
	 * @throws NullPointerException If any of the suppliers is null
	 */
	static @NonNull DateFieldProvider create(@NonNull Supplier<DayOfWeek> dayOfWeekSupplier, @NonNull IntSupplier dayOfMonthSupplier, @NonNull Supplier<Month> monthSupplier, @NonNull IntSupplier yearSupplier) {
		Objects.requireNonNull(dayOfWeekSupplier, "Day of week supplier must not be null");
		Objects.requireNonNull(dayOfMonthSupplier, "Day of month supplier must not be null");
		Objects.requireNonNull(monthSupplier, "Month supplier must not be null");
		Objects.requireNonNull(yearSupplier, "Year supplier must not be null");
		
		return new DateFieldProvider() {
			@Override
			public @NonNull Supplier<DayOfWeek> dayOfWeek() {
				return dayOfWeekSupplier;
			}
			
			@Override
			public @NonNull IntSupplier dayOfMonth() {
				return dayOfMonthSupplier;
			}
			
			@Override
			public @NonNull Supplier<Month> month() {
				return monthSupplier;
			}
			
			@Override
			public @NonNull IntSupplier year() {
				return yearSupplier;
			}
		};
	}
	
	/**
	 * Gets the day of week.<br>
	 * @return A Supplier that provides the DayOfWeek value
	 */
	@NonNull Supplier<DayOfWeek> dayOfWeek();
	
	/**
	 * Gets the day of month (1-31).<br>
	 * @return An IntSupplier that provides the day of month value
	 */
	@NonNull IntSupplier dayOfMonth();
	
	/**
	 * Gets the month.<br>
	 * @return A Supplier that provides the Month value
	 */
	@NonNull Supplier<Month> month();
	
	/**
	 * Gets the year.<br>
	 * @return An IntSupplier that provides the year value
	 */
	@NonNull IntSupplier year();
}
