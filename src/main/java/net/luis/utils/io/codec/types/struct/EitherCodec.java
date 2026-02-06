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
import net.luis.utils.util.Either;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A codec for encoding and decoding values of type {@link Either}.<br>
 * This codec uses two other codecs to encode and decode the values of the first and second type.<br>
 * <p>
 *     The first codec is used to encode and decode the values of the first type.<br>
 *     The second codec is used to encode and decode the values of the second type.
 * </p>
 * <p>
 *     The first codec is decoded first, and if it fails, the second codec is used to decode the value.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <F> The type of the first value
 * @param <S> The type of the second value
 */
public class EitherCodec<F, S> extends AbstractCodec<Either<F, S>> {
	
	/**
	 * The codec used to encode and decode the values of the first type.<br>
	 */
	private final Codec<F> firstCodec;
	/**
	 * The codec used to encode and decode the values of the second type.<br>
	 */
	private final Codec<S> secondCodec;
	
	/**
	 * Constructs a new either codec using the given codecs for the first and second type.<br>
	 *
	 * @param firstCodec The first codec
	 * @param secondCodec The second codec
	 * @throws NullPointerException If the first or second codec is null
	 */
	public EitherCodec(@NonNull Codec<F> firstCodec, @NonNull Codec<S> secondCodec) {
		this.firstCodec = Objects.requireNonNull(firstCodec, "First codec must not be null");
		this.secondCodec = Objects.requireNonNull(secondCodec, "Second codec must not be null");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<Either<F, S>> getType() {
		return (Class<Either<F, S>>) (Class<?>) Either.class;
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Either<F, S> value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null value as either", this);
		}
		
		if (value.isLeft()) {
			return this.firstCodec.encode(provider, current, value.leftOrThrow());
		} else if (value.isRight()) {
			return this.secondCodec.encode(provider, current, value.rightOrThrow());
		} else {
			throw new EncoderException("Either value must be either left or right", this);
		}
	}
	
	@Override
	public <R> @NonNull Either<F, S> decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as either", this);
		}
		
		DecoderException left;
		try {
			return Either.left(this.firstCodec.decode(provider, current, value));
		} catch (DecoderException e) {
			left = e;
		}
		
		DecoderException right;
		try {
			return Either.right(this.secondCodec.decode(provider, current, value));
		} catch (DecoderException e) {
			right = e;
		}
		
		DecoderException e = new DecoderException("Unable to decode value as either", this);
		e.addSuppressed(right);
		e.addSuppressed(left);
		throw e;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EitherCodec<?, ?> that)) return false;
		
		if (!this.firstCodec.equals(that.firstCodec)) return false;
		return this.secondCodec.equals(that.secondCodec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.firstCodec, this.secondCodec);
	}
	
	@Override
	public String toString() {
		return "EitherCodec[" + this.firstCodec + ", " + this.secondCodec + "]";
	}
	//endregion
}
