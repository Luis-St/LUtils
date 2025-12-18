/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A constraint interface for size-based validation of collections, maps, and other sized types.<br>
 * Provides methods to validate the size of collections and maps.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The codec type
 */
@FunctionalInterface
public interface SizeConstraint<T, C extends Codec<T>> extends CodecConstraint<T, C, SizeConstraintConfig> {
	
	@Override
	@NonNull C applyConstraint(@NonNull SizeConstraintConfig config);
	
	default @NotNull C minSize(int minSize) {
		return this.applyConstraint(new SizeConstraintConfig(
			OptionalInt.of(minSize),
			OptionalInt.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C maxSize(int maxSize) {
		return this.applyConstraint(new SizeConstraintConfig(
			OptionalInt.empty(),
			OptionalInt.of(maxSize),
			Optional.empty()
		));
	}
	
	default @NotNull C exactSize(int exactSize) {
		return this.applyConstraint(new SizeConstraintConfig(
			OptionalInt.of(exactSize),
			OptionalInt.of(exactSize),
			Optional.empty()
		));
	}
	
	default @NotNull C sizeBetween(int minSize, int maxSize) {
		return this.applyConstraint(new SizeConstraintConfig(
			OptionalInt.of(minSize),
			OptionalInt.of(maxSize),
			Optional.empty()
		));
	}
	
	default @NotNull C sizeIn(@NotNull Set<Integer> validSizes) {
		Objects.requireNonNull(validSizes, "Valid sizes set must not be null");
		
		return this.applyConstraint(new SizeConstraintConfig(
			OptionalInt.empty(),
			OptionalInt.empty(),
			Optional.of(validSizes)
		));
	}
	
	default @NotNull C empty() {
		return this.exactSize(0);
	}
	
	default @NotNull C notEmpty() {
		return this.minSize(1);
	}
}
