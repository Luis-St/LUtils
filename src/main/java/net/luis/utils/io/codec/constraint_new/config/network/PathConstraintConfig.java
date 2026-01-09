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

package net.luis.utils.io.codec.constraint_new.config.network;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.Platform;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.*;

/**
 * Configuration record for file path constraints.<br>
 * <p>
 *     This record stores the constraint values for path codecs.<br>
 *     It includes base constraints, length constraints, depth constraints,
 *     and path-specific constraints for structure, components, and platform compatibility.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param minLength The minimum length constraint as a pair of (value, inclusive)
 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
 * @param minDepth The minimum depth constraint as a pair of (value, inclusive)
 * @param maxDepth The maximum depth constraint as a pair of (value, inclusive)
 * @param absolute If present, requires paths to be absolute
 * @param relative If present, requires paths to be relative
 * @param normalized If present, requires paths to be normalized
 * @param canonical If present, requires paths to be canonical
 * @param path The string constraint config for path validation
 * @param root The string constraint config for root component validation
 * @param parent The string constraint config for parent directory validation
 * @param segment The string constraint config for segment validation
 * @param file The string constraint config for file name validation
 * @param withoutExtension If present, requires paths to have no extension
 * @param extension The string constraint config for extension validation
 * @param ancestorOf The set of paths that constrained paths must be ancestors of
 * @param descendantOf The set of paths that constrained paths must be descendants of
 * @param validFor The platform to validate against
 * @param portable If present, requires paths to be portable across platforms
 * @param separator The platform whose separator should be used
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record PathConstraintConfig(
	@NonNull Optional<Pair<Path, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Path>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> minLength,
	@NonNull Optional<Pair<Integer, Boolean>> maxLength,
	@NonNull Optional<Pair<Integer, Boolean>> minDepth,
	@NonNull Optional<Pair<Integer, Boolean>> maxDepth,
	@NonNull Optional<Unit> absolute,
	@NonNull Optional<Unit> relative,
	@NonNull Optional<Unit> normalized,
	@NonNull Optional<Unit> canonical,
	@NonNull Optional<StringConstraintConfig> path,
	@NonNull Optional<StringConstraintConfig> root,
	@NonNull Optional<StringConstraintConfig> parent,
	@NonNull Optional<StringConstraintConfig> segment,
	@NonNull Optional<StringConstraintConfig> file,
	@NonNull Optional<Unit> withoutExtension,
	@NonNull Optional<StringConstraintConfig> extension,
	@NonNull Optional<Set<String>> ancestorOf,
	@NonNull Optional<Set<String>> descendantOf,
	@NonNull Optional<Platform> validFor,
	@NonNull Optional<Unit> portable,
	@NonNull Optional<Platform> separator,
	@NonNull Optional<Constraint<Path>> custom
) {
	
	/**
	 * An unconstrained path configuration with no constraints applied.<br>
	 */
	public static final PathConstraintConfig UNCONSTRAINED = new PathConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new path constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param minLength The minimum length constraint as a pair of (value, inclusive)
	 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
	 * @param minDepth The minimum depth constraint as a pair of (value, inclusive)
	 * @param maxDepth The maximum depth constraint as a pair of (value, inclusive)
	 * @param absolute If present, requires paths to be absolute
	 * @param relative If present, requires paths to be relative
	 * @param normalized If present, requires paths to be normalized
	 * @param canonical If present, requires paths to be canonical
	 * @param path The string constraint config for path validation
	 * @param root The string constraint config for root component validation
	 * @param parent The string constraint config for parent directory validation
	 * @param segment The string constraint config for segment validation
	 * @param file The string constraint config for file name validation
	 * @param withoutExtension If present, requires paths to have no extension
	 * @param extension The string constraint config for extension validation
	 * @param ancestorOf The set of paths that constrained paths must be ancestors of
	 * @param descendantOf The set of paths that constrained paths must be descendants of
	 * @param validFor The platform to validate against
	 * @param portable If present, requires paths to be portable across platforms
	 * @param separator The platform whose separator should be used
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If min length is greater than max length when both are present
	 * @throws IllegalArgumentException If min and max length are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If min depth is greater than max depth when both are present
	 * @throws IllegalArgumentException If min and max depth are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If the ancestor of set is empty when present
	 * @throws IllegalArgumentException If the descendant of set is empty when present
	 * @throws IllegalArgumentException If both absolute and relative constraints are present
	 * @throws IllegalArgumentException If both without extension and extension constraints are present
	 */
	public PathConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(minLength, "Optional for 'min length' constraint must not be null");
		Objects.requireNonNull(maxLength, "Optional for 'max length' constraint must not be null");
		Objects.requireNonNull(minDepth, "Optional for 'min depth' constraint must not be null");
		Objects.requireNonNull(maxDepth, "Optional for 'max depth' constraint must not be null");
		Objects.requireNonNull(absolute, "Optional for 'absolute' constraint must not be null");
		Objects.requireNonNull(relative, "Optional for 'relative' constraint must not be null");
		Objects.requireNonNull(normalized, "Optional for 'normalized' constraint must not be null");
		Objects.requireNonNull(canonical, "Optional for 'canonical' constraint must not be null");
		Objects.requireNonNull(path, "Optional for 'path' constraint must not be null");
		Objects.requireNonNull(root, "Optional for 'root' constraint must not be null");
		Objects.requireNonNull(parent, "Optional for 'parent' constraint must not be null");
		Objects.requireNonNull(segment, "Optional for 'segment' constraint must not be null");
		Objects.requireNonNull(file, "Optional for 'file' constraint must not be null");
		Objects.requireNonNull(withoutExtension, "Optional for 'without extension' constraint must not be null");
		Objects.requireNonNull(extension, "Optional for 'extension' constraint must not be null");
		Objects.requireNonNull(ancestorOf, "Optional for 'ancestor of' constraint must not be null");
		Objects.requireNonNull(descendantOf, "Optional for 'descendant of' constraint must not be null");
		Objects.requireNonNull(validFor, "Optional for 'valid for' constraint must not be null");
		Objects.requireNonNull(portable, "Optional for 'portable' constraint must not be null");
		Objects.requireNonNull(separator, "Optional for 'separator' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (minLength.isPresent() && maxLength.isPresent()) {
			if (minLength.get().getFirst().compareTo(maxLength.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + minLength.get().getFirst() + " > " + maxLength.get().getFirst());
			}
			if (minLength.get().getFirst().compareTo(maxLength.get().getFirst()) == 0 && (!minLength.get().getSecond() || !maxLength.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
		
		if (minDepth.isPresent() && maxDepth.isPresent()) {
			if (minDepth.get().getFirst().compareTo(maxDepth.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + minDepth.get().getFirst() + " > " + maxDepth.get().getFirst());
			}
			if (minDepth.get().getFirst().compareTo(maxDepth.get().getFirst()) == 0 && (!minDepth.get().getSecond() || !maxDepth.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
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
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact path that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withEqualTo(@NonNull Path value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new PathConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The path that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withNotEqualTo(@NonNull Path value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new PathConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of paths that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withIn(@NonNull Collection<Path> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of paths that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withNotIn(@NonNull Collection<Path> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum length constraint (inclusive).<br>
	 *
	 * @param minLength The minimum path length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withMinLength(int minLength) {
		return new PathConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum length constraint (inclusive).<br>
	 *
	 * @param maxLength The maximum path length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withMaxLength(int maxLength) {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, Optional.of(Pair.of(maxLength, true)), this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact length constraint.<br>
	 *
	 * @param exactLength The exact path length required
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withExactLength(int exactLength) {
		return new PathConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified length range constraint (inclusive).<br>
	 *
	 * @param minLength The minimum path length (inclusive)
	 * @param maxLength The maximum path length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new PathConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum depth constraint (inclusive).<br>
	 *
	 * @param minDepth The minimum path depth (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withMinDepth(int minDepth) {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(minDepth, true)), this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum depth constraint (inclusive).<br>
	 *
	 * @param maxDepth The maximum path depth (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withMaxDepth(int maxDepth) {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, Optional.of(Pair.of(maxDepth, true)), this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact depth constraint.<br>
	 *
	 * @param exactDepth The exact path depth required
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withExactDepth(int exactDepth) {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(exactDepth, true)), Optional.of(Pair.of(exactDepth, true)), this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified depth range constraint (inclusive).<br>
	 *
	 * @param minDepth The minimum path depth (inclusive)
	 * @param maxDepth The maximum path depth (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withDepthBetween(int minDepth, int maxDepth) {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(minDepth, true)), Optional.of(Pair.of(maxDepth, true)), this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the absolute path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withAbsolute() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, Optional.of(Unit.INSTANCE), this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the relative path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withRelative() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, Optional.of(Unit.INSTANCE), this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withNormalized() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, Optional.of(Unit.INSTANCE), this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the canonical path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withCanonical() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, Optional.of(Unit.INSTANCE), this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified path string constraint.<br>
	 *
	 * @param config The string constraint config for path validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withPath(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'path' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, Optional.of(config), this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified root constraint.<br>
	 *
	 * @param config The string constraint config for root component validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withRoot(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'root' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, Optional.of(config), this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified parent constraint.<br>
	 *
	 * @param config The string constraint config for parent directory validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withParent(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'parent' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, Optional.of(config), this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified segment constraint.<br>
	 *
	 * @param config The string constraint config for segment validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withSegment(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'segment' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, Optional.of(config), this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified file name constraint.<br>
	 *
	 * @param config The string constraint config for file name validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withFile(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'file' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, Optional.of(config), this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the no-extension constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withWithoutExtension() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, Optional.of(Unit.INSTANCE), this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified extension constraint.<br>
	 *
	 * @param config The string constraint config for extension validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withExtension(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'extension' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, Optional.of(config), this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified ancestor-of constraint.<br>
	 *
	 * @param paths The collection of paths that constrained paths must be ancestors of
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withAncestorOf(@NonNull Collection<String> paths) {
		Objects.requireNonNull(paths, "Paths for 'ancestor of' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, Optional.of(Set.copyOf(paths)), this.descendantOf, this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified descendant-of constraint.<br>
	 *
	 * @param paths The collection of paths that constrained paths must be descendants of
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withDescendantOf(@NonNull Collection<String> paths) {
		Objects.requireNonNull(paths, "Paths for 'descendant of' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, Optional.of(Set.copyOf(paths)), this.validFor, this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified platform validity constraint.<br>
	 *
	 * @param platform The platform to validate against
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withValidFor(@NonNull Platform platform) {
		Objects.requireNonNull(platform, "Platform for 'valid for' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, Optional.of(platform), this.portable, this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the portable path constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withPortable() {
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, Optional.of(Unit.INSTANCE), this.separator, this.custom);
	}
	
	/**
	 * Creates a new config with the specified separator platform constraint.<br>
	 *
	 * @param platform The platform whose separator should be used
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withSeparator(@NonNull Platform platform) {
		Objects.requireNonNull(platform, "Platform for 'separator' constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, Optional.of(platform), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PathConstraintConfig withCustom(@NonNull Constraint<Path> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new PathConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.minDepth, this.maxDepth, this.absolute, this.relative, this.normalized, this.canonical, this.path, this.root, this.parent, this.segment, this.file, this.withoutExtension, this.extension, this.ancestorOf, this.descendantOf, this.validFor, this.portable, this.separator, Optional.of(constraint));
	}
}
