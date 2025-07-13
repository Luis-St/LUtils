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

package net.luis.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface that takes four arguments and consumes them.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object, Object, Object, Object)}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 * @param <W> The type of the fourth argument
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
	
	/**
	 * Takes four arguments and consumes them.<br>
	 *
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @param w The fourth argument
	 */
	void accept(T t, U u, V v, W w);
	
	/**
	 * Returns a composed consumer of same type that performs,
	 * in sequence, this operation followed by the {@code after} operation.<br>
	 *
	 * @param after The operation to perform after this operation
	 * @return The composed consumer
	 * @throws NullPointerException If the after operation is null
	 */
	default @NotNull QuadConsumer<T, U, V, W> andThen(@NotNull QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
		Objects.requireNonNull(after, "After operation must not be null");
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			after.accept(t, u, v, w);
		};
	}
}
