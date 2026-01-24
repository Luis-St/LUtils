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

import net.luis.utils.io.codec.constraint.config.io.URIPathConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.core.io.URIPathConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing URI path-based constraints.<br>
 * <p>
 *     This builder provides a fluent API for building constraints on URI paths including
 *     structure, components, and relationships.<br>
 *     This builder operates on string-based URI paths that always use '/' as the separator, with no platform-specific behavior.<br>
 *     It is typically used as a parameter to constraint builder methods that accept URI path constraints.
 * </p>
 *
 * @author Luis-St
 */
public class URIPathConstraintBuilder implements URIPathConstraint<URIPathConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private URIPathConstraintConfig config;
	
	/**
	 * Constructs a new URI path constraint builder with no constraints applied.<br>
	 */
	public URIPathConstraintBuilder() {
		this.config = URIPathConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new URI path constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public URIPathConstraintBuilder(@NonNull URIPathConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(value);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(constraint);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'length' constraint must not be null");
		
		this.config = this.config.withLength(builder.apply(new LengthConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder depth(@NonNull UnaryOperator<DepthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'depth' constraint must not be null");
		
		this.config = this.config.withDepth(builder.apply(new DepthConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder absolute() {
		this.config = this.config.withAbsolute();
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder relative() {
		this.config = this.config.withRelative();
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder normalized() {
		this.config = this.config.withNormalized();
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder path(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'path' constraint must not be null");
		
		this.config = this.config.withPath(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder segment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'segment' constraint must not be null");
		
		this.config = this.config.withSegment(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder file(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'file' constraint must not be null");
		
		this.config = this.config.withFile(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder withoutExtension() {
		this.config = this.config.withWithoutExtension();
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder extension(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'extension' constraint must not be null");
		
		this.config = this.config.withExtension(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder ancestorOf(@NonNull String path) {
		this.config = this.config.withAncestorOf(java.util.List.of(path));
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder ancestorOf(@NonNull Collection<String> paths) {
		this.config = this.config.withAncestorOf(paths);
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder descendantOf(@NonNull String path) {
		this.config = this.config.withDescendantOf(java.util.List.of(path));
		return this;
	}
	
	@Override
	public @NonNull URIPathConstraintBuilder descendantOf(@NonNull Collection<String> paths) {
		this.config = this.config.withDescendantOf(paths);
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built URI path constraint config
	 */
	public @NonNull URIPathConstraintConfig build() {
		return this.config;
	}
}
