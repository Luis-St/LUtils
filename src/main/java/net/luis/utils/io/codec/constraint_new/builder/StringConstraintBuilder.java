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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint.merged.StringConstraint;
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing string-based constraints.<br>
 * <p>
 *     This builder implements {@link StringConstraint} to provide a fluent API for building
 *     constraints on string values including pattern matching, length, and content validation.<br>
 *     It is typically used as a parameter to constraint builder methods that accept string constraints.
 * </p>
 *
 * @author Luis-St
 */
public class StringConstraintBuilder implements StringConstraint<StringConstraintBuilder> {
	
	// ToDo: Fix
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private StringConstraintConfig config;
	
	/**
	 * Constructs a new string constraint builder with no constraints applied.<br>
	 */
	public StringConstraintBuilder() {
		this.config = StringConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new string constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public StringConstraintBuilder(@NonNull StringConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull StringConstraintBuilder apply(@NonNull UnaryOperator<StringConstraintConfig> configModifier) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public @NonNull StringConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder equalToIgnoreCase(@NonNull String value) {
		this.config = this.config.withEqualToIgnoreCase(value);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEqualToIgnoreCase(@NonNull String value) {
		this.config = this.config.withNotEqualToIgnoreCase(value);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder inIgnoreCase(@NonNull Collection<String> values) {
		this.config = this.config.withInIgnoreCase(values);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notInIgnoreCase(@NonNull Collection<String> values) {
		this.config = this.config.withNotInIgnoreCase(values);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder lowerCase() {
		this.config = this.config.withLowerCase();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder upperCase() {
		this.config = this.config.withUpperCase();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		LengthConstraintBuilder lengthBuilder = new LengthConstraintBuilder();
		builder.apply(lengthBuilder);
		this.config = this.config.withLength(lengthBuilder.build());
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWith(@NonNull String prefix) {
		this.config = this.config.withStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notStartsWith(@NonNull String prefix) {
		this.config = this.config.withNotStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWithAny(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithAny(prefixes);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder startsWithNone(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithNone(prefixes);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder contains(@NonNull String substring) {
		this.config = this.config.withContains(substring);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notContains(@NonNull String substring) {
		this.config = this.config.withNotContains(substring);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsAny(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAny(substrings);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsNone(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsNone(substrings);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsAll(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAll(substrings);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder containsOnly(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsOnly(substrings);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWith(@NonNull String suffix) {
		this.config = this.config.withEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notEndsWith(@NonNull String suffix) {
		this.config = this.config.withNotEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWithAny(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithAny(suffixes);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder endsWithNone(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithNone(suffixes);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder matches(@NonNull String regex) {
		this.config = this.config.withMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notMatches(@NonNull String regex) {
		this.config = this.config.withNotMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder matches(@NonNull Pattern pattern) {
		this.config = this.config.withMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notMatches(@NonNull Pattern pattern) {
		this.config = this.config.withNotMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder trimmed() {
		this.config = this.config.withTrimmed();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder blank() {
		this.config = this.config.withBlank();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder notBlank() {
		this.config = this.config.withNotBlank();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder numeric() {
		this.config = this.config.withNumeric();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder alphabetic() {
		this.config = this.config.withAlphabetic();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder alphanumeric() {
		this.config = this.config.withAlphanumeric();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder ascii() {
		this.config = this.config.withAscii();
		return this;
	}
	
	@Override
	public @NonNull StringConstraintBuilder latin1() {
		this.config = this.config.withLatin1();
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built string constraint config
	 */
	public @NonNull StringConstraintConfig build() {
		return this.config;
	}
}
