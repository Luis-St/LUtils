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

package net.luis.utils.io.codec.constraint_new;

import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Functional interface for constraints where the configuration can be modified via a function.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the apply method (for fluent method chaining)
 */
@FunctionalInterface
public interface ApplicableConstraint<T, C> {
	
	/**
	 * Applies a modification to the constraint configuration and returns a new constraint instance with the updated configuration.<br>
	 *
	 * @param configModifier The function that modifies the constraint configuration
	 * @return A new constraint instance with the updated configuration
	 * @throws NullPointerException If the config modifier is null
	 */
	@NonNull C apply(@NonNull UnaryOperator<T> configModifier);
}
