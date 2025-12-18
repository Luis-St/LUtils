package net.luis.utils.io.codec.constraint.config;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.OptionalInt;

/**
 *
 * @author Luis-St
 *
 */

public record CodecLengthConstraintConfig(@NotNull OptionalInt minLength, @NotNull OptionalInt maxLength) {
	
	public CodecLengthConstraintConfig {
		Objects.requireNonNull(minLength, "Minimum length must not be null");
		Objects.requireNonNull(maxLength, "Maximum length must not be null");
		
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
