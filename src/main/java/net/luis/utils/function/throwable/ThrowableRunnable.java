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

/**
 * Functional interface that takes no arguments and performs an action.<br>
 * The {@link FunctionalInterface} method is {@link #run()}.<br>
 * The class is equivalent to {@link Runnable}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableRunnable<X extends Throwable> {

	/**
	 * Converts a throwable runnable into a runnable that throws a runtime exception when an exception is thrown.<br>
	 *
	 * @param runnable The throwable runnable
	 * @return A caught runnable
	 * @throws NullPointerException If the throwable runnable is null
	 */
	static @NotNull Runnable caught(@NotNull ThrowableRunnable<? extends Throwable> runnable) {
		Objects.requireNonNull(runnable, "Throwable runnable must not be null");
		return () -> {
			try {
				runnable.run();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * Functional method that performs an action.<br>
	 *
	 * @throws X The exception that can be thrown
	 */
	void run() throws X;

	/**
	 * Returns a composed runnable of same type that performs,
	 * in sequence, this operation followed by the {@code after} operation.<br>
	 *
	 * @param after The operation to perform after this operation
	 * @return The composed throwable runnable
	 * @throws NullPointerException If the after operation is null
	 */
	default @NotNull ThrowableRunnable<X> andThen(@NotNull ThrowableRunnable<X> after) {
		Objects.requireNonNull(after, "After operation must not be null");
		return () -> {
			this.run();
			after.run();
		};
	}
}
