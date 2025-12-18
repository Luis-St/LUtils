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
 * A constraint interface for size-based validation of collections and maps.<br>
 * Provides methods to validate the number of elements in a collection or entries in a map.<br>
 *
 * @author Luis-St
 *
 * @param <T> The collection or map type being constrained
 * @param <C> The codec type
 */
public interface CodecSizeConstraint<T, C extends Codec<T>> {
	
	@NotNull C minSize(int minSize);
	
	@NotNull C maxSize(int maxSize);
	
	@NotNull C exactSize(int exactSize);
	
	@NotNull C sizeInRange(int minSize, int maxSize);
	
	@NotNull C empty();
	
	@NotNull C notEmpty();
	
	@NotNull C singleton();
}
