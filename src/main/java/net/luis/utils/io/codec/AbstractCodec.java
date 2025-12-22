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

package net.luis.utils.io.codec;

import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstract non-sealed implementation of the {@link Codec} interface.<br>
 * Used by all default codecs in the {@link net.luis.utils.io.codec.types} package.<br>
 * <p>
 *     This class provides a way to store constraint configurations for codecs that support constraints.<br>
 *     If the codec does not support constraints, the constraint configuration will be null.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the codec
 * @param <T> The type of the constraint config, or {@link Void} if the codec does not support constraints
 */
public abstract non-sealed class AbstractCodec<C, T> implements Codec<C> {
	
	/**
	 * The constraint configuration for this codec, empty if the codec does not support constraints.<br>
	 */
	private final @NonNull Optional<T> constraintConfig;
	
	/**
	 * Constructs a new abstract codec without constraint configuration.<br>
	 */
	protected AbstractCodec() {
		this.constraintConfig = Optional.empty();
	}
	
	/**
	 * Constructs a new abstract codec with the given constraint configuration.<br>
	 * @param constraintConfig The constraint configuration for this codec
	 * @throws NullPointerException If the constraint configuration is null
	 */
	protected AbstractCodec(@NonNull T constraintConfig) {
		Objects.requireNonNull(constraintConfig, "Constraint configuration must not be null");
		this.constraintConfig = Optional.of(constraintConfig);
	}
	
	/**
	 * Gets the constraint configuration for this codec.<br>
	 * @return The constraint configuration, empty if the codec does not support constraints
	 */
	protected @NonNull Optional<T> getConstraintConfig() {
		return this.constraintConfig;
	}
	
	/**
	 * Checks the constraints for the given value.<br>
	 * The value of the returned {@link Result} indicates whether the constraints were satisfied or not.<br>
	 *
	 * @param value The value to check
	 * @return The result of the constraint check
	 * @throws NullPointerException If the value is null
	 */
	protected @NonNull Result<Void> checkConstraints(@NonNull C value) {
		return Result.success();
	}
}
