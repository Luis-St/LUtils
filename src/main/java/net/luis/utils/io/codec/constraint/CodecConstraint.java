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

import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * A functional interface for applying constraints to codecs.<br>
 * Provides a method to apply a constraint configuration and return a constrained codec.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The return type of the constraint method (most likely a Codec)
 * @param <V> The type of the constraint configuration
 */
@FunctionalInterface
public interface CodecConstraint<T, C, V> {
	
	/**
	 * Applies the given constraint configuration and returns a constrained codec.<br>
	 *
	 * @param configModifier Applies modifications to the current constraint configuration
	 * @return The constrained codec
	 * @throws NullPointerException If the constraint configuration is null
	 */
	@NonNull C applyConstraint(@NonNull UnaryOperator<V> configModifier);
}
