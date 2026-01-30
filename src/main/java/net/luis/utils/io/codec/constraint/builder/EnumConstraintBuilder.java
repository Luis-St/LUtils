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

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.core.BaseConstraint;
import net.luis.utils.io.codec.constraint.core.Constraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for constructing enum-based constraints.<br>
 * <p>
 *     This builder implements {@link BaseConstraint} to provide a fluent API for building
 *     constraints on enumeration values.<br>
 *     It is typically used as a parameter to constraint builder methods that accept enum constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The enum type being constrained
 */
public class EnumConstraintBuilder<T extends Enum<T>> implements BaseConstraint<T, EnumConstraintBuilder<T>> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private EnumConstraintConfig<T> config;
	
	/**
	 * Constructs a new enum constraint builder with no constraints applied.<br>
	 */
	public EnumConstraintBuilder() {
		this.config = EnumConstraintConfig.unconstrained();
	}
	
	/**
	 * Constructs a new enum constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public EnumConstraintBuilder(@NonNull EnumConstraintConfig<T> initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> equalTo(@NonNull T value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> notEqualTo(@NonNull T value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> in(@NonNull Collection<T> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> notIn(@NonNull Collection<T> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull EnumConstraintBuilder<T> custom(@NonNull Constraint<T> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	/**
	 * Builds and returns the enum constraint configuration.<br>
	 * @return The built constraint configuration
	 */
	public @NonNull EnumConstraintConfig<T> build() {
		return this.config;
	}
}
