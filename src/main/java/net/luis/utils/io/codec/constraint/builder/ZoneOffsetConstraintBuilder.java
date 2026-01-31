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

import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.merged.temporal.zoned.ZoneOffsetConstraint;
import org.jspecify.annotations.NonNull;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing zone offset constraints.<br>
 * <p>
 *     This builder implements {@link ZoneOffsetConstraint} to provide a fluent API for building
 *     constraints on zone offset values including comparison, sign, and hour-based validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept zone offset constraints.
 * </p>
 *
 * @author Luis-St
 */
public class ZoneOffsetConstraintBuilder implements ZoneOffsetConstraint<ZoneOffsetConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private ZoneOffsetConstraintConfig config;
	
	/**
	 * Constructs a new zone offset constraint builder with no constraints applied.<br>
	 */
	public ZoneOffsetConstraintBuilder() {
		this.config = ZoneOffsetConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new zone offset constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public ZoneOffsetConstraintBuilder(@NonNull ZoneOffsetConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder apply(@NonNull UnaryOperator<ZoneOffsetConstraintConfig> configModifier) {
		throw new UnsupportedOperationException("The 'apply' method is not supported in zone offset constraint builder");
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder equalTo(@NonNull ZoneOffset value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder notEqualTo(@NonNull ZoneOffset value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder in(@NonNull Collection<ZoneOffset> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder notIn(@NonNull Collection<ZoneOffset> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder custom(@NonNull Constraint<ZoneOffset> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder greaterThan(@NonNull ZoneOffset value) {
		this.config = this.config.withGreaterThan(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder greaterThanOrEqual(@NonNull ZoneOffset value) {
		this.config = this.config.withGreaterThanOrEqual(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder lessThan(@NonNull ZoneOffset value) {
		this.config = this.config.withLessThan(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder lessThanOrEqual(@NonNull ZoneOffset value) {
		this.config = this.config.withLessThanOrEqual(value);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder between(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		this.config = this.config.withBetween(min, max);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder betweenOrEqual(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		this.config = this.config.withBetweenOrEqual(min, max);
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder positive() {
		this.config = this.config.withPositive();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder negative() {
		this.config = this.config.withNegative();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder nonPositive() {
		this.config = this.config.withNonPositive();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder nonNegative() {
		this.config = this.config.withNonNegative();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder zero() {
		this.config = this.config.withZero();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder nonZero() {
		this.config = this.config.withNonZero();
		return this;
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder utc() {
		return this.zero();
	}
	
	@Override
	public @NonNull ZoneOffsetConstraintBuilder hour(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		this.config = this.config.withHour(numericBuilder.build());
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 * @return The built zone offset constraint config
	 */
	public @NonNull ZoneOffsetConstraintConfig build() {
		return this.config;
	}
}
