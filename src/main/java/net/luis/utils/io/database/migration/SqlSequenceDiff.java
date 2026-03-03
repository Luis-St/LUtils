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

import java.util.Optional;

/**
 * Interface describing changes applied to a sequence during a migration diff.<br>
 * Each method returns an {@link Optional} that is present only if the corresponding property was changed.<br>
 * Note: {@code startWith} is intentionally omitted — it is not an alterable property after creation.<br>
 *
 * @author Luis-St
 */
public interface SqlSequenceDiff {
	
	/**
	 * Returns the name of the sequence being changed.<br>
	 * @return The sequence name
	 */
	@NonNull String sequenceName();
	
	/**
	 * Returns the previous increment value before the change.<br>
	 * @return An optional containing the old increment value
	 */
	@NonNull Optional<Long> oldIncrementBy();
	
	/**
	 * Returns the new increment value after the change.<br>
	 * @return An optional containing the new increment value
	 */
	@NonNull Optional<Long> newIncrementBy();
	
	/**
	 * Returns the previous minimum value before the change.<br>
	 * @return An optional containing the old minimum value
	 */
	@NonNull Optional<Long> oldMinValue();
	
	/**
	 * Returns the new minimum value after the change.<br>
	 * @return An optional containing the new minimum value
	 */
	@NonNull Optional<Long> newMinValue();
	
	/**
	 * Returns the previous maximum value before the change.<br>
	 * @return An optional containing the old maximum value
	 */
	@NonNull Optional<Long> oldMaxValue();
	
	/**
	 * Returns the new maximum value after the change.<br>
	 * @return An optional containing the new maximum value
	 */
	@NonNull Optional<Long> newMaxValue();
	
	/**
	 * Returns the previous cache size before the change.<br>
	 * @return An optional containing the old cache size
	 */
	@NonNull Optional<Integer> oldCache();
	
	/**
	 * Returns the new cache size after the change.<br>
	 * @return An optional containing the new cache size
	 */
	@NonNull Optional<Integer> newCache();
	
	/**
	 * Returns the previous cycle setting before the change.<br>
	 * @return An optional containing the old cycle flag
	 */
	@NonNull Optional<Boolean> oldCycle();
	
	/**
	 * Returns the new cycle setting after the change.<br>
	 * @return An optional containing the new cycle flag
	 */
	@NonNull Optional<Boolean> newCycle();
}
