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

package net.luis.utils.io.codec.constraint.core.provider;

import net.luis.utils.io.codec.constraint.config.temporal.FieldConstraintConfig;
import org.jspecify.annotations.NonNull;

/**
 * A helper interface for time field constraint configuration manipulation.<br>
 * <p>
 *     This interface provides methods to apply time field constraints (hour, minute, second, millisecond)
 *     to temporal constraint configurations.
 * </p>
 *
 * @author Luis-St
 *
 * @param <V> The constraint configuration type
 */
public interface TimeFieldConstraintConfigProvider<V> {

	/**
	 * Creates a new configuration with an hour constraint.<br>
	 *
	 * @param hourConfig The hour field constraint configuration
	 * @return A new configuration with the hour constraint applied
	 */
	@NonNull V withHour(@NonNull FieldConstraintConfig hourConfig);

	/**
	 * Creates a new configuration with a minute constraint.<br>
	 *
	 * @param minuteConfig The minute field constraint configuration
	 * @return A new configuration with the minute constraint applied
	 */
	@NonNull V withMinute(@NonNull FieldConstraintConfig minuteConfig);

	/**
	 * Creates a new configuration with a second constraint.<br>
	 *
	 * @param secondConfig The second field constraint configuration
	 * @return A new configuration with the second constraint applied
	 */
	@NonNull V withSecond(@NonNull FieldConstraintConfig secondConfig);

	/**
	 * Creates a new configuration with a millisecond constraint.<br>
	 *
	 * @param millisecondConfig The millisecond field constraint configuration
	 * @return A new configuration with the millisecond constraint applied
	 */
	@NonNull V withMillisecond(@NonNull FieldConstraintConfig millisecondConfig);
}
