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

import net.luis.utils.io.codec.constraint.config.io.HostConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.merged.io.HostConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing host-based constraints.<br>
 * <p>
 *     This builder implements {@link HostConstraint} to provide a fluent API for building
 *     constraints on network hosts. It allows choosing between IP address constraints
 *     or domain name constraints, which are mutually exclusive.<br>
 *     It is typically used as a parameter to constraint builder methods that accept host constraints.
 * </p>
 *
 * @author Luis-St
 */
public class HostConstraintBuilder implements HostConstraint<String, HostConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private HostConstraintConfig config;
	
	/**
	 * Constructs a new host constraint builder with no constraints applied.<br>
	 */
	public HostConstraintBuilder() {
		this.config = HostConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new host constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public HostConstraintBuilder(@NonNull HostConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull HostConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder ip(@NonNull UnaryOperator<IpConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function must not be null");
		
		this.config = this.config.withIp(builder.apply(new IpConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull HostConstraintBuilder domain(@NonNull UnaryOperator<DomainConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function must not be null");
		
		this.config = this.config.withDomain(builder.apply(new DomainConstraintBuilder()).build());
		return this;
	}
	
	/**
	 * Builds and returns the host constraint configuration.<br>
	 * @return The built constraint configuration
	 */
	public @NonNull HostConstraintConfig build() {
		return this.config;
	}
}
