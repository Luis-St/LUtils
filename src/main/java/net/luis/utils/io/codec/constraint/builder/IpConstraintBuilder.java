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

package net.luis.utils.io.codec.constraint.builder;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.io.IpConstraintConfig;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.constraint.merged.io.IpConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing IP address constraints.<br>
 * <p>
 *     This builder implements {@link IpConstraint} to provide a fluent API for building
 *     constraints on IP addresses including format validation, type classification, and subnet membership.<br>
 *     It is typically used as a parameter to constraint builder methods that accept IP constraints.
 * </p>
 *
 * @author Luis-St
 */
public class IpConstraintBuilder implements IpConstraint<String, IpConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private IpConstraintConfig config;
	
	/**
	 * Constructs a new IP constraint builder with no constraints applied.<br>
	 */
	public IpConstraintBuilder() {
		this.config = IpConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new IP constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public IpConstraintBuilder(@NonNull IpConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull IpConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		LengthConstraintBuilder lengthBuilder = new LengthConstraintBuilder();
		builder.apply(lengthBuilder);
		this.config = this.config.withLength(lengthBuilder.build());
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder startsWith(@NonNull String prefix) {
		this.config = this.config.withStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notStartsWith(@NonNull String prefix) {
		this.config = this.config.withNotStartsWith(prefix);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder startsWithAny(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithAny(prefixes);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder startsWithNone(@NonNull Collection<String> prefixes) {
		this.config = this.config.withStartsWithNone(prefixes);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder contains(@NonNull String substring) {
		this.config = this.config.withContains(substring);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notContains(@NonNull String substring) {
		this.config = this.config.withNotContains(substring);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder containsAny(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAny(substrings);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder containsNone(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsNone(substrings);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder containsAll(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsAll(substrings);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder containsOnly(@NonNull Collection<String> substrings) {
		this.config = this.config.withContainsOnly(substrings);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder endsWith(@NonNull String suffix) {
		this.config = this.config.withEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notEndsWith(@NonNull String suffix) {
		this.config = this.config.withNotEndsWith(suffix);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder endsWithAny(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithAny(suffixes);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder endsWithNone(@NonNull Collection<String> suffixes) {
		this.config = this.config.withEndsWithNone(suffixes);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder matches(@NonNull String regex) {
		this.config = this.config.withMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notMatches(@NonNull String regex) {
		this.config = this.config.withNotMatches(regex);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder matches(@NonNull Pattern pattern) {
		this.config = this.config.withMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder notMatches(@NonNull Pattern pattern) {
		this.config = this.config.withNotMatches(pattern);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder ipVersion(@NonNull UnaryOperator<EnumConstraintBuilder<IpVersion>> builder) {
		Objects.requireNonNull(builder, "Builder function must not be null");
		
		this.config = this.config.withIpVersion(builder.apply(new EnumConstraintBuilder<>()).build());
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder ipType(@NonNull UnaryOperator<EnumConstraintBuilder<IpAddressType>> builder) {
		Objects.requireNonNull(builder, "Builder function must not be null");
		
		this.config = this.config.withIpType(builder.apply(new EnumConstraintBuilder<>()).build());
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder inAnySubnet(@NonNull Collection<String> cidrs) {
		this.config = this.config.withInAnySubnet(cidrs);
		return this;
	}
	
	@Override
	public @NonNull IpConstraintBuilder inNoSubnet(@NonNull Collection<String> cidrs) {
		this.config = this.config.withNotInAnySubnet(cidrs);
		return this;
	}
	
	/**
	 * Builds and returns the IP constraint configuration.<br>
	 *
	 * @return The built constraint configuration
	 */
	public @NonNull IpConstraintConfig build() {
		return this.config;
	}
}
