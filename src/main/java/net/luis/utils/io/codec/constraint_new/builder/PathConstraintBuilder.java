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
import net.luis.utils.io.codec.constraint.config.io.PathConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.Platform;
import net.luis.utils.io.codec.constraint.merged.io.PathConstraint;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Builder class for constructing path-based constraints.<br>
 * <p>
 *     This builder implements {@link PathConstraint} to provide a fluent API for building
 *     constraints on file paths including structure, components, and platform compatibility.<br>
 *     It is typically used as a parameter to constraint builder methods that accept path constraints.
 * </p>
 *
 * @author Luis-St
 */
public class PathConstraintBuilder implements PathConstraint<String, PathConstraintBuilder> {
	
	/**
	 * The current constraint configuration being built.<br>
	 */
	private PathConstraintConfig config;
	
	/**
	 * Constructs a new path constraint builder with no constraints applied.<br>
	 */
	public PathConstraintBuilder() {
		this.config = PathConstraintConfig.UNCONSTRAINED;
	}
	
	/**
	 * Constructs a new path constraint builder with the specified initial config.<br>
	 *
	 * @param initialConfig The initial configuration to use
	 * @throws NullPointerException If the initial config is null
	 */
	public PathConstraintBuilder(@NonNull PathConstraintConfig initialConfig) {
		this.config = Objects.requireNonNull(initialConfig, "Initial config must not be null");
	}
	
	@Override
	public @NonNull PathConstraintBuilder equalTo(@NonNull String value) {
		this.config = this.config.withEqualTo(Path.of(value));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notEqualTo(@NonNull String value) {
		this.config = this.config.withNotEqualTo(Path.of(value));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder in(@NonNull Collection<String> values) {
		this.config = this.config.withIn(values.stream().map(Path::of).collect(Collectors.toList()));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notIn(@NonNull Collection<String> values) {
		this.config = this.config.withNotIn(values.stream().map(Path::of).collect(Collectors.toList()));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder custom(@NonNull Constraint<String> constraint) {
		this.config = this.config.withCustom(path -> constraint.validate(path.toString()));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'length' constraint must not be null");
		
		this.config = this.config.withLength(builder.apply(new LengthConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder depth(@NonNull UnaryOperator<DepthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'depth' constraint must not be null");
		
		this.config = this.config.withDepth(builder.apply(new DepthConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder absolute() {
		this.config = this.config.withAbsolute();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder relative() {
		this.config = this.config.withRelative();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder normalized() {
		this.config = this.config.withNormalized();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder canonical() {
		this.config = this.config.withCanonical();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder path(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'path' constraint must not be null");
		
		this.config = this.config.withPath(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder root(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'root' constraint must not be null");
		
		this.config = this.config.withRoot(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder parent(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'parent' constraint must not be null");
		
		this.config = this.config.withParent(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder segment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'segment' constraint must not be null");
		
		this.config = this.config.withSegment(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder file(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'file' constraint must not be null");
		
		this.config = this.config.withFile(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder withoutExtension() {
		this.config = this.config.withWithoutExtension();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder extension(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder function for 'extension' constraint must not be null");
		
		this.config = this.config.withExtension(builder.apply(new StringConstraintBuilder()).build());
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull String path) {
		this.config = this.config.withAncestorOf(java.util.List.of(path));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull Collection<String> paths) {
		this.config = this.config.withAncestorOf(paths);
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull String path) {
		this.config = this.config.withDescendantOf(java.util.List.of(path));
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull Collection<String> paths) {
		this.config = this.config.withDescendantOf(paths);
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder validFor(@NonNull Platform platform) {
		this.config = this.config.withValidFor(platform);
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder portable() {
		this.config = this.config.withPortable();
		return this;
	}
	
	@Override
	public @NonNull PathConstraintBuilder separator(@NonNull Platform platform) {
		this.config = this.config.withSeparator(platform);
		return this;
	}
	
	/**
	 * Builds and returns the constraint configuration.<br>
	 *
	 * @return The built path constraint config
	 */
	public @NonNull PathConstraintConfig build() {
		return this.config;
	}
}
