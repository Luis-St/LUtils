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
import net.luis.utils.io.codec.constraint.core.SizeConstraint;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for constructing size-based constraints.<br>
 * <p>
 *     This builder implements {@link SizeConstraint} to provide a fluent API for building
 *     constraints on collection sizes.<br>
 *     It is typically used as a parameter to constraint builder methods that accept size constraints.
 * </p>
 *
 * @author Luis-St
 */
public class SizeConstraintBuilder implements SizeConstraint<Integer, SizeConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private SizeConstraintConfig config;
	
	/**
	 * Constructs a new size constraint builder with no constraints applied.<br>
	 */
	public SizeConstraintBuilder() {
		this.config = SizeConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new size constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public SizeConstraintBuilder(@NonNull SizeConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull SizeConstraintBuilder equalTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notEqualTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder in(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder minSize(int minSize) {
		this.config = this.config.withMinSize(minSize);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder maxSize(int maxSize) {
		this.config = this.config.withMaxSize(maxSize);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder exactSize(int exactSize) {
		this.config = this.config.withExactSize(exactSize);
		return this;
	}
	
	@Override
	public @NonNull SizeConstraintBuilder sizeBetween(int minSize, int maxSize) {
		this.config = this.config.withSizeBetween(minSize, maxSize);
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built size constraint config
	 */
	public @NonNull SizeConstraintConfig build() {
		return this.config;
	}
}
