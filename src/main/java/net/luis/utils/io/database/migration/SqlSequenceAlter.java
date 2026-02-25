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

package net.luis.utils.io.database.migration;

import org.jspecify.annotations.NonNull;

/**
 * Interface for altering sequence properties in migrations.<br>
 * Provides a fluent API for modifying the restart value, increment, min/max bounds, and cycling behavior of an existing sequence.<br>
 *
 * @author Luis-St
 */
public interface SqlSequenceAlter {
	
	/**
	 * Restarts the sequence with the specified value.<br>
	 *
	 * @param value The new restart value
	 * @return This alter instance for chaining
	 */
	@NonNull SqlSequenceAlter restartWith(long value);
	
	/**
	 * Changes the increment value of the sequence.<br>
	 *
	 * @param value The new increment value
	 * @return This alter instance for chaining
	 */
	@NonNull SqlSequenceAlter incrementBy(long value);
	
	/**
	 * Sets the minimum value of the sequence.<br>
	 *
	 * @param value The new minimum value
	 * @return This alter instance for chaining
	 */
	@NonNull SqlSequenceAlter setMinValue(long value);
	
	/**
	 * Sets the maximum value of the sequence.<br>
	 *
	 * @param value The new maximum value
	 * @return This alter instance for chaining
	 */
	@NonNull SqlSequenceAlter setMaxValue(long value);
	
	/**
	 * Sets whether the sequence should cycle.<br>
	 *
	 * @param cycle Whether to enable cycling
	 * @return This alter instance for chaining
	 */
	@NonNull SqlSequenceAlter setCycle(boolean cycle);
}
