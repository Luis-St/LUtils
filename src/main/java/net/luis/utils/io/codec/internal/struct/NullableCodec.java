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

package net.luis.utils.io.codec.internal.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.Objects;

/**
 * A codec for encoding and decoding nullable values.<br>
 * This codec wraps another codec and allows encoding and decoding of null values.<br>
 * <p>
 *     If the value to encode is null, the codec will create a null value using the type provider.<br>
 *     If the value to decode is null or represents a null value in the provider, the codec will return null.
 * </p>
 * <p>
 *     This codec is used to handle null values in a type-safe manner.<br>
 *     It ensures that null values are properly encoded and decoded according to the type provider.
 * </p>
 *
 * @see Codec#nullable(Codec)
 *
 * @author Luis-St
 *
 * @param <C> The type of the value that can be null
 */
@ApiStatus.Internal
public class NullableCodec<C> implements Codec<C> {
	
	/**
	 * The codec used to encode and decode non-null values.<br>
	 */
	private final Codec<C> codec;
	
	/**
	 * Constructs a new nullable codec using the given codec.<br>
	 *
	 * @param codec The codec for non-null values
	 * @throws NullPointerException If the codec is null
	 */
	public NullableCodec(@NotNull Codec<C> codec) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return provider.createNull();
		}
		return this.codec.encodeStart(provider, current, value);
	}
	
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.success(null);
		}
		
		Result<Boolean> isNull = provider.isNull(value);
		if (isNull.isSuccess() && isNull.resultOrThrow()) {
			return Result.success(null);
		}
		
		return this.codec.decodeStart(provider, value);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NullableCodec<?> that)) return false;
		return this.codec.equals(that.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return "NullableCodec[" + this.codec + "]";
	}
	//endregion
}
