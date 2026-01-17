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
import net.luis.utils.io.codec.constraint_new.config.temporal.LocalTimeConstraintConfig;
import net.luis.utils.io.codec.constraint_new.temporal.LocalTimeConstraint;
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
public class LocalTimeCodec extends AbstractCodec<LocalTime, LocalTimeConstraintConfig> implements LocalTimeConstraint<LocalTimeCodec> {
	
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
	private LocalTimeCodec(@NonNull LocalTimeConstraintConfig constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull LocalTimeCodec apply(@NonNull UnaryOperator<LocalTimeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new LocalTimeCodec(
			configModifier.apply(this.getConstraintConfig().orElse(LocalTimeConstraintConfig.UNCONSTRAINED))
		);
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
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
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
			LocalTime time = LocalTime.parse(key);
			
			Result<Void> constraintResult = this.checkConstraints(time);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(time);
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
