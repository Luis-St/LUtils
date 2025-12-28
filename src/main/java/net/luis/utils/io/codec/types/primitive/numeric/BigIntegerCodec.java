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
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import net.luis.utils.io.codec.constraint.numeric.IntegerConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for big integers.<br>
 * Uses string representation for encoding and decoding.<br>
 *
 * @author Luis-St
 */
public class BigIntegerCodec extends AbstractCodec<BigInteger, IntegerConstraintConfig<BigInteger>> implements IntegerConstraint<BigInteger, BigIntegerCodec> {

	/**
	 * Constructs a new big integer codec.<br>
	 */
	public BigIntegerCodec() {}

	/**
	 * Constructs a new big integer codec with the specified integer constraint configuration.<br>
	 *
	 * @param constraintConfig The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public BigIntegerCodec(@NonNull IntegerConstraintConfig<BigInteger> constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull BigIntegerCodec applyConstraint(@NonNull UnaryOperator<IntegerConstraintConfig<BigInteger>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");

		return new BigIntegerCodec(configModifier.apply(
			this.getConstraintConfig().orElse(IntegerConstraintConfig.unconstrained())
		));
	}

	@Override
	public @NonNull NumberProvider<BigInteger> provider() {
		return NumberProvider.of(BigInteger.ZERO, BigInteger.ONE, BigInteger.valueOf(100));
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull BigInteger value) {
		Objects.requireNonNull(value, "Value must not be null");

		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(NumberType.BIG_INTEGER, value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("BigInteger value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}

	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable BigInteger value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as big integer using '" + this + "'");
		}

		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toString());
	}

	@Override
	public @NonNull Result<String> encodeKey(@NonNull BigInteger key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toString());
	}

	@Override
	public <R> @NonNull Result<BigInteger> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as big integer using '" + this + "'");
		}

		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}

		String string = result.resultOrThrow();
		try {
			BigInteger bigInteger = new BigInteger(string);

			Result<Void> constraintResult = this.checkConstraints(bigInteger);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(bigInteger);
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode big integer '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public @NonNull Result<BigInteger> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");

		try {
			return Result.success(new BigInteger(key));
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode key '" + key + "' as big integer using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedBigIntegerCodec[constraints=" + config + "]";
		}).orElse("BigIntegerCodec");
	}
}
