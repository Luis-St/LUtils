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
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import net.luis.utils.io.codec.constraint.numeric.IntegerConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for integers.<br>
 *
 * @author Luis-St
 */
public class IntegerCodec extends AbstractCodec<Integer, IntegerConstraintConfig<Integer>> implements IntegerConstraint<Integer, IntegerCodec> {

	/**
	 * Constructs a new integer codec.<br>
	 */
	public IntegerCodec() {}

	/**
	 * Constructs a new integer codec with the specified integer constraint configuration.<br>
	 *
	 * @param constraintConfig The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public IntegerCodec(@NonNull IntegerConstraintConfig<Integer> constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull IntegerCodec applyConstraint(@NonNull UnaryOperator<IntegerConstraintConfig<Integer>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");

		return new IntegerCodec(configModifier.apply(
			this.getConstraintConfig().orElse(IntegerConstraintConfig.unconstrained())
		));
	}

	@Override
	public @NonNull NumberProvider<Integer> provider() {
		return new NumberProvider<>() {
			@Override
			public @NonNull Integer zero() {
				return 0;
			}

			@Override
			public @NonNull Integer one() {
				return 1;
			}

			@Override
			public @NonNull Integer hundred() {
				return 100;
			}
		};
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value must not be null");

		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(NumberType.INTEGER, value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Integer value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}

		return Result.success();
	}

	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Integer value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as integer using '" + this + "'");
		}

		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return provider.createInteger(value);
	}

	@Override
	public @NonNull Result<String> encodeKey(@NonNull Integer key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(Integer.toString(key));
	}

	@Override
	public <R> @NonNull Result<Integer> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as integer using '" + this + "'");
		}

		Result<Integer> result = provider.getInteger(value);
		if (result.isError()) {
			return result;
		}

		Integer intValue = result.resultOrThrow();
		Result<Void> constraintResult = this.checkConstraints(intValue);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return Result.success(intValue);
	}

	@Override
	public @NonNull Result<Integer> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");

		try {
			return Result.success(Integer.parseInt(key));
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as integer using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedIntegerCodec[constraints=" + config + "]";
		}).orElse("IntegerCodec");
	}
}
