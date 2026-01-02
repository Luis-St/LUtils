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

package net.luis.utils.io.codec.constraint.config.temporal.provider;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * Interface that provides access to time field values from temporal types.<br>
 * <p>
 *     This interface is used to extract hour, minute, second, and millisecond values
 *     from various temporal types in a uniform way, enabling delegation of validation logic.
 * </p>
 *
 * @author Luis-St
 */
public interface TimeFieldProvider {
	
	/**
	 * Creates a time field provider from the given suppliers.<br>
	 *
	 * @param hourSupplier The supplier for the hour
	 * @param minuteSupplier The supplier for the minute
	 * @param secondSupplier The supplier for the second
	 * @param millisecondSupplier The supplier for the millisecond
	 * @return A time field provider
	 * @throws NullPointerException If any of the suppliers is null
	 */
	static @NonNull TimeFieldProvider create(@NonNull IntSupplier hourSupplier, @NonNull IntSupplier minuteSupplier, @NonNull IntSupplier secondSupplier, @NonNull IntSupplier millisecondSupplier) {
		Objects.requireNonNull(hourSupplier, "Hour supplier must not be null");
		Objects.requireNonNull(minuteSupplier, "Minute supplier must not be null");
		Objects.requireNonNull(secondSupplier, "Second supplier must not be null");
		Objects.requireNonNull(millisecondSupplier, "Millisecond supplier must not be null");
		
		return new TimeFieldProvider() {
			@Override
			public @NonNull IntSupplier hour() {
				return hourSupplier;
			}
			
			@Override
			public @NonNull IntSupplier minute() {
				return minuteSupplier;
			}
			
			@Override
			public @NonNull IntSupplier second() {
				return secondSupplier;
			}
			
			@Override
			public @NonNull IntSupplier millisecond() {
				return millisecondSupplier;
			}
		};
	}
	
	/**
	 * Gets the hour of day (0-23).<br>
	 * @return An IntSupplier that provides the hour value
	 */
	@NonNull IntSupplier hour();
	
	/**
	 * Gets the minute of hour (0-59).<br>
	 * @return An IntSupplier that provides the minute value
	 */
	@NonNull IntSupplier minute();
	
	/**
	 * Gets the second of minute (0-59).<br>
	 * @return An IntSupplier that provides the second value
	 */
	@NonNull IntSupplier second();
	
	/**
	 * Gets the millisecond of second (0-999).<br>
	 * @return An IntSupplier that provides the millisecond value
	 */
	@NonNull IntSupplier millisecond();
}
