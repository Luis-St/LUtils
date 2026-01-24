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

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.io.DomainConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.io.DomainConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing domain name constraints.<br>
 * <p>
 *     This builder implements {@link DomainConstraint} to provide a fluent API for building
 *     constraints on domain names including structure validation (root domain vs subdomain).<br>
 *     It is typically used as a parameter to constraint builder methods that accept domain constraints.
 * </p>
 *
 * @author Luis-St
 */
public class DomainConstraintBuilder implements DomainConstraint<String, DomainConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private DomainConstraintConfig config;
	
	/**
	 * Constructs a new domain constraint builder with no constraints applied.<br>
	 */
	public DomainConstraintBuilder() {
		this.config = DomainConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new domain constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public DomainConstraintBuilder(@NonNull DomainConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull DomainConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		LengthConstraintBuilder lengthBuilder = new LengthConstraintBuilder();
		builder.apply(lengthBuilder);
		this.config = this.config.withLength(lengthBuilder.build());
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder startsWith(@NonNull String prefix) {
		this.config = this.config.withStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notStartsWith(@NonNull String prefix) {
		this.config = this.config.withNotStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder startsWithAny(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithAny(prefixes);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder startsWithNone(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithNone(prefixes);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder contains(@NonNull String substring) {
		this.config = this.config.withContains(substring);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notContains(@NonNull String substring) {
		this.config = this.config.withNotContains(substring);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder containsAny(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAny(substrings);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder containsNone(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsNone(substrings);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder containsAll(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAll(substrings);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder containsOnly(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsOnly(substrings);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder endsWith(@NonNull String suffix) {
		this.config = this.config.withEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notEndsWith(@NonNull String suffix) {
		this.config = this.config.withNotEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder endsWithAny(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithAny(suffixes);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder endsWithNone(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithNone(suffixes);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder matches(@NonNull String regex) {
		this.config = this.config.withMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notMatches(@NonNull String regex) {
		this.config = this.config.withNotMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder matches(@NonNull Pattern pattern) {
		this.config = this.config.withMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder notMatches(@NonNull Pattern pattern) {
		this.config = this.config.withNotMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder rootDomain() {
		this.config = this.config.withRootDomain();
		return this;
	}
	
	@Override
	public @NonNull DomainConstraintBuilder subDomain() {
		this.config = this.config.withSubDomain();
		return this;
	}
	
	/**
	 * Builds and returns the domain constraint configuration.<br>
	 *
	 * @return The built constraint configuration
	 */
	public @NonNull DomainConstraintConfig build() {
		return this.config;
	}
}
