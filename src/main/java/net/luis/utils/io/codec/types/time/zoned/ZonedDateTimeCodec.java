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

package net.luis.utils.io.codec.types.time.zoned;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.temporal.ZonedDateTimeConstraintConfig;
import net.luis.utils.io.codec.constraint.temporal.DateFieldConstraint;
import net.luis.utils.io.codec.constraint.temporal.TemporalSpanConstraint;
import net.luis.utils.io.codec.constraint.temporal.TimeFieldConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for zoned date times.<br>
 * <p>
 *     Uses the ISO-8601 string format as an internal representation.<br>
 *     Supports temporal constraints including comparison, span, time field, and date field constraints.
 * </p>
 *
 * @author Luis-St
 */
public class ZonedDateTimeCodec extends AbstractCodec<ZonedDateTime, ZonedDateTimeConstraintConfig> implements TemporalSpanConstraint<ZonedDateTime, ZonedDateTimeCodec, ZonedDateTimeConstraintConfig>, TimeFieldConstraint<ZonedDateTimeCodec, ZonedDateTimeConstraintConfig>, DateFieldConstraint<ZonedDateTimeCodec, ZonedDateTimeConstraintConfig> {

	/**
	 * Constructs a new zoned date time codec.<br>
	 */
	public ZonedDateTimeCodec() {}

	/**
	 * Constructs a new zoned date time codec with the specified constraint configuration.<br>
	 *
	 * @param constraintConfig The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	public ZonedDateTimeCodec(@NonNull ZonedDateTimeConstraintConfig constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull ZonedDateTimeCodec applyConstraint(@NonNull UnaryOperator<ZonedDateTimeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		return new ZonedDateTimeCodec(configModifier.apply(
			this.getConstraintConfig().orElse(ZonedDateTimeConstraintConfig.UNCONSTRAINED)
		));
	}

	@Override
	public @NonNull ZonedDateTimeCodec applyTimeFieldConstraint(@NonNull UnaryOperator<ZonedDateTimeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		return new ZonedDateTimeCodec(configModifier.apply(
			this.getConstraintConfig().orElse(ZonedDateTimeConstraintConfig.UNCONSTRAINED)
		));
	}

	@Override
	public @NonNull ZonedDateTimeCodec applyDateFieldConstraint(@NonNull UnaryOperator<ZonedDateTimeConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		return new ZonedDateTimeCodec(configModifier.apply(
			this.getConstraintConfig().orElse(ZonedDateTimeConstraintConfig.UNCONSTRAINED)
		));
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull ZonedDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");

		Result<Void> constraintResult = this.getConstraintConfig()
			.map(config -> config.matches(value))
			.orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("ZonedDateTime value " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}

		return Result.success();
	}

	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable ZonedDateTime value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");

		if (value == null) {
			return Result.error("Unable to encode null as zoned date time using '" + this + "'");
		}

		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return provider.createString(value.toString());
	}

	@Override
	public @NonNull Result<String> encodeKey(@NonNull ZonedDateTime key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toString());
	}

	@Override
	public <R> @NonNull Result<ZonedDateTime> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as zoned date time using '" + this + "'");
		}

		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}

		String string = result.resultOrThrow();
		try {
			ZonedDateTime dateTime = ZonedDateTime.parse(string);

			Result<Void> constraintResult = this.checkConstraints(dateTime);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}

			return Result.success(dateTime);
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode zoned date time '" + string + "' using '" + this + "': Unable to parse zoned date time: " + e.getMessage());
		}
	}

	@Override
	public @NonNull Result<ZonedDateTime> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		try {
			return Result.success(ZonedDateTime.parse(key));
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode key '" + key + "' as zoned date time using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedZonedDateTimeCodec[constraints=" + config + "]";
		}).orElse("ZonedDateTimeCodec");
	}
}
