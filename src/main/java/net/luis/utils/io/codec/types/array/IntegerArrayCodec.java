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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Internal codec implementation for integer arrays.<br>
 * Uses a list of integers as an internal representation.<br>
 *
 * @author Luis-St
 */
public class IntegerArrayCodec extends AbstractCodec<int[], Object> {
	
	/**
	 * The internal codec that handles the conversion between a list of integers and the array representation.<br>
	 */
	private final Codec<List<Integer>> internalCodec = Codecs.INTEGER.list();
	
	/**
	 * Constructs a new integer array codec.<br>
	 */
	public IntegerArrayCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, int @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as int array using '" + this + "'");
		}
		return this.internalCodec.encodeStart(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> @NotNull Result<int[]> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as int array using '" + this + "'");
		}
		
		Result<List<Integer>> result = this.internalCodec.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		List<Integer> list = result.resultOrThrow();
		return Result.success(ArrayUtils.toPrimitive(list.toArray(Integer[]::new)));
	}
	
	@Override
	public String toString() {
		return "IntegerArrayCodec";
	}
}
