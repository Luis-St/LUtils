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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Internal codec implementation for float arrays.<br>
 * Uses a list of floats as an internal representation.<br>
 *
 * @author Luis-St
 */
public class FloatArrayCodec extends AbstractCodec<float[], Object> {
	
	/**
	 * The internal codec that handles the conversion between a list of floats and the array representation.<br>
	 */
	private final Codec<List<Float>> internalCodec = Codecs.FLOAT.list();
	
	/**
	 * Constructs a new float array codec.<br>
	 */
	public FloatArrayCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, float @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as float array using '" + this + "'");
		}
		return this.internalCodec.encodeStart(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> @NonNull Result<float[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as float array using '" + this + "'");
		}
		
		Result<List<Float>> result = this.internalCodec.decodeStart(provider, current, value);
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
