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
import net.luis.utils.io.codec.constraint.config.CodecLengthConstraintConfig;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

/**
 * A constraint interface for length-based validation of strings, arrays, and other sequential types.<br>
 * Provides methods to validate the length of strings and arrays.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The codec type
 */
@FunctionalInterface
public interface CodecLengthConstraint<T, C extends Codec<T>> extends CodecConstraint<T, C, CodecLengthConstraintConfig> {
	
	@Override
	@NotNull C applyConstraint(@NotNull CodecLengthConstraintConfig config);
	
	default @NotNull C minLength(int minLength) {
		return this.applyConstraint(new CodecLengthConstraintConfig(OptionalInt.of(minLength), OptionalInt.empty()));
	}
	
	default @NotNull C maxLength(int maxLength) {
		return this.applyConstraint(new CodecLengthConstraintConfig(OptionalInt.empty(), OptionalInt.of(maxLength)));
	}
	
	default @NotNull C exactLength(int exactLength) {
		return this.applyConstraint(new CodecLengthConstraintConfig(OptionalInt.of(exactLength), OptionalInt.of(exactLength)));
	}
	
	default @NotNull C lengthInRange(int minLength, int maxLength) {
		return this.applyConstraint(new CodecLengthConstraintConfig(OptionalInt.of(minLength), OptionalInt.of(maxLength)));
	}
	
	default @NotNull C empty() {
		return this.exactLength(0);
	}
	
	default @NotNull C notEmpty() {
		return this.minLength(1);
	}
}
