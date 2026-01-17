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

package net.luis.utils.io.codec.constraint_new.collection;

import net.luis.utils.io.codec.constraint_new.*;
import net.luis.utils.io.codec.constraint_new.config.SizeConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for list types that provides size validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint} and {@link SizeConstraint} to provide
 *     comprehensive list size validation capabilities.<br>
 *     It provides methods to set minimum, maximum, exact, and range-based size constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface ListConstraint<C> extends ApplicableConstraint<SizeConstraintConfig, C>, SizeConstraint<Integer, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<SizeConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Integer value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Integer value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Integer> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Integer> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Integer> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C minSize(int minSize) {
		return this.apply(config -> config.withMinSize(minSize));
	}
	
	@Override
	default @NonNull C maxSize(int maxSize) {
		return this.apply(config -> config.withMaxSize(maxSize));
	}
	
	@Override
	default @NonNull C exactSize(int exactSize) {
		return this.apply(config -> config.withExactSize(exactSize));
	}
	
	@Override
	default @NonNull C sizeBetween(int minSize, int maxSize) {
		return this.apply(config -> config.withSizeBetween(minSize, maxSize));
	}
}
