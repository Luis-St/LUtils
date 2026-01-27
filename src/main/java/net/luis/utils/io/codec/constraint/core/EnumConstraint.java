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

package net.luis.utils.io.codec.constraint.core;

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Base interface for all enum constraint types that provides fundamental equality and membership operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods
 *     for constraining enum values based on equality, membership, and custom validation.<br>
 *     It allows validation of enum constants using the standard constraint operations.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The enum type being constrained
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface EnumConstraint<T extends Enum<T>, C> extends ApplicableConstraint<EnumConstraintConfig<T>, C>, BaseConstraint<T, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<EnumConstraintConfig<T>> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<T> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<T> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<T> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
}
