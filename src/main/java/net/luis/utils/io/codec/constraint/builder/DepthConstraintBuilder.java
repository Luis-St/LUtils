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

package net.luis.utils.io.codec.constraint.builder;

import net.luis.utils.io.codec.constraint.config.DepthConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.core.DepthConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for constructing depth-based constraints.<br>
 * <p>
 *     This builder implements {@link DepthConstraint} to provide a fluent API for building
 *     constraints on hierarchical depths such as path depths or tree depths.<br>
 *     It is typically used as a parameter to constraint builder methods that accept depth constraints.
 * </p>
 *
 * @author Luis-St
 */
public class DepthConstraintBuilder implements DepthConstraint<Integer, DepthConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private DepthConstraintConfig config;
	
	/**
	 * Constructs a new depth constraint builder with no constraints applied.<br>
	 */
	public DepthConstraintBuilder() {
		this.config = DepthConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new depth constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public DepthConstraintBuilder(@NonNull DepthConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull DepthConstraintBuilder equalTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder notEqualTo(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder in(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder minDepth(int minDepth) {
		this.config = this.config.withMinDepth(minDepth);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder maxDepth(int maxDepth) {
		this.config = this.config.withMaxDepth(maxDepth);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder exactDepth(int exactDepth) {
		this.config = this.config.withExactDepth(exactDepth);
		return this;
	}
	
	@Override
	public @NonNull DepthConstraintBuilder depthBetween(int minDepth, int maxDepth) {
		this.config = this.config.withDepthBetween(minDepth, maxDepth);
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built depth constraint config
	 */
	public @NonNull DepthConstraintConfig build() {
		return this.config;
	}
}
