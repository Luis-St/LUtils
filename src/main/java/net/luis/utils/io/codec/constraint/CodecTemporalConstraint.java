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

package net.luis.utils.io.codec.constraint;

import net.luis.utils.io.codec.Codec;
import org.jetbrains.annotations.NotNull;

/**
 * A constraint interface for temporal types (dates, times, durations, etc.).<br>
 * Provides methods to validate temporal values against various time-based constraints.<br>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 */
public interface CodecTemporalConstraint<T, C extends Codec<T>> {
	
	@NotNull C after(@NotNull T minimum);
	
	@NotNull C afterOrEqual(@NotNull T minimum);
	
	@NotNull C before(@NotNull T maximum);
	
	@NotNull C beforeOrEqual(@NotNull T maximum);
	
	@NotNull C inRange(@NotNull T minimum, @NotNull T maximum);
	
	@NotNull C past();
	
	@NotNull C future();
	
	@NotNull C now();
}
