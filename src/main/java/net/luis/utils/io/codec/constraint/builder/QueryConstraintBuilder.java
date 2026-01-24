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

import net.luis.utils.io.codec.constraint.config.collection.MapConstraintConfig;
import net.luis.utils.io.codec.constraint.config.io.QueryConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.core.io.QueryConstraint;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Builder class for constructing query parameter constraints.<br>
 * <p>
 *     This builder implements {@link QueryConstraint} to provide a fluent API for building
 *     constraints on URI query parameters including value validation and single/multi-value requirements.<br>
 *     It is typically used as a parameter to constraint builder methods that accept query constraints.
 * </p>
 *
 * @author Luis-St
 */
public class QueryConstraintBuilder implements QueryConstraint<QueryConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private QueryConstraintConfig config;
	
	/**
	 * Constructs a new query constraint builder with no constraints applied.<br>
	 */
	public QueryConstraintBuilder() {
		this.config = QueryConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new query constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public QueryConstraintBuilder(@NonNull QueryConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull QueryConstraintBuilder apply(@NonNull UnaryOperator<MapConstraintConfig<String, List<String>>> configModifier) {
		throw new UnsupportedOperationException("The 'apply' method is not supported in query constraint builder");
	}
	
	@Override
	public @NonNull QueryConstraintBuilder equalTo(@NonNull Map<String, List<String>> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder notEqualTo(@NonNull Map<String, List<String>> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder in(@NonNull Collection<Map<String, List<String>>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder notIn(@NonNull Collection<Map<String, List<String>>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder custom(@NonNull Constraint<Map<String, List<String>>> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder size(@NonNull UnaryOperator<SizeConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'size' constraint must not be null");
		
		SizeConstraintBuilder sizeBuilder = new SizeConstraintBuilder();
		builder.apply(sizeBuilder);
		this.config = this.config.withSize(sizeBuilder.build());
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder requiredKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key for 'required key' constraint must not be null");
		return this.requiredKeys(Collections.singleton(key));
	}
	
	@Override
	public @NonNull QueryConstraintBuilder requiredKeys(@NonNull Collection<String> keys) {
		this.config = this.config.withRequiredKeys(keys);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder forbiddenKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key for 'forbidden key' constraint must not be null");
		return this.forbiddenKeys(Collections.singleton(key));
	}
	
	@Override
	public @NonNull QueryConstraintBuilder forbiddenKeys(@NonNull Collection<String> keys) {
		this.config = this.config.withForbiddenKeys(keys);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder allowedKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key for 'allowed key' constraint must not be null");
		return this.allowedKeys(Collections.singleton(key));
	}
	
	@Override
	public @NonNull QueryConstraintBuilder allowedKeys(@NonNull Collection<String> keys) {
		this.config = this.config.withAllowedKeys(keys);
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder nonNullKeys() {
		this.config = this.config.withNonNullKeys();
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder uniqueValues() {
		this.config = this.config.withUniqueValues();
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder nonNullValues() {
		this.config = this.config.withNonNullValues();
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder value(@NonNull String key, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'value' constraint must not be null");
		
		this.config = this.config.withValue(key, builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder values(@NonNull String regex, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'values' constraint must not be null");
		
		this.config = this.config.withValues(Pattern.compile(regex), builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder values(@NonNull Pattern pattern, @NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'values' constraint must not be null");
		
		this.config = this.config.withValues(pattern, builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder singleValued() {
		this.config = this.config.withSingleValued();
		return this;
	}
	
	@Override
	public @NonNull QueryConstraintBuilder multiValued(@NonNull String key, @NonNull UnaryOperator<SizeConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'multi valued' constraint must not be null");
		
		this.config = this.config.withMultiValued(key, builder.apply(new SizeConstraintBuilder()).build());
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built query constraint config
	 */
	public @NonNull QueryConstraintConfig build() {
		return this.config;
	}
}
