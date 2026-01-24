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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.core.LengthConstraint;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for constructing length-based constraints.<br>
 * <p>
 *     This builder implements {@link LengthConstraint} to provide a fluent API for building
 *     constraints on string lengths or array lengths.<br>
 *     It is typically used as a parameter to constraint builder methods that accept length constraints.
 * </p>
 *
 * @author Luis-St
 */
public class LengthConstraintBuilder implements LengthConstraint<Integer, LengthConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private LengthConstraintConfig config;
	
	/**
	 * Constructs a new length constraint builder with no constraints applied.<br>
	 */
	public LengthConstraintBuilder() {
		this.config = LengthConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new length constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public LengthConstraintBuilder(@NonNull LengthConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull LengthConstraintBuilder equalTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder notEqualTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder in(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder minLength(int minLength) {
		this.config = this.config.withMinLength(minLength);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder maxLength(int maxLength) {
		this.config = this.config.withMaxLength(maxLength);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder exactLength(int exactLength) {
		this.config = this.config.withExactLength(exactLength);
		return this;
	}
	
	@Override
	public @NonNull LengthConstraintBuilder lengthBetween(int minLength, int maxLength) {
		this.config = this.config.withLengthBetween(minLength, maxLength);
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built length constraint config
	 */
	public @NonNull LengthConstraintConfig build() {
		return this.config;
	}
}
