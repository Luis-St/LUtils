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
 * Builder for sequence definitions in migrations.<br>
 * Provides a fluent API for configuring sequence properties such as start value, increment, min/max bounds, and cycling behavior.<br>
 *
 * @author Luis-St
 */
public interface SqlSequenceBuilder {
	
	/**
	 * Sets the starting value of the sequence.<br>
	 *
	 * @param value The start value
	 * @return This builder for chaining
	 */
	@NonNull SqlSequenceBuilder startWith(long value);
	
	/**
	 * Sets the increment value of the sequence.<br>
	 *
	 * @param value The increment value
	 * @return This builder for chaining
	 */
	@NonNull SqlSequenceBuilder incrementBy(long value);
	
	/**
	 * Sets the minimum value of the sequence.<br>
	 *
	 * @param value The minimum value
	 * @return This builder for chaining
	 */
	@NonNull SqlSequenceBuilder minValue(long value);
	
	/**
	 * Sets the maximum value of the sequence.<br>
	 *
	 * @param value The maximum value
	 * @return This builder for chaining
	 */
	@NonNull SqlSequenceBuilder maxValue(long value);
	
	/**
	 * Enables cycling for the sequence.<br>
	 * When the sequence reaches its maximum (or minimum) value, it wraps around.<br>
	 *
	 * @return This builder for chaining
	 */
	@NonNull SqlSequenceBuilder cycle();
}
