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

package net.luis.utils.data.codec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;

/**
 *
 * @author Luis-St
 *
 */

public interface ParserCache<T> extends Supplier<T> {
	
	//region Factory methods
	static <X> @NotNull ParserCache<X> invalid(@NotNull String message) {
		return new InvalidParser<>(message);
	}
	
	static <T> @NotNull ParserCache<T> of(@NotNull T value) {
		return new ValueParser<>(value);
	}
	
	@SuppressWarnings("unchecked")
	static <T, X> @NotNull ParserCache<X> cast(@NotNull T value) {
		return (ParserCache<X>) new ValueParser<>(value);
	}
	//endregion
	
	boolean isValid();
	
	@NotNull T get();
	
	@SuppressWarnings("unchecked")
	default <X> @NotNull X cast() {
		return (X) this.get();
	}
	
	//region Invalid implementation
	final class InvalidParser<T> implements ParserCache<T> {
		
		private final String message;
		
		private InvalidParser(@NotNull String message) {
			this.message = Objects.requireNonNull(message, "Message must not be null");
		}
		
		@Override
		public boolean isValid() {
			return false;
		}
		
		@Override
		public @NotNull T get() {
			throw new NoSuchElementException("No value present");
		}
		
		//region Object overrides
		@Override
		public String toString() {
			return this.message;
		}
		//endregion
	}
	//endregion
	
	//region Value implementation
	final class ValueParser<T> implements ParserCache<T> {
		
		private final T value;
		
		private ValueParser(@NotNull T value) {
			this.value = Objects.requireNonNull(value, "Value must not be null");
		}
		
		@Override
		public boolean isValid() {
			return true;
		}
		
		@Override
		public @NotNull T get() {
			return this.value;
		}
		
		//region Object overrides
		@Override
		public String toString() {
			return "Value ParserCache: " + this.value;
		}
		//endregion
	}
	//endregion
}
