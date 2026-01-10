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

import net.luis.utils.io.codec.constraint_new.ComparableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for constructing numeric constraints.<br>
 * <p>
 *     This builder implements {@link ComparableConstraint} to provide a fluent API for building
 *     integer-based numeric constraints.<br>
 *     It is typically used as a parameter to constraint builder methods that accept numeric constraints.
 * </p>
 *
 * @author Luis-St
 */
public class NumericConstraintBuilder implements ComparableConstraint<Integer, NumericConstraintBuilder> {

	/**
	 * The current constraint configuration being built.<br>
	 */
	private NumericFieldConstraintConfig config;

	/**
	 * Constructs a new numeric constraint builder with no constraints applied.<br>
	 */
	public NumericConstraintBuilder() {
		this.config = NumericFieldConstraintConfig.UNCONSTRAINED;
	}

	/**
	 * Constructs a new numeric constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public NumericConstraintBuilder(@NonNull NumericFieldConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}

	@Override
	public @NonNull NumericConstraintBuilder equalTo(@NonNull Integer value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder notEqualTo(@NonNull Integer value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder in(@NonNull Collection<Integer> values) {
		this.config = this.config.withIn(values);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder notIn(@NonNull Collection<Integer> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder custom(@NonNull Constraint<Integer> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder greaterThan(@NonNull Integer value) {
		this.config = this.config.withGreaterThan(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder greaterThanOrEqual(@NonNull Integer value) {
		this.config = this.config.withGreaterThanOrEqual(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder lessThan(@NonNull Integer value) {
		this.config = this.config.withLessThan(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder lessThanOrEqual(@NonNull Integer value) {
		this.config = this.config.withLessThanOrEqual(value);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder between(@NonNull Integer min, @NonNull Integer max) {
		this.config = this.config.withBetween(min, max);
		return this;
	}

	@Override
	public @NonNull NumericConstraintBuilder betweenOrEqual(@NonNull Integer min, @NonNull Integer max) {
		this.config = this.config.withBetweenOrEqual(min, max);
		return this;
	}

	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built numeric field constraint config
	 */
	public @NonNull NumericFieldConstraintConfig build() {
		return this.config;
	}
}
