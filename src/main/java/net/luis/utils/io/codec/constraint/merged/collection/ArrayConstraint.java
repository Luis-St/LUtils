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

package net.luis.utils.io.codec.constraint.merged.collection;

import net.luis.utils.io.codec.constraint.builder.LengthConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.collection.ArrayConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for array types that provides size validation operations.<br>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the array
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface ArrayConstraint<T, C> extends ApplicableConstraint<ArrayConstraintConfig<T>, C>, BaseConstraint<T[], C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<ArrayConstraintConfig<T>> configModifier);
	
	@Override
	default @NonNull C equalTo(T @NonNull [] value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(T @NonNull [] value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<T[]> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<T[]> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<T[]> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies length constraints to the array using a builder.<br>
	 * <p>
	 *     This method provides a fluent API for configuring length constraints on arrays.<br>
	 *     The builder allows setting minimum length, maximum length, exact length, or length ranges.
	 * </p>
	 *
	 * @param builder The builder function to configure length constraints
	 * @return A new type with the applied length constraints
	 * @throws NullPointerException If the builder is null
	 * @see LengthConstraintBuilder
	 */
	default @NonNull C length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		LengthConstraintBuilder lengthBuilder = new LengthConstraintBuilder();
		builder.apply(lengthBuilder);
		return this.apply(config -> config.withLength(lengthBuilder.build()));
	}
}
