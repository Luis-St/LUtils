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

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint.config.io.PortConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.PortRange;
import net.luis.utils.io.codec.constraint.merged.io.PortConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing port-based constraints.<br>
 * <p>
 *     This builder implements {@link PortConstraint} to provide a fluent API for building
 *     constraints on network port numbers including range and port type validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept port constraints.
 * </p>
 *
 * @author Luis-St
 */
public class PortConstraintBuilder implements PortConstraint<Integer, PortConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private PortConstraintConfig config;
	
	/**
	 * Constructs a new port constraint builder with no constraints applied.<br>
	 */
	public PortConstraintBuilder() {
		this.config = PortConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new port constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public PortConstraintBuilder(@NonNull PortConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull PortConstraintBuilder equalTo(@NonNull Integer value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notEqualTo(@NonNull Integer value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder in(@NonNull Collection<Integer> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder inRange(int min, int max) {
		this.config = this.config.withInRange(min, max);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder notInRange(int min, int max) {
		this.config = this.config.withNotInRange(min, max);
		return this;
	}
	
	@Override
	public @NonNull PortConstraintBuilder type(@NonNull UnaryOperator<EnumConstraintBuilder<PortRange>> builder) {
		Objects.requireNonNull(builder, "Builder function for 'type' constraint must not be null");
		
		this.config = this.config.withType(builder.apply(new EnumConstraintBuilder<>()).build());
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built port constraint config
	 */
	public @NonNull PortConstraintConfig build() {
		return this.config;
	}
}
