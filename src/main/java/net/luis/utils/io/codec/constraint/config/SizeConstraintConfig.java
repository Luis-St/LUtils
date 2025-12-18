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
