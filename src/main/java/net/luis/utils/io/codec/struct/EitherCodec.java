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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Either;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.*;

import java.util.Objects;

/**
 * A codec for encoding and decoding values of type {@link Either}.<br>
 * This codec uses two other codecs to encode and decode the values of the first and second type.<br>
 * <p>
 *     The first codec is used to encode and decode the values of the first type.<br>
 *     The second codec is used to encode and decode the values of the second type.
 * </p>
 * <p>
 *     The first codec is decoded first and if it fails, the second codec is used to decode the value.<br>
 *     <strong>Note</strong>: If the first codec is a string codec, it will always succeed, so the second codec will never be used.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <F> The type of the first value
 * @param <S> The type of the second value
 */
public class EitherCodec<F, S> implements Codec<Either<F, S>> {
	
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
	 * Do not use this constructor directly, use any of the either factory methods in {@link Codec} instead.<br>
	 * @param firstCodec The first codec
	 * @param secondCodec The second codec
	 * @throws NullPointerException If the first or second codec is null
	 */
	@ApiStatus.Internal
	public EitherCodec(@NotNull Codec<F> firstCodec, @NotNull Codec<S> secondCodec) {
		this.firstCodec = Objects.requireNonNull(firstCodec, "First codec must not be null");
		this.secondCodec = Objects.requireNonNull(secondCodec, "Second codec must not be null");
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Either<F, S> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value as either using '" + this + "'");
		}
		return value.mapTo(
			first -> this.firstCodec.encodeStart(provider, current, first),
			second -> this.secondCodec.encodeStart(provider, current, second)
		);
	}
	
	@Override
	public <R> @NotNull Result<Either<F, S>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as either");
		}
		Result<F> firstResult = this.firstCodec.decodeStart(provider, value);
		Result<S> secondResult = this.secondCodec.decodeStart(provider, value);
		if (firstResult.isError() && secondResult.isError()) {
			return Result.error("Unable to decode value as either using '" + this + "': \n" + firstResult.errorOrThrow() + "\n" + secondResult.errorOrThrow());
		}
		if (firstResult.isSuccess()) {
			return firstResult.map(Either::left);
		}
		return secondResult.map(Either::right);
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
