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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.constraint.LengthConstraint;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Internal codec implementation for byte arrays.<br>
 * Uses a list of bytes as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ByteArrayCodec extends AbstractCodec<byte[], LengthConstraintConfig> implements LengthConstraint<byte[], ByteArrayCodec> {

	/**
	 * The internal codec that handles the conversion between a list of bytes and the array representation.<br>
	 */
	private final Codec<List<Byte>> internalCodec = Codecs.BYTE.list();

	/**
	 * Constructs a new byte array codec.<br>
	 */
	public ByteArrayCodec() {}

	/**
	 * Constructs a new byte array codec with the specified length constraint configuration.<br>
	 *
	 * @param constraintConfig The length constraint configuration
	 */
	public ByteArrayCodec(@NonNull LengthConstraintConfig constraintConfig) {
		super(constraintConfig);
	}

	@Override
	public @NonNull ByteArrayCodec applyConstraint(@NonNull LengthConstraintConfig config) {
		Objects.requireNonNull(config, "Constraint config must not be null");
		return new ByteArrayCodec(config);
	}

	@Override
	protected @NonNull Result<Void> checkConstraints(byte @NonNull [] value) {
		Objects.requireNonNull(value, "Value must not be null");
		Result<Void> constraintResult = this.getConstraintConfig().map(config -> config.matches(value.length)).orElseGet(Result::success);
		if (constraintResult.isError()) {
			return Result.error("Byte array " + Arrays.toString(value) + " does not meet constraints: " + constraintResult.errorOrThrow());
		}
		return Result.success();
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, byte @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");

		if (value == null) {
			return Result.error("Unable to encode null as byte array using '" + this + "'");
		}

		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return this.internalCodec.encodeStart(provider, current, Arrays.asList(ArrayUtils.toObject(value)));
	}

	@Override
	public <R> @NonNull Result<byte[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as byte array using '" + this + "'");
		}

		Result<List<Byte>> result = this.internalCodec.decodeStart(provider, current, value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}

		List<Byte> list = result.resultOrThrow();
		byte[] array = ArrayUtils.toPrimitive(list.toArray(Byte[]::new));

		Result<Void> constraintResult = this.checkConstraints(array);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}

		return Result.success(array);
	}

	@Override
	public String toString() {
		if (this.getConstraintConfig().isPresent()) {
			return "ConstrainedByteArrayCodec[constraints=" + this.getConstraintConfig().get() + "]";
		}
		return "ByteArrayCodec";
	}
}
