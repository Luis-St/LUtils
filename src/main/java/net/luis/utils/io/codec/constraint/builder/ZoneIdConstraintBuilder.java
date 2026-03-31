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

import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.merged.temporal.zoned.ZoneIdConstraint;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing zone id constraints.<br>
 * <p>
 *     This builder implements {@link ZoneIdConstraint} to provide a fluent API for building
 *     constraints on zone id values including type, normalization, and availability validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept zone id constraints.
 * </p>
 *
 * @author Luis-St
 */
public class ZoneIdConstraintBuilder implements ZoneIdConstraint<ZoneIdConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private ZoneIdConstraintConfig config;
	
	/**
	 * Constructs a new zone id constraint builder with no constraints applied.<br>
	 */
	public ZoneIdConstraintBuilder() {
		this.config = ZoneIdConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new zone id constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public ZoneIdConstraintBuilder(@NonNull ZoneIdConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder apply(@NonNull UnaryOperator<ZoneIdConstraintConfig> configModifier) {
		throw new UnsupportedOperationException("The 'apply' method is not supported in zone id constraint builder");
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder equalTo(@NonNull ZoneId value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder notEqualTo(@NonNull ZoneId value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder in(@NonNull Collection<ZoneId> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder notIn(@NonNull Collection<ZoneId> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder custom(@NonNull Constraint<ZoneId> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder normalized() {
		this.config = this.config.withNormalized();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder regionBased() {
		this.config = this.config.withRegionBased();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder offsetBased() {
		this.config = this.config.withOffsetBased();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder fixedOffset() {
		this.config = this.config.withFixedOffset();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder utc() {
		this.config = this.config.withUtc();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder systemDefault() {
		this.config = this.config.withSystemDefault();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder available() {
		this.config = this.config.withAvailable();
		return this;
	}
	
	@Override
	public @NonNull ZoneIdConstraintBuilder region(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		this.config = this.config.withRegion(stringBuilder.build());
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 * @return The built zone id constraint config
	 */
	public @NonNull ZoneIdConstraintConfig build() {
		return this.config;
	}
}
