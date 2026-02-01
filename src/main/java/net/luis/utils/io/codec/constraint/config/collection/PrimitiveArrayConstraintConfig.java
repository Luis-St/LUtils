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

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

/**
 * Configuration record for primitive array constraints.<br>
 * <p>
 *     Unlike {@link ArrayConstraintConfig} where the type parameter represents the element type,
 *     this config uses the type parameter to represent the entire primitive array type
 *     (e.g., {@code int[]}, {@code boolean[]}).<br>
 *     This is necessary because Java generics do not support primitive types directly.
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the array and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of arrays and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 * <p>
 *     The length field stores length constraints using {@link LengthConstraintConfig}.
 * </p>
 * <p>
 *     The equalityFunction and lengthExtractor are type-specific functions that handle
 *     the equality comparison and length extraction for different primitive array types.
 * </p>
 *
 * @author Luis-St
 *
 * @param <A> The primitive array type (e.g., {@code int[]}, {@code boolean[]})
 * @param equalTo The array equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The array collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param length The length constraint configuration
 * @param custom A custom constraint implementation
 * @param equalityFunction The function to compare two arrays for equality (e.g., {@code Arrays::equals})
 * @param lengthExtractor The function to extract the length from an array
 */
public record PrimitiveArrayConstraintConfig<A>(
	@NonNull Optional<Pair<A, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<A>, Boolean>> in,
	@NonNull Optional<LengthConstraintConfig> length,
	@NonNull Optional<Constraint<A>> custom,
	@NonNull BiPredicate<A, A> equalityFunction,
	@NonNull ToIntFunction<A> lengthExtractor
) implements ConstraintConfig<A> {
	
	/**
	 * Constructs a new primitive array constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The array equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The array collection constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param length The length constraint configuration
	 * @param custom A custom constraint implementation
	 * @param equalityFunction The function to compare two arrays for equality
	 * @param lengthExtractor The function to extract the length from an array
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 */
	public PrimitiveArrayConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(length, "Optional for 'length' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		Objects.requireNonNull(equalityFunction, "Equality function must not be null");
		Objects.requireNonNull(lengthExtractor, "Length extractor must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint set must not be empty when present");
		}
	}
	
	//region Factory methods
	
	/**
	 * Creates an unconstrained configuration for {@code int[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for int arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<int[]> intArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code long[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for long arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<long[]> longArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code double[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for double arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<double[]> doubleArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code float[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for float arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<float[]> floatArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code short[]} arrays.<br>
	 *
	 * @return An unconstrained primitive array constraint config for short arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<short[]> shortArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code byte[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for byte arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<byte[]> byteArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code boolean[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for boolean arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<boolean[]> booleanArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	
	/**
	 * Creates an unconstrained configuration for {@code char[]} arrays.<br>
	 * @return An unconstrained primitive array constraint config for char arrays
	 */
	public static @NonNull PrimitiveArrayConstraintConfig<char[]> charArray() {
		return new PrimitiveArrayConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Arrays::equals, arr -> arr.length
		);
	}
	//endregion
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact array that should be matched
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withEqualTo(@NonNull A value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new PrimitiveArrayConstraintConfig<>(Optional.of(Pair.of(value, false)), this.in, this.length, this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The array that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withNotEqualTo(@NonNull A value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new PrimitiveArrayConstraintConfig<>(Optional.of(Pair.of(value, true)), this.in, this.length, this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of arrays that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withIn(@NonNull Collection<A> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, Optional.of(Pair.of(new HashSet<>(values), false)), this.length, this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of arrays that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withNotIn(@NonNull Collection<A> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, Optional.of(Pair.of(new HashSet<>(values), true)), this.length, this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified minimum length (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the minimum length constraint applied
	 * @throws IllegalArgumentException If the minimum length is negative
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withMinLength(int minLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withMinLength(minLength);
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(newLength), this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified maximum length (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the maximum length constraint applied
	 * @throws IllegalArgumentException If the maximum length is negative
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withMaxLength(int maxLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withMaxLength(maxLength);
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(newLength), this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified exact length.<br>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the exact length constraint applied
	 * @throws IllegalArgumentException If the exact length is negative
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withExactLength(int exactLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withExactLength(exactLength);
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(newLength), this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified length range (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the length range constraint applied
	 * @throws IllegalArgumentException If minLength is greater than maxLength or either is negative
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withLengthBetween(int minLength, int maxLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withLengthBetween(minLength, maxLength);
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(newLength), this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified length constraints.<br>
	 * <p>
	 *     This method applies the given {@link LengthConstraintConfig} to this config.
	 * </p>
	 *
	 * @param lengthConfig The length constraint configuration to apply
	 * @return A new config with the length constraints applied
	 * @throws NullPointerException If the length config is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withLength(@NonNull LengthConstraintConfig lengthConfig) {
		Objects.requireNonNull(lengthConfig, "Length config must not be null");
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, Optional.of(lengthConfig), this.custom, this.equalityFunction, this.lengthExtractor);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull PrimitiveArrayConstraintConfig<A> withCustom(@NonNull Constraint<A> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new PrimitiveArrayConstraintConfig<>(this.equalTo, this.in, this.length, Optional.of(constraint), this.equalityFunction, this.lengthExtractor);
	}
	//endregion
	
	@Override
	public void validate(@NonNull A value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo, this.equalityFunction),
			() -> ConstraintValidators.validateIn(value, this.in, this.equalityFunction),
			() -> ConstraintValidators.validateExtractedValue(value, this.length, v -> this.lengthExtractor.applyAsInt(v), "Length"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
