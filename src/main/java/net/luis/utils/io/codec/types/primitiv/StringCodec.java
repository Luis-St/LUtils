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

package net.luis.utils.io.codec.types.primitiv;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint.LengthConstraint;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for strings.<br>
 *
 * @author Luis-St
 */
public class StringCodec extends AbstractCodec<String, LengthConstraintConfig> implements LengthConstraint<String, StringCodec> {
	
	/**
	 * Constructs a new string codec.<br>
	 */
	public StringCodec() {}
	
	/**
	 * Constructs a new string codec with the specified length constraint configuration.<br>
	 *
	 * @param constraintConfig The length constraint configuration
	 */
	public StringCodec(@NonNull LengthConstraintConfig constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull StringCodec applyConstraint(@NonNull LengthConstraintConfig config) {
		Objects.requireNonNull(config, "Constraint config must not be null");
		return new StringCodec(config);
	}
	
	@Override
	protected @NonNull Result<Void> checkConstraints(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.length())).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("String " + value + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as string using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		
		return provider.createString(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key);
	}
	
	@Override
	public <R> @NonNull Result<String> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as string using '" + this + "'");
		}
		
		Result<String> stringResult = provider.getString(value);
		if (stringResult.isError()) {
			return stringResult;
		}
		
		Result<Void> constraintResult = this.checkConstraints(stringResult.resultOrThrow());
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		
		return stringResult;
	}
	
	@Override
	public @NonNull Result<String> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key);
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedStringCodec[constraints=" + config + "]";
		}).orElse("StringCodec");
	}
}
