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
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.numeric.IntegerConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for longs.<br>
 *
 * @author Luis-St
 */
public class LongCodec extends AbstractCodec<Long, IntegerConstraintConfig<Long>> implements IntegerConstraint<Long, LongCodec> {
	
	/**
	 * Constructs a new long codec.<br>
	 */
	public LongCodec() {}
	
	/**
	 * Constructs a new long codec with the specified integer constraint configuration.<br>
	 *
	 * @param constraintConfig The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private LongCodec(@NonNull IntegerConstraintConfig<Long> constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull LongCodec apply(@NonNull UnaryOperator<IntegerConstraintConfig<Long>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new LongCodec(
			configModifier.apply(this.getConstraintConfig().orElse(IntegerConstraintConfig.unconstrained()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Long value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as long using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createLong(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Long key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(Long.toString(key));
	}
	
	@Override
	public <R> @NonNull Result<Long> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as long using '" + this + "'");
		}
		
		Result<Long> result = provider.getLong(value);
		if (result.isError()) {
			return result;
		}
		
		Long longValue = result.resultOrThrow();
		Result<Void> constraintResult = this.checkConstraints(longValue);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(longValue);
	}
	
	@Override
	public @NonNull Result<Long> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Long longValue = Long.parseLong(key);
			
			Result<Void> constraintResult = this.checkConstraints(longValue);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(longValue);
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as long using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedLongCodec[constraints=" + config + "]";
		}).orElse("LongCodec");
	}
}
