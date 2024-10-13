/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.util;

import org.jetbrains.annotations.*;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a converter that can convert a value between type {@code F} and type {@code T}.<br>
 * This interface extends {@link ValueParser} and provides a method to convert a value back from type {@code T} to type {@code F}.<br>
 *
 * @author Luis-St
 *
 * @param <F> The type of the value to convert
 * @param <T> The type of the converted value
 */
public interface ValueConverter<F, T> extends ValueParser<F, T> {
	
	/**
	 * Converts the given value to a value from type {@code T} to type {@code F}.<br>
	 * @param value The value to convert
	 * @return The converted value
	 * @throws NullPointerException If the value is null (optional)
	 * @throws IllegalArgumentException If the value is invalid (optional)
	 */
	@NotNull F convert(@Nullable T value);
	
	/**
	 * Safe method to convert a value from type {@code T} to type {@code F}.<br>
	 * The method tries to convert the value and returns an {@link Optional} of the result.<br>
	 * @param value The value to convert
	 * @return An {@link Optional} of the converted value or an empty {@link Optional} if the value is invalid
	 */
	default @NotNull Optional<F> tryConvert(@Nullable T value) {
		try {
			return Optional.of(this.convert(value));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Returns the converter itself since it is already a converter.<br>
	 * @param convert The convert function
	 * @return The converter itself
	 */
	@Override
	default @NotNull ValueConverter<F, T> asConverter(@Nullable Function<T, F> convert) {
		return this;
	}
}
