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

package net.luis.utils.io.database.sequence;

import org.jspecify.annotations.NonNull;

/**
 * Interface representing SQL sequence information for schema introspection.<br>
 *
 * @author Luis-St
 */
public interface SqlSequenceInfo {
	
	/**
	 * Returns the name of the sequence.<br>
	 * @return The sequence name
	 */
	@NonNull String name();
	
	/**
	 * Returns the starting value of the sequence.<br>
	 * @return The start value
	 */
	long startWith();
	
	/**
	 * Returns the increment value of the sequence.<br>
	 * @return The increment value
	 */
	long incrementBy();
	
	/**
	 * Returns the minimum value of the sequence.<br>
	 * @return The minimum value
	 */
	long minValue();
	
	/**
	 * Returns the maximum value of the sequence.<br>
	 * @return The maximum value
	 */
	long maxValue();
	
	/**
	 * Returns the number of sequence values to cache.<br>
	 * @return The cache size
	 */
	int cache();
	
	/**
	 * Returns whether the sequence cycles when it reaches its bounds.<br>
	 * @return Whether cycling is enabled
	 */
	boolean cycle();
}
