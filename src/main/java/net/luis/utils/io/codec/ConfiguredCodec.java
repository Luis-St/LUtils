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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * A codec that is configured to encode and decode a specific component of an object.<br>
 * This codec uses another codec to encode and decode the component.<br>
 * The underlying codec is expected to be an instance of {@link NamedCodec}.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type of the component
 * @param <O> The type of the object
 */
public class ConfiguredCodec<C, O> {
	
	/**
	 * The codec used to encode and decode the component.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The function used to retrieve the component from the object.<br>
	 */
	private final Function<O, C> getter;
	
	/**
	 * Constructs a new configured codec using the given codec and getter for the component.<br>
	 *
	 * @param codec The codec
	 * @param getter The getter
	 * @throws NullPointerException If the codec or getter is null
	 */
	ConfiguredCodec(@NotNull Codec<C> codec, @NotNull Function<O, C> getter) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.getter = Objects.requireNonNull(getter, "Getter must not be null");
	}
	
	/**
	 * Encodes the component of the object using the given type provider and current value.<br>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param object The object
	 * @return The result of the encoding operation
	 * @param <R> The type of the current value
	 * @throws NullPointerException If the type provider or current value is null
	 */
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O object) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (object == null) {
			return Result.error("Unable to encode component because the component can not be retrieved from a null object");
		}
		return this.codec.encodeStart(provider, current, this.getter.apply(object));
	}
	
	/**
	 * Decodes the component of the object using the given type provider and value.<br>
	 * This method will simply pass through the value to the underlying codec.<br>
	 *
	 * @param provider The type provider
	 * @param value The value
	 * @return The result of the decoding operation
	 * @param <R> The type of the value
	 * @throws NullPointerException If the type provider is null
	 */
	public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.codec.decodeStart(provider, value);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ConfiguredCodec<?, ?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return Objects.equals(this.getter, that.getter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return "ConfigurableCodec[" + this.codec + "]";
	}
	//endregion
}
