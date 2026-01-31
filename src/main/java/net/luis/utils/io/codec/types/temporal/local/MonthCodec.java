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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.core.EnumConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Month;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for month.<br>
 * Uses the string name as an internal representation.<br>
 *
 * @author Luis-St
 */
public class MonthCodec extends AbstractCodec<Month, EnumConstraintConfig<Month>> implements EnumConstraint<Month, MonthCodec> {
	
	/**
	 * Constructs a new month codec.<br>
	 */
	public MonthCodec() {}
	
	/**
	 * Constructs a new month codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private MonthCodec(@NonNull EnumConstraintConfig<Month> config) {
		super(config);
	}
	
	@Override
	public @NonNull MonthCodec apply(@NonNull UnaryOperator<EnumConstraintConfig<Month>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new MonthCodec(
			configModifier.apply(this.getConstraintConfig().orElse(EnumConstraintConfig.unconstrained()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Month value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as month using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.name());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Month key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.name());
	}
	
	@Override
	public <R> @NonNull Result<Month> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as month using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			Month month = Month.valueOf(string);
			
			Result<Void> constraintResult = this.checkConstraints(month);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(month);
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode month '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<Month> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Month month = Month.valueOf(key);
			
			Result<Void> constraintResult = this.checkConstraints(month);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(month);
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode key '" + key + "' as month using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedMonthCodec[constraints=" + config + "]";
		}).orElse("MonthCodec");
	}
}
