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

package net.luis.utils.io.codec.constraint.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.CodecConstraint;
import net.luis.utils.io.codec.constraint.ComparableConstraint;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public interface NumericCodecConstraint<T extends Number & Comparable<T>, C extends Codec<T>, V> extends CodecConstraint<T, C, V>, ComparableConstraint<T, C> {
	
	@Override
	@NonNull C applyConstraint(@NonNull V config);
	
	@NotNull C positive();
	
	@NotNull C negative();
	
	@NotNull C nonNegative();
	
	@NotNull C nonPositive();
	
	@NotNull C zero();
	
	@NotNull C nonZero();
}
