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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.zoned.ZoneIdConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for zone ids.<br>
 * Uses the string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ZoneIdCodec extends AbstractCodec<ZoneId, ZoneIdConstraintConfig> implements ZoneIdConstraint<ZoneIdCodec> {
	
	/**
	 * Constructs a new zone id codec.<br>
	 */
	public ZoneIdCodec() {}
	
	/**
	 * Constructs a new zone id codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private ZoneIdCodec(@NonNull ZoneIdConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull ZoneIdCodec apply(@NonNull UnaryOperator<ZoneIdConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new ZoneIdCodec(
			configModifier.apply(this.getConstraintConfig().orElse(ZoneIdConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable ZoneId value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as zone id using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.getId());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull ZoneId key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.getId());
	}
	
	@Override
	public <R> @NonNull Result<ZoneId> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as zone id using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			ZoneId zoneId = ZoneId.of(string);
			Result<Void> constraintResult = this.checkConstraints(zoneId);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			
			return Result.success(zoneId);
		} catch (ZoneRulesException e) {
			return Result.error("Unable to decode zone id '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<ZoneId> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(ZoneId.of(key));
		} catch (ZoneRulesException e) {
			return Result.error("Unable to decode key '" + key + "' as zone id using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedZoneIdCodec[constraints=" + config + "]";
		}).orElse("ZoneIdCodec");
	}
}
