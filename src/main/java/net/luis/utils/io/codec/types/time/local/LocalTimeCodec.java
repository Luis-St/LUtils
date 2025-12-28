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

package net.luis.utils.io.codec.types.time.local;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.temporal.LocalTimeConstraintConfig;
import net.luis.utils.io.codec.constraint.temporal.TemporalSpanConstraint;
import net.luis.utils.io.codec.constraint.temporal.TimeFieldConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for local times.<br>
 * <p>
 *     Uses the ISO-8601 string format as an internal representation.<br>
 *     Supports temporal constraints including comparison, span, and time field constraints.
 * </p>
 *
 * @author Luis-St
 */
public class LocalTimeCodec extends AbstractCodec<LocalTime, LocalTimeConstraintConfig> implements TemporalSpanConstraint<LocalTime, LocalTimeCodec, LocalTimeConstraintConfig>, TimeFieldConstraint<LocalTimeCodec, LocalTimeConstraintConfig> {
	
	/**
	 * Constructs a new local time codec.<br>
	 */
	public LocalTimeCodec() {}
	
	/**
	 * Constructs a new local time codec with the specified constraint configuration.<br>
	 *
	 * @param constraintConfig The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public LocalTimeCodec(@NonNull LocalTimeConstraintConfig constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull LocalTimeCodec applyConstraint(@NonNull UnaryOperator<LocalTimeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new LocalTimeCodec(configModifier.apply(
			this.getConstraintConfig().orElse(LocalTimeConstraintConfig.UNCONSTRAINED)
		));
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull LocalTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("LocalTime value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable LocalTime value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as local time using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toString());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull LocalTime key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toString());
	}
	
	@Override
	public <R> @NonNull Result<LocalTime> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as local time using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			LocalTime time = LocalTime.parse(string);
			
			Result<Void> constraintResult = this.checkConstraints(time);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(time);
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode local time '" + string + "' using '" + this + "': Unable to parse local time: " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<LocalTime> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(LocalTime.parse(key));
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode key '" + key + "' as local time using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedLocalTimeCodec[constraints=" + config + "]";
		}).orElse("LocalTimeCodec");
	}
}
