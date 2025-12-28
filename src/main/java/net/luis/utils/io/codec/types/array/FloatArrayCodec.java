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
import net.luis.utils.io.codec.constraint.LengthConstraint;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for float arrays.<br>
 * Uses a list of floats as an internal representation.<br>
 *
 * @author Luis-St
 */
public class FloatArrayCodec extends AbstractCodec<float[], LengthConstraintConfig> implements LengthConstraint<float[], FloatArrayCodec> {
	
	/**
	 * The internal codec that handles the conversion between a list of floats and the array representation.<br>
	 */
	private final Codec<List<Float>> internalCodec = Codecs.FLOAT.list();
	
	/**
	 * Constructs a new float array codec.<br>
	 */
	public FloatArrayCodec() {}
	
	/**
	 * Constructs a new float array codec with the specified length constraint configuration.<br>
	 *
	 * @param constraintConfig The length constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public FloatArrayCodec(@NonNull LengthConstraintConfig constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull FloatArrayCodec applyConstraint(@NonNull UnaryOperator<LengthConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new FloatArrayCodec(configModifier.apply(
			this.getConstraintConfig().orElse(LengthConstraintConfig.UNCONSTRAINED)
		));
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(float @NonNull [] value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.length)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Float array " + Arrays.toString(value) + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, float @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as float array using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
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
		float[] array = ArrayUtils.toPrimitive(list.toArray(Float[]::new));
		
		Result<Void> constraintResult = this.checkConstraints(array);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(array);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedFloatArrayCodec[constraints=" + config + "]";
		}).orElse("FloatArrayCodec");
	}
}
