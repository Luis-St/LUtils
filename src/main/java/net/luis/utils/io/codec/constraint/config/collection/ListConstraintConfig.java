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

package net.luis.utils.io.codec.constraint.config.collection;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint.merged.collection.ListConstraint;
import net.luis.utils.io.codec.constraint_new.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.matcher.ConstraintMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for list constraints on list types.<br>
 * <p>
 *     This record stores the constraint values for {@link ListConstraint}.<br>
 *     It provides constraints for list equality, collection membership, and size validation.
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the list and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of lists and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 * <p>
 *     The size field stores size constraints using {@link SizeConstraintConfig}.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the list
 * @param equalTo The list equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The list collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param size The size constraint configuration
 * @param custom A custom constraint implementation
 */
public record ListConstraintConfig<T>(
	@NonNull Optional<Pair<List<T>, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<List<T>>, Boolean>> in,
	@NonNull Optional<SizeConstraintConfig> size,
	@NonNull Optional<Constraint<List<T>>> custom
) implements ConstraintConfig<List<T>> {
	
	/**
	 * Constructs a new list constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The list equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The list collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param size The size constraint configuration
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 */
	public ListConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(size, "Optional for 'size' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint set must not be empty when present");
		}
	}
	
	/**
	 * Creates an unconstrained list configuration with no constraints applied.<br>
	 *
	 * @param <T> The element type of the list
	 * @return An unconstrained list constraint config
	 */
	public static <T> @NonNull ListConstraintConfig<T> unconstrained() {
		return new ListConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact list that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withEqualTo(@NonNull List<T> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new ListConstraintConfig<>(Optional.of(Pair.of(List.copyOf(value), false)), this.in, this.size, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The list that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withNotEqualTo(@NonNull List<T> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new ListConstraintConfig<>(Optional.of(Pair.of(List.copyOf(value), true)), this.in, this.size, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of lists that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withIn(@NonNull Collection<List<T>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		Set<List<T>> copies = new HashSet<>();
		for (List<T> value : values) {
			copies.add(List.copyOf(value));
		}
		return new ListConstraintConfig<>(this.equalTo, Optional.of(Pair.of(copies, false)), this.size, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of lists that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withNotIn(@NonNull Collection<List<T>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		Set<List<T>> copies = new HashSet<>();
		for (List<T> value : values) {
			copies.add(List.copyOf(value));
		}
		return new ListConstraintConfig<>(this.equalTo, Optional.of(Pair.of(copies, true)), this.size, this.custom);
	}
	
	/**
	 * Creates a new list constraint config with the specified minimum size (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @return A new config with the minimum size constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withMinSize(int minSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withMinSize(minSize);
		return new ListConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.custom);
	}
	
	/**
	 * Creates a new list constraint config with the specified maximum size (inclusive).<br>
	 *
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the maximum size constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withMaxSize(int maxSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withMaxSize(maxSize);
		return new ListConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.custom);
	}
	
	/**
	 * Creates a new list constraint config with the specified exact size.<br>
	 *
	 * @param exactSize The exact size required
	 * @return A new config with the exact size constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withExactSize(int exactSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withExactSize(exactSize);
		return new ListConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.custom);
	}
	
	/**
	 * Creates a new list constraint config with the specified size range (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the size range constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withSizeBetween(int minSize, int maxSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withSizeBetween(minSize, maxSize);
		return new ListConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.custom);
	}
	
	/**
	 * Creates a new list constraint config with the specified size constraints.<br>
	 * <p>
	 *     This method applies the given {@link SizeConstraintConfig} to this config.
	 * </p>
	 *
	 * @param sizeConfig The size constraint configuration to apply
	 * @return A new config with the size constraints applied
	 * @throws NullPointerException If the size config is null
	 */
	public @NonNull ListConstraintConfig<T> withSize(@NonNull SizeConstraintConfig sizeConfig) {
		Objects.requireNonNull(sizeConfig, "Size config must not be null");
		return new ListConstraintConfig<>(this.equalTo, this.in, Optional.of(sizeConfig), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ListConstraintConfig<T> withCustom(@NonNull Constraint<List<T>> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new ListConstraintConfig<>(this.equalTo, this.in, this.size, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull List<T> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchExtractedValue(value, this.size, List::size, "Size"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
