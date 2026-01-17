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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.io.codec.constraint_new.ArrayConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.matcher.ConstraintMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for array constraints on array types.<br>
 * <p>
 *     This record stores the constraint values for {@link ArrayConstraint}.<br>
 *     It provides constraints for array equality, collection membership, and length validation.
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the array and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the list of arrays and
 *     the second value indicates negation (false=in, true=notIn).<br>
 *     A list is used instead of a set because arrays do not have proper hashCode implementations.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the array
 * @param equalTo The array equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The array collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum length constraint as a pair of (value, inclusive)
 * @param max The maximum length constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record ArrayConstraintConfig<T>(
	@NonNull Optional<Pair<T[], Boolean>> equalTo,
	@NonNull Optional<Pair<Set<T[]>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<T[]>> custom
) implements ConstraintConfig<T[]> {
	
	/**
	 * Constructs a new array constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The array equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The array collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum length constraint as a pair of (value, inclusive)
	 * @param max The maximum length constraint as a pair of (value, inclusive)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint list is empty when present
	 * @throws IllegalArgumentException If the minimum length is negative when present
	 * @throws IllegalArgumentException If the maximum length is negative when present
	 * @throws IllegalArgumentException If the minimum length is greater than the maximum length when both are present
	 * @throws IllegalArgumentException If min and max length are equal but at least one bound is exclusive when both are present
	 */
	public ArrayConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint list must not be empty when present");
		}
		
		if (min.isPresent() && min.get().getFirst() < 0) {
			throw new IllegalArgumentException("Min length must be non-negative when present, but got " + min.get().getFirst());
		}
		
		if (max.isPresent() && max.get().getFirst() < 0) {
			throw new IllegalArgumentException("Max length must be non-negative when present, but got " + max.get().getFirst());
		}
		
		if (min.isPresent() && max.isPresent() && min.get().getFirst() > max.get().getFirst()) {
			throw new IllegalArgumentException("Min length must be less than or equal to max length when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
		}
		
		if (min.isPresent() && max.isPresent() && min.get().getFirst().equals(max.get().getFirst()) && (!min.get().getSecond() || !max.get().getSecond())) {
			throw new IllegalArgumentException("Min and max length are equal but at least one bound is exclusive when both are present");
		}
	}
	
	/**
	 * Creates an unconstrained array configuration with no constraints applied.<br>
	 *
	 * @param <T> The element type of the array
	 * @return An unconstrained array constraint config
	 */
	public static <T> @NonNull ArrayConstraintConfig<T> unconstrained() {
		return new ArrayConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact array that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withEqualTo(T @NonNull [] value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new ArrayConstraintConfig<>(Optional.of(Pair.of(value.clone(), false)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The array that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withNotEqualTo(T @NonNull [] value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new ArrayConstraintConfig<>(Optional.of(Pair.of(value.clone(), true)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of arrays that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withIn(@NonNull Collection<T[]> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		Set<T[]> copies = new HashSet<>();
		for (T[] value : values) {
			copies.add(value.clone());
		}
		return new ArrayConstraintConfig<>(this.equalTo, Optional.of(Pair.of(copies, false)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of arrays that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withNotIn(@NonNull Collection<T[]> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		Set<T[]> copies = new HashSet<>();
		for (T[] value : values) {
			copies.add(value.clone());
		}
		return new ArrayConstraintConfig<>(this.equalTo, Optional.of(Pair.of(copies, true)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new array constraint config with the specified minimum length (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the minimum length constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withMinLength(int minLength) {
		return new ArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new array constraint config with the specified maximum length (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the maximum length constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withMaxLength(int maxLength) {
		return new ArrayConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(maxLength, true)), this.custom);
	}
	
	/**
	 * Creates a new array constraint config with the specified exact length.<br>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the exact length constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withExactLength(int exactLength) {
		return new ArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.custom);
	}
	
	/**
	 * Creates a new array constraint config with the specified length range (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the length range constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withLengthBetween(int minLength, int maxLength) {
		return new ArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ArrayConstraintConfig<T> withCustom(@NonNull Constraint<T[]> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new ArrayConstraintConfig<>(this.equalTo, this.in, this.min, this.max, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(T @NonNull [] value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo, Arrays::equals),
			() -> ConstraintMatchers.matchIn(value, this.in, Arrays::equals),
			() -> ConstraintMatchers.matchRange(value.length, this.min, this.max),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
