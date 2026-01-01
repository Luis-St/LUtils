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

package net.luis.utils.io.codec.types.stream;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Internal codec implementation for int streams.<br>
 * Uses a stream of boxed integers as an internal representation.<br>
 *
 * @author Luis-St
 */
public class IntStreamCodec extends AbstractCodec<IntStream, Object> {
	
	/**
	 * The internal codec that handles the conversion between a stream of boxed integers and the unboxed version.<br>
	 */
	private final Codec<Stream<Integer>> internalCodec = Codecs.INTEGER.stream();
	
	/**
	 * Constructs a new int stream codec.<br>
	 */
	public IntStreamCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable IntStream value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as int stream using '" + this + "'");
		}
		return this.internalCodec.encodeStart(provider, current, value.boxed());
	}
	
	@Override
	public <R> @NonNull Result<IntStream> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as int stream using '" + this + "'");
		}
		
		Result<Stream<Integer>> result = this.internalCodec.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		return Result.success(result.resultOrThrow().mapToInt(Integer::intValue));
	}
	
	@Override
	public String toString() {
		return "IntStreamCodec";
	}
}
