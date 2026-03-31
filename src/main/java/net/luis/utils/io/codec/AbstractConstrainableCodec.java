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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.core.ApplicableConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Abstract non-sealed implementation of the {@link Codec} interface.<br>
 * Used by all default codecs in the {@link net.luis.utils.io.codec.types} package.<br>
 * <p>
 *     This class provides a way to store constraint configurations for codecs that support constraints.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the codec
 * @param <T> The type of the constraint config
 * @param <V> The return type of the apply method for modifying the constraint config
 */
public abstract non-sealed class AbstractConstrainableCodec<C, T extends ConstraintConfig<C>, V> implements Codec<C>, ApplicableConstraint<T, V> {
	
	/**
	 * The name of the codec class.<br>
	 * Used for string representation purposes.<br>
	 */
	private final String codecName = this.getClass().getSimpleName();
	/**
	 * The constructor function to create new instances of the codec with modified constraint configurations.<br>
	 */
	protected final Function<T, V> constructorFunction;
	/**
	 * The constraint configuration for this codec.<br>
	 */
	protected final T config;
	
	/**
	 * Constructs a new abstract constrainable codec with the given constructor function and constraint configuration.<br>
	 *
	 * @param constructorFunction The constructor function to create new instances of the codec with modified constraint configurations
	 * @param config The constraint configuration for this codec
	 * @throws NullPointerException If the constraint configuration is null
	 */
	protected AbstractConstrainableCodec(@NonNull Function<T, V> constructorFunction, @NonNull T config) {
		this.constructorFunction = Objects.requireNonNull(constructorFunction, "Constructor function must not be null");
		this.config = Objects.requireNonNull(config, "Constraint configuration must not be null");
	}
	
	@Override
	public @NonNull V apply(@NonNull UnaryOperator<T> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return this.constructorFunction.apply(configModifier.apply(this.config));
	}
	
	/**
	 * Checks if this codec has no constraints applied.<br>
	 * @return True if this codec is unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this.config.isUnconstrained();
	}
	
	/**
	 * Checks the constraints for the given value.<br>
	 *
	 * @param value The value to check
	 * @return The validated value if the constraints were satisfied
	 * @throws NullPointerException If the value is null
	 * @throws ConstraintViolateException If the value violates any constraints
	 */
	private @NonNull C validateConstraints(@NonNull C value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		if (!this.config.isUnconstrained()) {
			this.config.validate(value);
		}
		return value;
	}
	
	/**
	 * Validates the constraints for the given value during encoding.<br>
	 *
	 * @param value The value to validate
	 * @return The validated value if the constraints were satisfied
	 * @throws NullPointerException If the value is null
	 * @throws EncoderException If the value violates any constraints
	 * @see #validateDecodeConstraints(Object)
	 */
	protected @NonNull C validateEncodeConstraints(@NonNull C value) throws EncoderException {
		try {
			return this.validateConstraints(value);
		} catch (ConstraintViolateException e) {
			throw new EncoderException("Encoding failed due to constraint violation: " + e.getMessage(), this, e);
		}
	}
	
	/**
	 * Validates the constraints for the given value during decoding.<br>
	 *
	 * @param value The value to validate
	 * @return The validated value if the constraints were satisfied
	 * @throws NullPointerException If the value is null
	 * @throws DecoderException If the value violates any constraints
	 * @see #validateEncodeConstraints(Object)
	 */
	protected @NonNull C validateDecodeConstraints(@NonNull C value) throws DecoderException {
		try {
			return this.validateConstraints(value);
		} catch (ConstraintViolateException e) {
			throw new DecoderException("Decoding failed due to constraint violation: " + e.getMessage(), this, e);
		}
	}
	
	/**
	 * Generates the base string representation of this codec including its constraint configuration if applicable.<br>
	 * <p>
	 *     If the codec is unconstrained, it simply returns the given codec name.<br>
	 *     Otherwise, it returns a string in the format {@code Constrained{codecName}[constraints={constraintConfig}]}.<br>
	 * </p>
	 * The codec name is derived from the class name of the codec instance.<br>
	 *
	 * @return The base string representation of this codec
	 */
	@Override
	public String toString() {
		Objects.requireNonNull(this.codecName, "Codec name must not be null");
		return this.config.isUnconstrained() ? this.codecName : "Constrained" + this.codecName + "[constraints=" + this.config + "]";
	}
}
