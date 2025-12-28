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

import net.luis.utils.io.codec.constraint.config.temporal.core.FieldConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Set;

/**
 * A helper interface for date field constraint configuration manipulation.<br>
 * <p>
 *     This interface provides methods to apply date field constraints (day of week, day of month, month, year)
 *     to temporal constraint configurations.
 * </p>
 *
 * @author Luis-St
 *
 * @param <V> The constraint configuration type
 */
public interface DateFieldConstraintConfigProvider<V> {
	
	/**
	 * Creates a new configuration with a day of week constraint.<br>
	 *
	 * @param daysOfWeek The set of allowed days of week
	 * @return A new configuration with the day of week constraint applied
	 * @throws NullPointerException If the given set of days is null
	 */
	@NonNull V withDayOfWeek(@NonNull Set<DayOfWeek> daysOfWeek);
	
	/**
	 * Creates a new configuration with a day of month constraint.<br>
	 *
	 * @param monthConfig The day of month field constraint configuration
	 * @return A new configuration with the day of month constraint applied
	 * @throws NullPointerException If the given configuration is null
	 */
	@NonNull V withDayOfMonth(@NonNull FieldConstraintConfig monthConfig);
	
	/**
	 * Creates a new configuration with a month constraint.<br>
	 *
	 * @param months The set of allowed months
	 * @return A new configuration with the month constraint applied
	 * @throws NullPointerException If the given set of months is null
	 */
	@NonNull V withMonth(@NonNull Set<Month> months);
	
	/**
	 * Creates a new configuration with a year constraint.<br>
	 *
	 * @param yearConfig The year field constraint configuration
	 * @return A new configuration with the year constraint applied
	 * @throws NullPointerException If the given configuration is null
	 */
	@NonNull V withYear(@NonNull FieldConstraintConfig yearConfig);
}
