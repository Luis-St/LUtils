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

package net.luis.utils.function.throwable;

import net.luis.utils.function.QuadConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface that takes four arguments and consumes them.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object, Object, Object, Object)}.<br>
 * The class is equivalent to {@link QuadConsumer}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The first argument type
 * @param <U> The second argument type
 * @param <V> The third argument type
 * @param <W> The fourth argument type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableQuadConsumer<T, U, V, W, X extends Throwable> {
	
	/**
	 * Converts a throwable quad-consumer into a quad-consumer that throws a runtime exception when an exception is thrown.<br>
	 *
	 * @param consumer The throwable quad-consumer
	 * @return A caught quad-consumer
	 * @param <T> The first argument type
	 * @param <U> The second argument type
	 * @param <V> The third argument type
	 * @param <W> The fourth argument type
	 * @throws NullPointerException If the throwable quad-consumer is null
	 */
	static <T, U, V, W> @NotNull QuadConsumer<T, U, V, W> caught(@NotNull ThrowableQuadConsumer<T, U, V, W, ? extends Throwable> consumer) {
		Objects.requireNonNull(consumer, "Throwable consumer must not be null");
		return (t, u, v, w) -> {
			try {
				consumer.accept(t, u, v, w);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Functional method that takes four arguments and consumes them.<br>
	 *
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @param w The fourth argument
	 * @throws X The exception that can be thrown
	 */
	void accept(T t, U u, V v, W w) throws X;
	
	/**
	 * Returns a composed consumer of same type that performs,
	 * in sequence, this operation followed by the {@code after} operation.<br>
	 *
	 * @param after The operation to perform after this operation
	 * @return The composed throwable consumer
	 * @throws NullPointerException If the after operation is null
	 */
	default @NotNull ThrowableQuadConsumer<T, U, V, W, X> andThen(@NotNull ThrowableQuadConsumer<? super T, ? super U, ? super V, ? super W, X> after) {
		Objects.requireNonNull(after, "After operation must not be null");
		return (t, u, v, w) -> {
			this.accept(t, u, v, w);
			after.accept(t, u, v, w);
		};
	}
}
