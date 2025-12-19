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
public record LengthConstraintConfig(
	@NotNull OptionalInt minLength,
	@NotNull OptionalInt maxLength,
	@NotNull Optional<Set<Integer>> validLengths
) {
	
	public LengthConstraintConfig {
		Objects.requireNonNull(minLength, "Minimum length must not be null");
		Objects.requireNonNull(maxLength, "Maximum length must not be null");
		Objects.requireNonNull(validLengths, "Valid lengths set must not be null");
		
		if (minLength.isPresent() && minLength.getAsInt() < 0) {
			throw new IllegalArgumentException("Minimum length cannot be negative");
		}
		if (maxLength.isPresent() && maxLength.getAsInt() < 0) {
			throw new IllegalArgumentException("Maximum length cannot be negative");
		}
		if (minLength.isPresent() && maxLength.isPresent() && maxLength.getAsInt() < minLength.getAsInt()) {
			throw new IllegalArgumentException("Maximum length cannot be less than minimum length");
		}
	}
}
