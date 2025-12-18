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
 * A constraint interface for size-based validation of collections and maps.<br>
 * Provides methods to validate the number of elements in a collection or entries in a map.<br>
 *
 * @author Luis-St
 *
 * @param <T> The collection or map type being constrained
 * @param <C> The codec type
 */
public interface SizeCodecConstraint<T, C extends Codec<T>> extends CodecConstraint<T, C> {
	
	int getSize(@NotNull T value);
	
	default @NotNull C minSize(int minSize) {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size < minSize) {
				return Result.error("Size " + size + " is less than minimum size " + minSize);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C maxSize(int maxSize) {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size > maxSize) {
				return Result.error("Size " + size + " is greater than maximum size " + maxSize);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C exactSize(int exactSize) {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size != exactSize) {
				return Result.error("Size " + size + " is not equal to exact size " + exactSize);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C sizeBetween(int minSize, int maxSize) {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size < minSize || size > maxSize) {
				return Result.error("Size " + size + " is not between " + minSize + " and " + maxSize);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C sizeIn(@NotNull Set<Integer> allowedSizes) {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (!allowedSizes.contains(size)) {
				return Result.error("Size " + size + " is not in allowed sizes " + allowedSizes);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C empty() {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size != 0) {
				return Result.error("Size is " + size + ", expected empty");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C notEmpty() {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size == 0) {
				return Result.error("Size is empty, expected not empty");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C singleton() {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size != 1) {
				return Result.error("Size is " + size + ", expected singleton");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C pair() {
		return this.applyConstraint(value -> {
			int size = this.getSize(value);
			if (size != 2) {
				return Result.error("Size is " + size + ", expected pair");
			}
			return Result.success(true);
		});
	}
}
