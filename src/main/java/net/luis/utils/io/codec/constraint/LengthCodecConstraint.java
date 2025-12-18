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
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A constraint interface for length-based validation of types line strings, arrays, and other sequential types.<br>
 * Provides methods to validate the length of strings and arrays.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The codec type
 */
public interface LengthCodecConstraint<T, C extends Codec<T>> extends CodecConstraint<T, C> {
	
	int getLength(@NotNull T value);
	
	default @NotNull C minLength(int minLength) {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length < minLength) {
				return Result.error("Length " + length + " is less than minimum length " + minLength);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C maxLength(int maxLength) {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length > maxLength) {
				return Result.error("Length " + length + " is greater than maximum length " + maxLength);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C exactLength(int exactLength) {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length != exactLength) {
				return Result.error("Length " + length + " is not equal to exact length " + exactLength);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C lengthBetween(int minLength, int maxLength) {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length < minLength || length > maxLength) {
				return Result.error("Length " + length + " is not between " + minLength + " and " + maxLength);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C lengthIn(@NotNull Set<Integer> validLengths) {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (!validLengths.contains(length)) {
				return Result.error("Length " + length + " is not in valid lengths " + validLengths);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C empty() {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length != 0) {
				return Result.error("Length is " + length + ", expected 0");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C notEmpty() {
		return this.applyConstraint(value -> {
			int length = this.getLength(value);
			if (length == 0) {
				return Result.error("Length is 0, expected non-empty");
			}
			return Result.success(true);
		});
	}
}
