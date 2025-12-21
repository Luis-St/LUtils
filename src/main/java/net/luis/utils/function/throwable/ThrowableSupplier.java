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
import java.util.function.Supplier;

/**
 * Functional interface that takes no arguments and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #get()}.<br>
 * The class is equivalent to {@link Supplier}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The return type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableSupplier<T, X extends Throwable> {
	
	/**
	 * Converts a throwable supplier into a supplier that throws a runtime exception when an exception is thrown.<br>
	 *
	 * @param supplier The throwable supplier
	 * @return A caught supplier
	 * @param <T> The type of the result
	 * @throws NullPointerException If the throwable supplier is null
	 */
	static <T> @NonNull Supplier<T> caught(@NonNull ThrowableSupplier<T, ? extends Throwable> supplier) {
		Objects.requireNonNull(supplier, "Throwable supplier must not be null");
		return () -> {
			try {
				return supplier.get();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Returns the result of the supplier.<br>
	 *
	 * @return The result
	 * @throws X The exception that can be thrown
	 */
	T get() throws X;
}
