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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.numeric.DecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import net.luis.utils.io.codec.constraint.numeric.DecimalConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for floats.<br>
 *
 * @author Luis-St
 */
public class FloatCodec extends AbstractCodec<Float, DecimalConstraintConfig<Float>> implements DecimalConstraint<Float, FloatCodec> {
	
	/**
	 * Constructs a new float codec.<br>
	 */
	public FloatCodec() {}
	
	/**
	 * Constructs a new float codec with the specified decimal constraint configuration.<br>
	 *
	 * @param constraintConfig The decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public FloatCodec(@NonNull DecimalConstraintConfig<Float> constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull FloatCodec applyConstraint(@NonNull UnaryOperator<DecimalConstraintConfig<Float>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new FloatCodec(configModifier.apply(
			this.getConstraintConfig().orElse(DecimalConstraintConfig.unconstrained())
		));
	}
	
	@Override
	public @NonNull NumberProvider<Float> provider() {
		return NumberProvider.of(0.0F, 1.0F, 100.0F);
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Float value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(NumberType.FLOAT, value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Float value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Float value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as float using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createFloat(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Float key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(Float.toString(key));
	}
	
	@Override
	public <R> @NonNull Result<Float> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as float using '" + this + "'");
		}
		
		Result<Float> result = provider.getFloat(value);
		if (result.isError()) {
			return result;
		}
		
		Float floatValue = result.resultOrThrow();
		Result<Void> constraintResult = this.checkConstraints(floatValue);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(floatValue);
	}
	
	@Override
	public @NonNull Result<Float> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(Float.parseFloat(key));
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as float using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedFloatCodec[constraints=" + config + "]";
		}).orElse("FloatCodec");
	}
}
