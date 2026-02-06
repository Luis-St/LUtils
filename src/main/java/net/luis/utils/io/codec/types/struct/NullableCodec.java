/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
 * @see Codec#nullable()
 *
 * @author Luis-St
 *
 * @param <C> The type of the value that can be null
 */
public class NullableCodec<C> extends AbstractCodec<C> {
	
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
	public NullableCodec(@NonNull Codec<C> codec) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	@Override
	public @NonNull Class<C> getType() {
		return this.codec.getType();
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return provider.createNull();
		}
		
		return this.codec.encode(provider, current, value);
	}
	
	@Override
	@SuppressWarnings({ "ReturnOfNull", "DataFlowIssue" })
	public <R> @NonNull C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return null;
		}
		
		if (provider.isNull(value)) {
			return null;
		}
		return this.codec.decode(provider, current, value);
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
