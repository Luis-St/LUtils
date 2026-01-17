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
import net.luis.utils.io.codec.constraint_new.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint_new.numeric.IntegerConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Internal codec implementation for bytes.<br>
 *
 * @author Luis-St
 */
public class ByteCodec extends AbstractCodec<Byte, IntegerConstraintConfig<Byte>> implements IntegerConstraint<Byte, ByteCodec> {
	
	/**
	 * Constructs a new byte codec.<br>
	 */
	public ByteCodec() {}
	
	/**
	 * Constructs a new byte codec with the specified integer constraint configuration.<br>
	 *
	 * @param constraintConfig The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private ByteCodec(@NonNull IntegerConstraintConfig<Byte> constraintConfig) {
		super(constraintConfig);
	}
	
	@Override
	public @NonNull ByteCodec apply(@NonNull UnaryOperator<IntegerConstraintConfig<Byte>> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new ByteCodec(
			configModifier.apply(this.getConstraintConfig().orElse(IntegerConstraintConfig.unconstrained()))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Byte value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as byte using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return provider.createByte(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Byte key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		Result<Void> constraintResult = this.checkConstraints(key);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(Byte.toString(key));
	}
	
	@Override
	public <R> @NonNull Result<Byte> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as byte using '" + this + "'");
		}
		
		Result<Byte> result = provider.getByte(value);
		if (result.isError()) {
			return result;
		}
		
		Byte byteValue = result.resultOrThrow();
		Result<Void> constraintResult = this.checkConstraints(byteValue);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		return Result.success(byteValue);
	}
	
	@Override
	public @NonNull Result<Byte> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Byte byteValue = Byte.parseByte(key);
			
			Result<Void> constraintResult = this.checkConstraints(byteValue);
			if (constraintResult.isError()) {
				return Result.error(constraintResult.errorOrThrow());
			}
			return Result.success(byteValue);
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as byte using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedByteCodec[constraints=" + config + "]";
		}).orElse("ByteCodec");
	}
}
