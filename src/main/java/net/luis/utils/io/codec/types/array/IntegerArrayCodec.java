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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint_new.ArrayConstraint;
import net.luis.utils.io.codec.constraint_new.config.ArrayConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for integer arrays.<br>
 * Uses a list of integers as an internal representation.<br>
 *
 * @author Luis-St
 */
public class IntegerArrayCodec extends AbstractCodec<int[], ArrayConstraintConfig<int[]>> implements ArrayConstraint<int[], IntegerArrayCodec> {
	
	/**
	 * The internal codec that handles the conversion between a list of integers and the array representation.<br>
	 */
	private final Codec<List<Integer>> internalCodec = Codecs.INTEGER.list();
	
	/**
	 * Constructs a new integer array codec.<br>
	 */
	public IntegerArrayCodec() {}
	
	/**
	 * Constructs a new integer array codec with the specified length constraint configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private IntegerArrayCodec(@NonNull ArrayConstraintConfig<int[]> config) {
		super(config);
	}
	
	@Override
	public @NonNull IntegerArrayCodec apply(@NonNull UnaryOperator<ArrayConstraintConfig<int[]>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new IntegerArrayCodec(
			configModifier.apply(this.getConstraintConfig().orElse(ArrayConstraintConfig.unconstrained()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, int @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as int array using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return this.internalCodec.encodeStart(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}
	
	@Override
	public <R> @NonNull Result<int[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
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
		int[] array = ArrayUtils.toPrimitive(list.toArray(Integer[]::new));
		
		Result<Void> constraintResult = this.checkConstraints(array);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(array);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedIntegerArrayCodec[constraints=" + config + "]";
		}).orElse("IntegerArrayCodec");
	}
}
