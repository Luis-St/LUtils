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

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Functional interface that takes one argument and consumes it.<br>
 * The {@link FunctionalInterface} method is {@link #accept(Object)}.<br>
 * The class is equivalent to {@link Consumer}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The argument type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableConsumer<T, X extends Throwable> {
	
	/**
	 * Converts a throwable consumer into a consumer that throws a runtime exception when an exception is thrown.<br>
	 *
	 * @param consumer The throwable consumer
	 * @return A caught consumer
	 * @param <T> The argument type
	 * @throws NullPointerException If the throwable consumer is null
	 */
	static <T> @NonNull Consumer<T> caught(@NonNull ThrowableConsumer<T, ? extends Throwable> consumer) {
		Objects.requireNonNull(consumer, "Throwable consumer must not be null");
		return (t) -> {
			try {
				consumer.accept(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Functional method that takes one argument and consumes it.<br>
	 *
	 * @param t The argument
	 * @throws X The exception that can be thrown
	 */
	void accept(T t) throws X;
	
	/**
	 * Returns a composed consumer of same type that performs,
	 * in sequence, this operation followed by the {@code after} operation.<br>
	 *
	 * @param after The operation to perform after this operation
	 * @return The composed throwable consumer
	 * @throws NullPointerException If the after operation is null
	 */
	default @NonNull ThrowableConsumer<T, X> andThen(@NonNull ThrowableConsumer<? super T, X> after) {
		Objects.requireNonNull(after, "After operation must not be null");
		return (t) -> {
			this.accept(t);
			after.accept(t);
		};
	}
}
