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
import net.luis.utils.io.codec.constraint.config.temporal.YearConstraintConfig;
import net.luis.utils.io.codec.constraint.temporal.TemporalComparisonConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for years.<br>
 * <p>
 *     Uses the ISO-8601 integer format as an internal representation.<br>
 *     Supports temporal comparison constraints.
 * </p>
 *
 * @author Luis-St
 */
public class YearCodec extends AbstractCodec<Year, YearConstraintConfig> implements TemporalComparisonConstraint<Year, YearCodec, YearConstraintConfig> {
	
	/**
	 * Constructs a new year codec.<br>
	 */
	public YearCodec() {}
	
	/**
	 * Constructs a new year codec with the specified constraint configuration.<br>
	 *
	 * @param constraintConfig The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public YearCodec(@NonNull YearConstraintConfig constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull YearCodec applyConstraint(@NonNull UnaryOperator<YearConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new YearCodec(configModifier.apply(
			this.getConstraintConfig().orElse(YearConstraintConfig.UNCONSTRAINED)
		));
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull Year value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Year value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Year value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as year using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createInteger(value.getValue());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Year key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(String.valueOf(key.getValue()));
	}
	
	@Override
	public <R> @NonNull Result<Year> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as year using '" + this + "'");
		}
		
		Result<Integer> result = provider.getInteger(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		int yearValue = result.resultOrThrow();
		try {
			Year year = Year.of(yearValue);
			
			Result<Void> constraintResult = this.checkConstraints(year);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			
			return Result.success(year);
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode year '" + yearValue + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<Year> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(Year.of(Integer.parseInt(key)));
		} catch (NumberFormatException | DateTimeParseException e) {
			return Result.error("Unable to decode key '" + key + "' as year using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedYearCodec[constraints=" + config + "]";
		}).orElse("YearCodec");
	}
}
