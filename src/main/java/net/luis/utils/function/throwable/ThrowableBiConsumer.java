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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Functional interface that takes two arguments and consumes them.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object, Object)}.<br>
 * The class is equivalent to {@link BiConsumer}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The first argument type
 * @param <U> The second argument type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U, X extends Throwable> {
	
	/**
	 * Converts a throwable bi-consumer into a bi-consumer that throws a runtime exception when an exception is thrown.<br>
	 * @param consumer The throwable bi-consumer
	 * @return A caught bi-consumer
	 * @param <T> The first argument type
	 * @param <U> The second argument type
	 * @throws NullPointerException If the throwable bi-consumer is null
	 */
	static <T, U> @NotNull BiConsumer<T, U> caught(@NotNull ThrowableBiConsumer<T, U, ? extends Throwable> consumer) {
		Objects.requireNonNull(consumer, "Throwable consumer must not be null");
		return (t, u) -> {
			try {
				consumer.accept(t, u);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Functional method that takes two arguments and consumes them.<br>
	 * @param t The first argument
	 * @param u The second argument
	 * @throws X The exception that can be thrown
	 */
	void accept(T t, U u) throws X;
	
	/**
	 * Returns a composed consumer of same type that performs,<br>
	 * in sequence, this operation followed by the {@code after} operation.<br>
	 * @param after The operation to perform after this operation
	 * @return The composed throwable consumer
	 * @throws NullPointerException If the after operation is null
	 */
	default @NotNull ThrowableBiConsumer<T, U, X> andThen(@NotNull ThrowableBiConsumer<? super T, ? super U, X> after) {
		Objects.requireNonNull(after, "After operation must not be null");
		return (t, u) -> {
			this.accept(t, u);
			after.accept(t, u);
		};
	}
}
