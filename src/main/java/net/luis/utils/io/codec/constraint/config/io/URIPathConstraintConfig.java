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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.matcher.IOMatchers;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for URI path constraints.<br>
 * <p>
 *     This record stores the constraint values for URI path validation.<br>
 *     This config operates on string-based URI paths that always use '/' as the separator, with no platform-specific behavior.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param length The length constraint configuration
 * @param depth The depth constraint configuration (counts '/' segments)
 * @param absolute If present, requires paths to start with '/'
 * @param relative If present, requires paths to not start with '/'
 * @param normalized If present, requires paths to contain no '.' or '..' segments
 * @param path The string constraint config for full path validation
 * @param segment The string constraint config for segment validation
 * @param file The string constraint config for file name (last segment) validation
 * @param withoutExtension If present, requires paths to have no extension
 * @param extension The string constraint config for extension validation
 * @param ancestorOf The set of paths that constrained paths must be ancestors of
 * @param descendantOf The set of paths that constrained paths must be descendants of
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record URIPathConstraintConfig(
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	@NonNull Optional<LengthConstraintConfig> length,
	@NonNull Optional<DepthConstraintConfig> depth,
	@NonNull Optional<Unit> absolute,
	@NonNull Optional<Unit> relative,
	@NonNull Optional<Unit> normalized,
	@NonNull Optional<StringConstraintConfig> path,
	@NonNull Optional<StringConstraintConfig> segment,
	@NonNull Optional<StringConstraintConfig> file,
	@NonNull Optional<Unit> withoutExtension,
	@NonNull Optional<StringConstraintConfig> extension,
	@NonNull Optional<Set<String>> ancestorOf,
	@NonNull Optional<Set<String>> descendantOf,
	@NonNull Optional<Constraint<String>> custom
) implements ConstraintConfig<String> {
	
	/**
	 * An unconstrained URI path configuration with no constraints applied.<br>
	 */
	public static final URIPathConstraintConfig UNCONSTRAINED = new URIPathConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new URI path constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param length The length constraint configuration
	 * @param depth The depth constraint configuration
	 * @param absolute If present, requires paths to start with '/'
	 * @param relative If present, requires paths to not start with '/'
	 * @param normalized If present, requires paths to contain no '.' or '..' segments
	 * @param path The string constraint config for full path validation
	 * @param segment The string constraint config for segment validation
	 * @param file The string constraint config for file name validation
	 * @param withoutExtension If present, requires paths to have no extension
	 * @param extension The string constraint config for extension validation
	 * @param ancestorOf The set of paths that constrained paths must be ancestors of
	 * @param descendantOf The set of paths that constrained paths must be descendants of
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If the ancestor of set is empty when present
	 * @throws IllegalArgumentException If the descendant of set is empty when present
	 * @throws IllegalArgumentException If both absolute and relative constraints are present
	 * @throws IllegalArgumentException If both without extension and extension constraints are present
	 */
	public URIPathConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(length, "Optional for 'length' constraint must not be null");
		Objects.requireNonNull(depth, "Optional for 'depth' constraint must not be null");
		Objects.requireNonNull(absolute, "Optional for 'absolute' constraint must not be null");
		Objects.requireNonNull(relative, "Optional for 'relative' constraint must not be null");
		Objects.requireNonNull(normalized, "Optional for 'normalized' constraint must not be null");
		Objects.requireNonNull(path, "Optional for 'path' constraint must not be null");
		Objects.requireNonNull(segment, "Optional for 'segment' constraint must not be null");
		Objects.requireNonNull(file, "Optional for 'file' constraint must not be null");
		Objects.requireNonNull(withoutExtension, "Optional for 'without extension' constraint must not be null");
		Objects.requireNonNull(extension, "Optional for 'extension' constraint must not be null");
		Objects.requireNonNull(ancestorOf, "Optional for 'ancestor of' constraint must not be null");
		Objects.requireNonNull(descendantOf, "Optional for 'descendant of' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (ancestorOf.isPresent() && ancestorOf.get().isEmpty()) {
			throw new IllegalArgumentException("Ancestor of set must not be empty when present");
		}
		
		if (descendantOf.isPresent() && descendantOf.get().isEmpty()) {
			throw new IllegalArgumentException("Descendant of set must not be empty when present");
		}
		
		if (absolute.isPresent() && relative.isPresent()) {
			throw new IllegalArgumentException("Both absolute and relative constraints cannot be present at the same time");
		}
		
		if (withoutExtension.isPresent() && extension.isPresent()) {
			throw new IllegalArgumentException("Both without extension and extension constraints cannot be present at the same time");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact path that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new URIPathConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The path that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withNotEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new URIPathConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of paths that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of paths that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withNotIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified length constraint configuration.<br>
	 *
	 * @param config The length constraint configuration
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withLength(@NonNull LengthConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'length' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, Optional.of(config), this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified depth constraint configuration.<br>
	 *
	 * @param config The depth constraint configuration
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withDepth(@NonNull DepthConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'depth' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, Optional.of(config), this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the absolute path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withAbsolute() {
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, Optional.of(Unit.INSTANCE), this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the relative path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withRelative() {
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, Optional.of(Unit.INSTANCE), this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withNormalized() {
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, Optional.of(Unit.INSTANCE), this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified path string constraint.<br>
	 *
	 * @param config The string constraint config for path validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withPath(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'path' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, Optional.of(config), this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified segment constraint.<br>
	 *
	 * @param config The string constraint config for segment validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withSegment(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'segment' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, Optional.of(config), this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified file name constraint.<br>
	 *
	 * @param config The string constraint config for file name validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withFile(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'file' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, Optional.of(config), this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the no-extension constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withWithoutExtension() {
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, Optional.of(Unit.INSTANCE), this.extension, this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified extension constraint.<br>
	 *
	 * @param config The string constraint config for extension validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withExtension(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'extension' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, Optional.of(config), this.ancestorOf, this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified ancestor-of constraint.<br>
	 *
	 * @param paths The collection of paths that constrained paths must be ancestors of
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withAncestorOf(@NonNull Collection<String> paths) {
		Objects.requireNonNull(paths, "Paths for 'ancestor of' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, Optional.of(Set.copyOf(paths)), this.descendantOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified descendant-of constraint.<br>
	 *
	 * @param paths The collection of paths that constrained paths must be descendants of
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withDescendantOf(@NonNull Collection<String> paths) {
		Objects.requireNonNull(paths, "Paths for 'descendant of' constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, Optional.of(Set.copyOf(paths)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull URIPathConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new URIPathConstraintConfig(this.equalTo, this.in, this.length, this.depth, this.absolute, this.relative, this.normalized, this.path, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchExtractedValue(value, this.length, String::length, "Length"),
			() -> ConstraintMatchers.matchExtractedValue(value, this.depth, IOMatchers::calculateUriPathDepth, "Depth"),
			() -> ConstraintMatchers.matchFlag(value, this.absolute, p -> p.startsWith("/"), "URI path '" + value + "' must be absolute"),
			() -> ConstraintMatchers.matchFlag(value, this.relative, p -> !p.startsWith("/"), "URI path '" + value + "' must be relative"),
			() -> ConstraintMatchers.matchFlag(value, this.normalized, IOMatchers::isUriPathNormalized, "URI path '" + value + "' must be normalized"),
			() -> IOMatchers.matchUriPathStringConfig(value, this.path),
			() -> IOMatchers.matchUriPathSegmentConfig(value, this.segment),
			() -> IOMatchers.matchUriPathFileNameConfig(value, this.file),
			() -> IOMatchers.matchUriPathWithoutExtension(value, this.withoutExtension),
			() -> IOMatchers.matchUriPathExtensionConfig(value, this.extension),
			() -> IOMatchers.matchUriPathAncestorOf(value, this.ancestorOf),
			() -> IOMatchers.matchUriPathDescendantOf(value, this.descendantOf),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
