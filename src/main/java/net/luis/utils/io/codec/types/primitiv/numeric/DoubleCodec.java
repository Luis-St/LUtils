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

package net.luis.utils.io.codec.types.primitiv.numeric;

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
	public DoubleCodec(@NonNull DecimalConstraintConfig<Double> constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull DoubleCodec applyConstraint(@NonNull UnaryOperator<DecimalConstraintConfig<Double>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");

		return new DoubleCodec(configModifier.apply(
			this.getConstraintConfig().orElse(DecimalConstraintConfig.unconstrained())
		));
	}

	@Override
	public @NonNull NumberProvider<Double> provider() {
		return new NumberProvider<>() {
			@Override
			public @NonNull Double zero() {
				return 0.0;
			}

			@Override
			public @NonNull Double one() {
				return 1.0;
			}

			@Override
			public @NonNull Double hundred() {
				return 100.0;
			}
		};
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Double value) {
		Objects.requireNonNull(value, "Value must not be null");

		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(NumberType.DOUBLE, value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Double value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}

		return Result.success();
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
			return Result.success(Double.parseDouble(key));
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
