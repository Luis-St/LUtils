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
import net.luis.utils.io.codec.constraint.config.numeric.BigDecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import net.luis.utils.io.codec.constraint.numeric.BigDecimalConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for big decimals.<br>
 * Uses string representation for encoding and decoding.<br>
 *
 * @author Luis-St
 */
public class BigDecimalCodec extends AbstractCodec<BigDecimal, BigDecimalConstraintConfig> implements BigDecimalConstraint<BigDecimalCodec> {

	/**
	 * Constructs a new big decimal codec.<br>
	 */
	public BigDecimalCodec() {}

	/**
	 * Constructs a new big decimal codec with the specified big decimal constraint configuration.<br>
	 *
	 * @param constraintConfig The big decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public BigDecimalCodec(@NonNull BigDecimalConstraintConfig constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull BigDecimalCodec applyConstraint(@NonNull UnaryOperator<BigDecimalConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");

		return new BigDecimalCodec(configModifier.apply(
			this.getConstraintConfig().orElse(BigDecimalConstraintConfig.UNCONSTRAINED)
		));
	}

	@Override
	public @NonNull NumberProvider<BigDecimal> provider() {
		return new NumberProvider<>() {
			@Override
			public @NonNull BigDecimal zero() {
				return BigDecimal.ZERO;
			}

			@Override
			public @NonNull BigDecimal one() {
				return BigDecimal.ONE;
			}

			@Override
			public @NonNull BigDecimal hundred() {
				return BigDecimal.valueOf(100);
			}
		};
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value must not be null");

		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("BigDecimal value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}

		return Result.success();
	}

	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable BigDecimal value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as big decimal using '" + this + "'");
		}

		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return provider.createString(value.toPlainString());
	}

	@Override
	public @NonNull Result<String> encodeKey(@NonNull BigDecimal key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toPlainString());
	}

	@Override
	public <R> @NonNull Result<BigDecimal> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as big decimal using '" + this + "'");
		}

		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}

		String string = result.resultOrThrow();
		try {
			BigDecimal bigDecimal = new BigDecimal(string);

			Result<Void> constraintResult = this.checkConstraints(bigDecimal);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}

			return Result.success(bigDecimal);
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode big decimal '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public @NonNull Result<BigDecimal> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");

		try {
			return Result.success(new BigDecimal(key));
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode key '" + key + "' as big decimal using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedBigDecimalCodec[constraints=" + config + "]";
		}).orElse("BigDecimalCodec");
	}
}
