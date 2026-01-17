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

package net.luis.utils.io.codec.types;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint_new.UUIDConstraint;
import net.luis.utils.io.codec.constraint_new.config.UUIDConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for UUIDs.<br>
 * Uses the standard string representation as an internal representation.<br>
 *
 * @author Luis-St
 */
public class UUIDCodec extends AbstractCodec<UUID, UUIDConstraintConfig> implements UUIDConstraint<UUIDCodec> {
	
	/**
	 * Constructs a new UUID codec.<br>
	 */
	public UUIDCodec() {}
	
	/**
	 * Constructs a new UUID codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private UUIDCodec(@NonNull UUIDConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull UUIDCodec apply(@NonNull UnaryOperator<UUIDConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new UUIDCodec(
			configModifier.apply(this.getConstraintConfig().orElse(UUIDConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable UUID value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as UUID using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createString(value.toString());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull UUID key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(key.toString());
	}
	
	@Override
	public <R> @NonNull Result<UUID> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as UUID using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			UUID uuid = UUID.fromString(string);
			
			Result<Void> constraintResult = this.checkConstraints(uuid);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(uuid);
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode UUID '" + string + "' using '" + this + "': Unable to parse UUID: " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<UUID> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			UUID uuid = UUID.fromString(key);
			
			Result<Void> constraintResult = this.checkConstraints(uuid);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(uuid);
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode UUID from key '" + key + "' using'" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedUUIDCodec[constraints=" + config + "]";
		}).orElse("UUIDCodec");
	}
}
