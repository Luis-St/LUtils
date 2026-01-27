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

import net.luis.utils.io.codec.constraint.builder.SizeConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.collection.SetConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for set types that provides size validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link BaseConstraint} with methods
 *     for constraining sets based on their size.<br>
 *     It provides size validation capabilities using a fluent builder API.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the set
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface SetConstraint<T, C> extends ApplicableConstraint<SetConstraintConfig<T>, C>, BaseConstraint<Set<T>, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<SetConstraintConfig<T>> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Set<T> value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Set<T> value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Set<T>> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Set<T>> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Set<T>> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	/**
	 * Applies size constraints to the set using a builder.<br>
	 * <p>
	 *     This method provides a fluent API for configuring size constraints on sets.<br>
	 *     The builder allows setting minimum size, maximum size, exact size, or size ranges.
	 * </p>
	 *
	 * @param builder The builder function to configure size constraints
	 * @return A new type with the applied size constraints
	 * @throws NullPointerException If the builder is null
	 * @see SizeConstraintBuilder
	 */
	default @NonNull C size(@NonNull UnaryOperator<SizeConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		SizeConstraintBuilder sizeBuilder = new SizeConstraintBuilder();
		builder.apply(sizeBuilder);
		return this.apply(config -> config.withSize(sizeBuilder.build()));
	}
}
