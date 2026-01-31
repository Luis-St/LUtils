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

package net.luis.utils.io.codec.constraint.merged.io;

import net.luis.utils.io.codec.constraint.builder.*;
import net.luis.utils.io.codec.constraint.config.io.FilePathConstraintConfig;
import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.core.io.PathConstraint;
import net.luis.utils.io.codec.constraint.util.Platform;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for file system path types that provides comprehensive path validation operations.<br>
 * <p>
 *     This interface extends {@link ApplicableConstraint}, {@link BaseConstraint}, and {@link PathConstraint}
 *     with methods for constraining paths based on their structure, components, relationships, and platform compatibility.<br>
 *     It allows validation of file system paths according to various criteria using {@link java.nio.file.Path Path}.
 * </p>
 * <p>
 *     In addition to the common path constraints defined in {@link PathConstraint}, this interface provides
 *     platform-specific methods for file system paths:
 * </p>
 * <ul>
 *     <li>{@link #canonical()} - canonical path validation</li>
 *     <li>{@link #root(UnaryOperator)} - root component validation</li>
 *     <li>{@link #parent(UnaryOperator)} - parent directory validation</li>
 *     <li>{@link #validFor(Platform)} - platform-specific validity</li>
 *     <li>{@link #portable()} - cross-platform portability</li>
 *     <li>{@link #separator(Platform)} - path separator validation</li>
 * </ul>
 *
 * @author Luis-St
 *
 * @param <T> The value type for BaseConstraint methods (String for builders, Path for codecs)
 * @param <C> The return type of the constraint method (for fluent method chaining)
 *
 * @see PathConstraint
 */
@FunctionalInterface
public interface FilePathConstraint<T, C> extends ApplicableConstraint<FilePathConstraintConfig, C>, PathConstraint<Path, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<FilePathConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Path value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Path value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Path> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Path> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Path> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C length(@NonNull UnaryOperator<LengthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		return this.apply(config -> config.withLength(builder.apply(new LengthConstraintBuilder()).build()));
	}
	
	@Override
	default @NonNull C depth(@NonNull UnaryOperator<DepthConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		return this.apply(config -> config.withDepth(builder.apply(new DepthConstraintBuilder()).build()));
	}
	
	@Override
	default @NonNull C absolute() {
		return this.apply(FilePathConstraintConfig::withAbsolute);
	}
	
	@Override
	default @NonNull C relative() {
		return this.apply(FilePathConstraintConfig::withRelative);
	}
	
	@Override
	default @NonNull C normalized() {
		return this.apply(FilePathConstraintConfig::withNormalized);
	}
	
	@Override
	default @NonNull C path(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withPath(stringBuilder.build()));
	}
	
	@Override
	default @NonNull C segment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withSegment(stringBuilder.build()));
	}
	
	@Override
	default @NonNull C file(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withFile(stringBuilder.build()));
	}
	
	@Override
	default @NonNull C withoutExtension() {
		return this.apply(FilePathConstraintConfig::withWithoutExtension);
	}
	
	@Override
	default @NonNull C extension(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withExtension(stringBuilder.build()));
	}
	
	@Override
	default @NonNull C ancestorOf(@NonNull String path) {
		Objects.requireNonNull(path, "Path must not be null");
		return this.apply(config -> config.withAncestorOf(Collections.singletonList(path)));
	}
	
	@Override
	default @NonNull C ancestorOf(@NonNull Collection<String> paths) {
		return this.apply(config -> config.withAncestorOf(paths));
	}
	
	@Override
	default @NonNull C descendantOf(@NonNull String path) {
		Objects.requireNonNull(path, "Path must not be null");
		return this.apply(config -> config.withDescendantOf(Collections.singletonList(path)));
	}
	
	@Override
	default @NonNull C descendantOf(@NonNull Collection<String> paths) {
		return this.apply(config -> config.withDescendantOf(paths));
	}
	
	/**
	 * Applies a canonical path constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that paths are in canonical form (absolute, normalized, and unique).
	 * </p>
	 *
	 * @return A new type with the applied canonical constraint
	 * @see #normalized()
	 */
	default @NonNull C canonical() {
		return this.apply(FilePathConstraintConfig::withCanonical);
	}
	
	/**
	 * Applies a root constraint to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the root component of the path matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the root constraint using a string constraint builder
	 * @return A new type with the applied root constraint
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C root(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withRoot(stringBuilder.build()));
	}
	
	/**
	 * Applies a parent constraint to the path using a builder.<br>
	 * <p>
	 *     The returned type will validate that the parent directory of the path matches the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the parent constraint using a string constraint builder
	 * @return A new type with the applied parent constraint
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C parent(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		StringConstraintBuilder stringBuilder = new StringConstraintBuilder();
		builder.apply(stringBuilder);
		return this.apply(config -> config.withParent(stringBuilder.build()));
	}
	
	/**
	 * Applies a platform validity constraint to the path.<br>
	 * <p>
	 *     The returned type will validate that paths are valid for the specified platform.
	 * </p>
	 *
	 * @param platform The platform to validate against
	 * @return A new type with the applied platform validity constraint
	 * @throws NullPointerException If the platform is null
	 * @see #portable()
	 * @see Platform
	 */
	default @NonNull C validFor(@NonNull Platform platform) {
		return this.apply(config -> config.withValidFor(platform));
	}
	
	/**
	 * Applies a portable path constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that paths are portable across all supported platforms.
	 * </p>
	 *
	 * @return A new type with the applied portable constraint
	 * @see #validFor(Platform)
	 */
	default @NonNull C portable() {
		return this.apply(FilePathConstraintConfig::withPortable);
	}
	
	/**
	 * Applies a path separator constraint based on platform.<br>
	 * <p>
	 *     The returned type will validate that paths use the path separator for the specified platform.
	 * </p>
	 *
	 * @param platform The platform whose separator should be used
	 * @return A new type with the applied separator constraint
	 * @throws NullPointerException If the platform is null
	 * @see Platform
	 */
	default @NonNull C separator(@NonNull Platform platform) {
		return this.apply(config -> config.withSeparator(platform));
	}
}
