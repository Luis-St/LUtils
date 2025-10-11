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

package net.luis.utils.io.codec.internal.array;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Objects;

/**
 * Internal codec implementation for float arrays.<br>
 * Uses a list of floats as an internal representation.<br>
 *
 * @author Luis-St
 */
@ApiStatus.Internal
public class FloatArrayCodec implements Codec<float[]> {
	
	/**
	 * The internal codec that handles the conversion between a list of floats and the array representation.<br>
	 */
	private final Codec<List<Float>> internalCodec = Codec.list(Codecs.FLOAT);
	
	/**
	 * Constructs a new float array codec.<br>
	 */
	public FloatArrayCodec() {}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, float @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as float array using '" + this + "'");
		}
		return this.internalCodec.encodeStart(provider, current, Lists.newArrayList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public @NotNull <R> Result<float[]> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as float array using '" + this + "'");
		}
		
		Result<List<Float>> result = this.internalCodec.decodeStart(provider, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		List<Float> list = result.resultOrThrow();
		return Result.success(ArrayUtils.toPrimitive(list.toArray(Float[]::new)));
	}
	
	@Override
	public String toString() {
		return "FloatArrayCodec";
	}
}
