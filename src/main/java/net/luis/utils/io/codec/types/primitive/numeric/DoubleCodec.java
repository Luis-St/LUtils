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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint_new.config.numeric.DecimalConstraintConfig;
import net.luis.utils.io.codec.constraint_new.numeric.DecimalConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for doubles.<br>
 *
 * @author Luis-St
 */
public class DoubleCodec extends AbstractCodec<Double, DecimalConstraintConfig<Double>> implements DecimalConstraint<Double, DoubleCodec> {
	
	/**
	 * Creates a new double codec.<br>
	 */
	public DoubleCodec() {}
	
	/**
	 * Creates a new double codec with the specified decimal constraint configuration.<br>
	 *
	 * @param constraintConfig The decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private DoubleCodec(@NonNull DecimalConstraintConfig<Double> constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull DoubleCodec apply(@NonNull UnaryOperator<DecimalConstraintConfig<Double>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new DoubleCodec(
			configModifier.apply(this.getConstraintConfig().orElse(DecimalConstraintConfig.unconstrained()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Double value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as double using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createDouble(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Double key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(Double.toString(key));
	}
	
	@Override
	public <R> @NonNull Result<Double> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as double using '" + this + "'");
		}
		
		Result<Double> result = provider.getDouble(value);
		if (result.isError()) {
			return result;
		}
		
		Double doubleValue = result.resultOrThrow();
		Result<Void> constraintResult = this.checkConstraints(doubleValue);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(doubleValue);
	}
	
	@Override
	public @NonNull Result<Double> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Double doubleValue = Double.parseDouble(key);
			
			Result<Void> constraintResult = this.checkConstraints(doubleValue);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(doubleValue);
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as double using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedDoubleCodec[constraints=" + config + "]";
		}).orElse("DoubleCodec");
	}
}
