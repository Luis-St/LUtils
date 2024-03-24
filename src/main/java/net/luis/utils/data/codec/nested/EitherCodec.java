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

package net.luis.utils.data.codec.nested;

import net.luis.utils.data.codec.Codec;
import net.luis.utils.data.codec.ParserCache;
import net.luis.utils.util.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class EitherCodec<L, R> implements NestedCodec<Either<L, R>> {
	
	private final Codec<L> leftCodec;
	private final Codec<R> rightCodec;
	
	public EitherCodec(@NotNull Codec<L> leftCodec, @NotNull Codec<R> rightCodec) {
		this.leftCodec = Objects.requireNonNull(leftCodec, "Left codec must not be null");
		this.rightCodec = Objects.requireNonNull(rightCodec, "Right codec must not be null");
	}
	
	@Override
	public <X> @NotNull ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull <X> Either<L, R> decode(@NotNull ParserCache<X> cache) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull Either<L, R> either) {
		Objects.requireNonNull(either, "Either must not be null");
		if (either.isLeft()) {
			return this.leftCodec.encode(either.leftOrThrow());
		} else if (either.isRight()) {
			return this.rightCodec.encode(either.rightOrThrow());
		}
		throw new IllegalStateException("Either is neither left nor right");
	}
	
	@Override
	public @NotNull Either<L, R> getDefaultValue() {
		throw new NoSuchMethodError("Either has no default value");
	}
}
