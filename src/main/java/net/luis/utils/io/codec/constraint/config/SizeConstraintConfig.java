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

package net.luis.utils.io.codec.constraint.config;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("OptionalContainsCollection")
public record SizeConstraintConfig(
	@NotNull OptionalInt minSize,
	@NotNull OptionalInt maxSize,
	@NotNull Optional<Set<Integer>> validSizes
) {
	
	public SizeConstraintConfig {
		Objects.requireNonNull(minSize, "Minimum size must not be null");
		Objects.requireNonNull(maxSize, "Maximum size must not be null");
		Objects.requireNonNull(validSizes, "Valid sizes set must not be null");
		
		if (minSize.isPresent() && minSize.getAsInt() < 0) {
			throw new IllegalArgumentException("Minimum size cannot be negative");
		}
		if (maxSize.isPresent() && maxSize.getAsInt() < 0) {
			throw new IllegalArgumentException("Maximum size cannot be negative");
		}
		if (minSize.isPresent() && maxSize.isPresent() && maxSize.getAsInt() < minSize.getAsInt()) {
			throw new IllegalArgumentException("Maximum size cannot be less than minimum size");
		}
	}
}
