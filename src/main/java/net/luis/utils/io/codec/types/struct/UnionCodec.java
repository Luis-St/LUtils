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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A codec that constrains values to a specific set of allowed values.
 * <p>
 *     This codec wraps another codec and validates that encoded and decoded values
 *     are within a predefined set of valid values. Any value outside this set will
 *     cause encoding or decoding to fail with a {@link Result#error(String)}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * Codec<String> statusCodec = new UnionCodec<>(
 *     Codecs.STRING,
 *     Set.of("pending", "active", "completed")
 * );
 * }</pre>
 *
 * @author Luis-St
 *
 * @param <C> The type of values handled by this codec
 */
public class UnionCodec<C> extends AbstractCodec<C, Object> {
	
	/**
	 * The codec used for encoding and decoding individual values.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The set of valid values that can be encoded or decoded.<br>
	 */
	private final Set<C> validValues;
	
	/**
	 * Constructs a new union codec with the specified codec and valid values.
	 *
	 * @param codec The codec to use for encoding and decoding individual values
	 * @param validValues The set of valid values that can be encoded or decoded
	 * @throws NullPointerException if codec or validValues is null
	 * @throws IllegalArgumentException if validValues is empty
	 */
	public UnionCodec(@NonNull Codec<C> codec, @NonNull Collection<C> validValues) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		Objects.requireNonNull(validValues, "Valid values must not be null");
		
		if (validValues.isEmpty()) {
			throw new IllegalArgumentException("Valid values must not be empty");
		}
		this.validValues = Set.copyOf(validValues);
	}
	
	@Override
	public @NonNull Class<C> getType() {
		return this.codec.getType();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as union using '" + this + "'");
		}
		
		if (!this.validValues.contains(value)) {
			return Result.error("Unable to encode value '" + value + "' as union using '" + this + "': value is not in the set of valid values " + this.validValues);
		}
		return this.codec.encodeStart(provider, current, value);
	}
	
	@Override
	public <R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to decode null value as union using '" + this + "'");
		}
		
		Result<C> result = this.codec.decodeStart(provider, current, value);
		if (result.isError()) {
			return result;
		}
		
		C decoded = result.resultOrThrow();
		if (!this.validValues.contains(decoded)) {
			return Result.error("Unable to decode value '" + decoded + "' as union using '" + this + "': value is not in the set of valid values " + this.validValues);
		}
		return result;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UnionCodec<?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return this.validValues.equals(that.validValues);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.validValues);
	}
	
	@Override
	public String toString() {
		return "UnionCodec[" + this.codec + "," + this.validValues + "]";
	}
	//endregion
}
