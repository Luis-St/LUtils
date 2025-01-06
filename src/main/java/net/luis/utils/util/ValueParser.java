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

package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a parser that can parse a value of type {@code F} to a value of type {@code T}.<br>
 * This is a functional interface whose functional method is {@link #parse(Object)}.<br>
 * This interface also provides a method to try to parse a value and return an {@link Optional} of the result.<br>
 *
 * @author Luis-St
 *
 * @param <F> The type of the value to parse (from)
 * @param <T> The type of the parsed value (to)
 */
@FunctionalInterface
public interface ValueParser<F, T> {
	
	/**
	 * Parses the given value to a value from type {@code F} to type {@code T}.<br>
	 * @param value The value to parse
	 * @return The parsed value
	 * @throws NullPointerException If the value is null (optional)
	 * @throws IllegalArgumentException If the value is invalid (optional)
	 */
	@NotNull T parse(@Nullable F value);
	
	/**
	 * Safe method to parse a value from type {@code F} to type {@code T}.<br>
	 * The method tries to parse the value and returns an {@link Optional} of the result.<br>
	 * @param value The value to parse
	 * @return An {@link Optional} of the parsed value or an empty {@link Optional} if the value is invalid
	 */
	default @NotNull Optional<T> tryParse(@Nullable F value) {
		try {
			return Optional.of(this.parse(value));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Returns a {@link ValueConverter} of this parser with the given convert function.<br>
	 * The convert function is used to convert a value of type {@code T} to a value of type {@code F}.<br>
	 * @param convert The convert function
	 * @return A {@link ValueConverter} of this parser with the given convert function
	 * @throws NullPointerException If the convert function is null
	 */
	default @NotNull ValueConverter<F, T> asConverter(@NotNull Function<T, F> convert) {
		Objects.requireNonNull(convert, "Convert must not be null");
		return new ValueConverter<>() {
			
			@Override
			public @NotNull F convert(@Nullable T value) {
				return convert.apply(value);
			}
			
			@Override
			public @NotNull T parse(@Nullable F value) {
				return ValueParser.this.parse(value);
			}
		};
	}
}
