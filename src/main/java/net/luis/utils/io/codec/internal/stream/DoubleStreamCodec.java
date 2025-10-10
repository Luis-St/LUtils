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

package net.luis.utils.io.codec.internal.stream;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Internal codec implementation for double streams.<br>
 * Uses a stream of boxed doubles as an internal representation.<br>
 *
 * @author Luis-St
 */
@ApiStatus.Internal
public class DoubleStreamCodec implements Codec<DoubleStream> {
	
	/**
	 * The internal codec that handles the conversion between a stream of boxed doubles and the unboxed version.<br>
	 */
	private final Codec<Stream<Double>> internalCodec = Codec.stream(Codecs.DOUBLE);
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable DoubleStream value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as double stream using '" + this + "'");
		}
		return this.internalCodec.encodeStart(provider, current, value.boxed());
	}
	
	@Override
	public @NotNull <R> Result<DoubleStream> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as double stream using '" + this + "'");
		}
		
		Result<Stream<Double>> result = this.internalCodec.decodeStart(provider, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		return Result.success(result.resultOrThrow().mapToDouble(Double::doubleValue));
	}
	
	@Override
	public String toString() {
		return "DoubleStreamCodec";
	}
}
